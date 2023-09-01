package ch.rethinc.exposed.generator

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import java.time.Duration


private val config: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

private val httpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
    .dockerHost(config.getDockerHost())
    .sslConfig(config.getSSLConfig())
    .maxConnections(100)
    .connectionTimeout(Duration.ofSeconds(30))
    .responseTimeout(Duration.ofSeconds(45))
    .build()

var dockerClient = DockerClientImpl.getInstance(config, httpClient)




