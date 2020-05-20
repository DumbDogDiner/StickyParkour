package com.dumbdogdiner.parkour.utils

import com.dumbdogdiner.parkour.ParkourPlugin

object Configuration {
    private val plugin = ParkourPlugin.instance

    fun loadDefaultConfig() {
        val config = plugin.config

        config.options()
            .header("""
                ${plugin.description.name} v${plugin.description.version}
                
                Parkour Main Configuration File
                
                Developer(s): ${plugin.description.authors}\n
            """.trimIndent())

        config.addDefault("disableSound", false)

        // BEGIN LINGUISTICS
        config.addDefault(path + "prefix", "&d&lPawkour &r&8» &b")
        config.addDefault(path + "noPermission", "&cYou do not have permission to run this command.")
        config.addDefault(path + "noConsole", "&cThis command must be run as a player.")
        config.addDefault(path + "unknownSubCommand", "&cUnknown or invalid subcommand.")
        config.addDefault(path + "invalidCommandUsage", "&cInvalid usage: %USAGE%")

        // Session linguistics
        config.addDefault(path + "newBestTime", "Congratulations! You scored a new best time of &d&l%TIME%!")

        // Command-specific linguistics.
        config.addDefault(path + "listCount", "There are currently %COUNT% courses.")

        // Editing linguistics
        config.addDefault(path + "createEditingSession", "Right-click on pressure plates with the Course Editor to add them to the course!")
        config.addDefault(path + "courseNotFound", "&cThat course could not be found.")
        config.addDefault(path + "checkpointNotFound", "&cCould not find that checkpoint.")
        config.addDefault(path + "checkpointExists", "&cA checkpoint for this course already exists at this pressure plate!")
        config.addDefault(path + "checkpointCreated", "Checkpoint added!")
        config.addDefault(path + "checkpointRemoved", "Checkpoint removed.")
        config.addDefault(path + "badWorld", "Trans-dimensional parkour is &c&lILLEGAL&r. All checkpoints must be in the same world.")
        config.addDefault(path + "badLength", "&cInsufficient length! &bMake sure a course has at least 2 checkpoints before exiting.")
        config.addDefault(path + "badBlock", "&cThis is not a pressure plate!")
        config.addDefault(path + "courseSaved", "Saved course!")

        config.options().copyDefaults(true)
        plugin.saveConfig()
        plugin.reloadConfig()
    }
    private const val path = "language."
}
