package ch.rethinc.exposed.generator

import com.github.dockerjava.api.command.WaitContainerResultCallback
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.command.LogContainerResultCallback

class DatabaseMigration : AutoCloseable {

    private var databaseContainerId: String? = null

    fun startMigratedDatabase(configuration: DatabaseConfiguration) = synchronized(this) {
        if (this.databaseContainerId != null) {
            throw IllegalStateException("Container has never been stopped")
        }

        val exposedPort = ExposedPort.tcp(configuration.databasePort)
        val portBindings = Ports()
        portBindings.bind(exposedPort, Ports.Binding.bindPort(configuration.databasePort))
        val databaseContainer = dockerClient.createContainerCmd(configuration.databaseImage)
            .withEnv(
                mapOf(
                    "POSTGRES_DB" to configuration.databaseName,
                    "POSTGRES_USER" to configuration.databaseUser,
                    "POSTGRES_PASSWORD" to configuration.databasePassword
                ).toEnvList()
            ).withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings))
            .exec()
        this.databaseContainerId = databaseContainer.id
        dockerClient.startContainerCmd(databaseContainer.id).exec()
        val databaseContainerWaitCallback = WaitContainerResultCallback()
        dockerClient.waitContainerCmd(databaseContainer.id).exec(databaseContainerWaitCallback)
        databaseContainerWaitCallback.awaitStarted()
        println("database container started")

        var migrationContainerId: String? = null
        try {
            val migrationContainer = dockerClient.createContainerCmd(configuration.migrationImage).withEnv(
                mapOf(
                    "DB_HOST" to "localhost",
                    "DB_PORT" to configuration.databasePort.toString(),
                    "DB_DATABASE" to configuration.databaseName,
                    "DB_USER" to configuration.databaseUser,
                    "DB_PASSWORD" to configuration.databasePassword
                ).toEnvList()
            )
                .withHostConfig(HostConfig.newHostConfig().withNetworkMode("host"))
                .exec()
            migrationContainerId = migrationContainer.id
            dockerClient.startContainerCmd(migrationContainer.id).exec()
            val migrationWaitCallback = WaitContainerResultCallback()
            dockerClient.waitContainerCmd(migrationContainer.id).exec(migrationWaitCallback)
            val statusCode = migrationWaitCallback.awaitCompletion().awaitStatusCode()
            if (statusCode != 0) {
                val logCallback = object : LogContainerResultCallback() {
                    override fun onNext(item: Frame?) {
                        println(item)
                    }
                }
                dockerClient.logContainerCmd(migrationContainer.id).withStdErr(true).withStdOut(true).withTail(10).exec(logCallback)
                logCallback.awaitCompletion()
                throw Exception("Migration exited with non 0 status code ($statusCode)")
            }
        } finally {
            migrationContainerId?.let {
                dockerClient.removeContainerCmd(it).exec()
            }
        }
    }

    private fun Map<String, String>.toEnvList(): List<String> =
        this.entries.map { "${it.key}=${it.value}" }

    override fun close() {
        this.databaseContainerId?.let {
            dockerClient.stopContainerCmd(it).exec()
            dockerClient.removeContainerCmd(it).exec()
        }
        this.databaseContainerId = null
    }
}