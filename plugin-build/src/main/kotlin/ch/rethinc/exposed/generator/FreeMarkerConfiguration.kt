package ch.rethinc.exposed.generator

import freemarker.template.Configuration

object FreeMarkerConfiguration {
    val fromClassPath: Configuration
        get() {
            return Configuration(Configuration.VERSION_2_3_31)
                .apply {
                    setClassForTemplateLoading(javaClass, "/templates")
                }
        }
}