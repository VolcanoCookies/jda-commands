package net.volcano.jdacommands.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "emote")
class EmoteConfig {

	var notFoundEmoteId: String? = null
	var cooldownEmoteId: String? = null
	var noPermissionsEmoteId: String? = null

}