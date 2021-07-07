package net.volcano.jdacommands.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "categories")
class CategoryConfig {

	lateinit var emojis: Map<String, String>

}