package ch.rethinc.exposed.generator

import com.jetbrains.exposed.gradle.plugin.ExposedGradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ExposedDaoGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(ExposedGradlePlugin::class.java)
    }
}
