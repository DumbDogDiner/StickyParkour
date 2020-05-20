package com.dumbdogdiner.parkour.session

import com.dumbdogdiner.parkour.courses.Course
import org.bukkit.OfflinePlayer

class StoredSession {
    lateinit var player: OfflinePlayer
    lateinit var course: Course

    var time: Double = -1.0
}
