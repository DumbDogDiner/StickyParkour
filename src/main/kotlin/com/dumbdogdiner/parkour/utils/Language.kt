package com.dumbdogdiner.parkour.utils

import com.dumbdogdiner.parkour.ParkourPlugin

object Language {
    private const val path = "language"
    private val plugin = ParkourPlugin.instance
    private val config = plugin.config

    private fun get(path: String) = Utils.colorize(config.getString(path)!!)
    private fun getList(path: String) = config.getStringList(path).map { Utils.colorize(it) }


    val prefix
        get() = get("${path}.prefix")

    val noPermission
        get() = "$prefix${get("$path.noPermission")}"
    val noConsole
        get() = "$prefix${get("$path.noConsole")}"
    val unknownSubCommand
        get() = "$prefix${get("${path}.unknownSubCommand")}"
    val invalidCommandUsage
        get() = "$prefix${get("${path}.invalidCommandUsage")}"

    val listCount
        get() = "$prefix${get("${path}.listCount")}"

    val createEditingSession
        get() = "$prefix${get("${path}.createEditingSession")}"
    val checkpointNotFound
        get() = "$prefix${get("${path}.checkpointNotFound")}"
    val checkpointExists
        get() = "$prefix${get("${path}.checkpointExists")}"
    val checkpointCreated
        get() = "$prefix${get("${path}.checkpointCreated")}"
}