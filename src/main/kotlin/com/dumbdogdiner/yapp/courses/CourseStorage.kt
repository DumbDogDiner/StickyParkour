package com.dumbdogdiner.yapp.courses

import com.dumbdogdiner.yapp.Base
import com.dumbdogdiner.yapp.YappParkourPlugin
import com.dumbdogdiner.yapp.utils.Utils

import java.io.File
import java.io.IOException

import org.bukkit.Location
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

/**
 * Wrapper class for storing course data.
 */
class CourseStorage : Base {
    private val file: File
    private val storage: FileConfiguration

    init {
        val plugin = YappParkourPlugin.instance

        file = File(plugin.dataFolder, "courses.yml")

        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource("courses.yml", false)
        }

        storage = YamlConfiguration()
        try {
            storage.load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    /**
     * Fetch all stored courses.
     */
    fun fetchCourses(): MutableList<Course> {
        val courses = mutableListOf<Course>()

        for (key in storage.getKeys(false)) {
            val course = Course()

            course.name = key
            storage.getConfigurationSection(key)?.getString("description")?.let { course.description = it }

            val checkpoints: List<Location> = fetchCourseCheckpoints(key) ?: continue

            checkpoints.forEach { course.addCheckpointAtLocation(it) }
            courses.add(course)
        }

        return courses
    }

    /**
     * Fetch a course's checkpoints from storage.
     */
    fun fetchCourseCheckpoints(id: String): List<Location>? {
        val section = storage.getConfigurationSection(id) ?: return null
        val res = mutableListOf<Location>()

        for (checkpoint in section.getStringList("checkpoints")) {
            val loc = Utils.deserializeLocation(checkpoint) ?: return null
            res.add(loc)
        }

        return res
    }

    /**
     * Save the provided courses to disk.
     */
    fun saveCourses(courses: MutableList<Course>) {
        courses.forEach { saveCourse(it, true) }
        storage.save(file)
        Utils.log("Saved ${courses.size} courses to disk.")
    }

    /**
     * Save a course to disk.
     */
    fun saveCourse(course: Course, skipSave: Boolean = false) {
        var section = storage.getConfigurationSection(course.name)

        if (section == null) {
            section = storage.createSection(course.name)
        }

        section.set("description", course.description)
        section.set("checkpoints", course.getCheckpoints().map { Utils.serializeLocation(it.getEndCheckpoint()) })

        if (skipSave) {
            return
        }
        storage.save(file)
        Utils.log("Saved course '${course.name}' to disk.")
    }

    /**
     * Delete a course from the config.
     */
    fun removeCourse(course: Course) {
        storage.set(course.name, null)
        Utils.log("Deleted course '${course.name}' from disk.")
    }

}
