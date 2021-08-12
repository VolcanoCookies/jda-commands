package net.volcano.jdacommands.model.command

class Help(
	val usage: String?,
	val description: String?,
	val examples: Array<String>,
	val details: String?,
	val category: String?,
	val emoji: String?,
	val permissions: Array<String>
) {

	companion object {

		class HelpBuilder {

			var usage: String? = null
			var description: String? = null
			lateinit var examples: Array<String>
			var details: String? = null
			lateinit var category: String
			lateinit var emoji: String
			lateinit var permissions: Array<String>

			fun build(): Help {
				return Help(
					usage,
					description,
					examples,
					details,
					category,
					emoji,
					permissions
				)
			}
		}

	}

}
