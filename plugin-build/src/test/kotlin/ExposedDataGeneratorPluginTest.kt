
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test


class ExposedDataGeneratorPluginTest {
    @Test
    fun runGenerateDaos() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("ch.rethinc.exposed.generator")

        project.getTasks().getByName("generateDaos")
    }
}