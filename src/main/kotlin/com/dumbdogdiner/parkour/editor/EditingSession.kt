package com.dumbdogdiner.parkour.editor

import com.dumbdogdiner.parkour.Base
import com.dumbdogdiner.parkour.courses.Course
import com.dumbdogdiner.parkour.utils.Language
import com.dumbdogdiner.parkour.utils.SoundUtils
import com.dumbdogdiner.parkour.utils.Utils
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * A player's editing session.
 * TODO: are all these clones really necessary?
 */
class EditingSession(val player: Player, val course: Course, private val type: Type) : Base {
    private val editorTool = createItemTool()

    enum class Type {
        CREATE,
        DELETE,
        MODIFY
    }

    init {
        player.inventory.addItem(editorTool.clone())
        player.sendMessage(Language.createEditingSession)
        SoundUtils.info(player)
    }

    /**
     * End this editing session.
     */
    fun end(dropProgress: Boolean) {
        val tool = player.inventory.find { itemStack -> itemStack == editorTool.clone()  }
        if (tool != null) {
            player.inventory.remove(tool)
        }

        if (!dropProgress) {
            if (type == Type.CREATE) {
                courseManager.addCourse(course)
            } else {
                courseManager.updateCourse(course)
            }

            player.sendMessage(Language.courseSaved)
            SoundUtils.success(player)
        }
    }

    /**
     * Handle a checkpoint clicked event.
     */
    fun handleCheckpointClicked(e: PlayerInteractEvent) {
        val item = e.item ?: return
        val block = e.clickedBlock ?: return

        if (item != editorTool.clone()) {
            return
        }

        if (!Utils.isPressurePlate(block.type)) {
            player.sendMessage(Language.badBlock)
            SoundUtils.error(player)
            return
        }

        when (type) {
            Type.CREATE -> addCheckpoint(block.location)
            Type.DELETE -> removeCheckpoint(block.location)
            Type.MODIFY -> {}
        }
    }

    /**
     * Add a checkpoint to the course.
     */
    private fun addCheckpoint(loc: Location) {
        if (course.getCheckpoints().lastOrNull() != null && course.getCheckpoints().last().world != loc.world) {
            player.sendMessage(Language.badWorld)
            SoundUtils.error(player)
            return
        }

        val checkpoint: Location? = course.findCheckpoint(loc)
        if (checkpoint != null) {
            player.sendMessage(Language.checkpointExists)
            SoundUtils.error(player)
            return
        }

        course.addCheckpoint(loc)
        player.sendMessage(Language.checkpointCreated)
        SoundUtils.info(player)
    }

    /**
     * Remove a checkpoint from the course.
     */
    private fun removeCheckpoint(loc: Location) {
        val checkpoint = course.findCheckpoint(loc)

        if (checkpoint == null) {
            player.sendMessage(Language.checkpointNotFound)
            SoundUtils.error(player)
            return
        }

        course.removeCheckpoint(checkpoint)
        player.sendMessage(Language.checkpointRemoved)
        SoundUtils.info(player)
    }

    /**
     * Exit the editor when an item is dropped.
     *
     * If the player has added less than two checkpoints i.e. no start or end point, the
     * editor will drop the current progress and inform the user it is doing such.
     */
    fun handleDropEvent(e: PlayerDropItemEvent) {
        if (
            e.itemDrop.itemStack != editorTool
        ) {
            return
        }

        var dropProgress = false

        // If there isn't a start and an endpoint, discard progress.
        if (course.getCheckpoints().size < 2) {
            player.sendMessage(Language.badLength)
            SoundUtils.error(player)
            dropProgress = true
        }

        editingSessionManager.endEditingSession(this, dropProgress)
        e.itemDrop.remove()
    }

    /**
     * Handle the editor dying. Why this would ever happen I can't say.
     */
    fun handleEditorDeath(e: PlayerDeathEvent) {
        e.itemsToKeep.add(editorTool.clone())
        e.drops.remove(editorTool.clone())
    }

    companion object {
        /**
         * Create a carbon copy of the editor tool used.
         * TODO: See if ItemStack.clone() would be more efficient.
         */
        fun createItemTool(): ItemStack {
            val editorTool = ItemStack(Material.BLAZE_ROD, 1)

            val meta = editorTool.itemMeta
            meta.setDisplayName(Utils.colorize("&r&6&lCourse Editor"))
            meta.lore = mutableListOf("A magical glowing stick! oWO!!", "Use this to create parkour courses, or return them unto the void.")

            editorTool.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
            editorTool.itemMeta = meta

            return editorTool
        }
    }
}
