package org.example.audio

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager
import java.lang.RuntimeException

class GuildMusicManager {
    private val controllers = HashMap<Long, Controller>()

    fun makeController(manager: AudioManager, guildId: Long, channel: MessageChannel): Controller {
        if (controllers.containsKey(guildId)) return controllers[guildId]!!
        val player = ControllerManager.playerManager.createPlayer()
        player.volume = 35
        manager.sendingHandler = AudioPlayerSendHandler(player)
        val newController = Controller(player, manager, channel)
        controllers[guildId] = newController
        player.addListener(newController)
        return newController
    }

    fun destroy(guildId: Long, msg: String) {
        try {
            controllers[guildId]!!.fuck(msg)
            controllers.remove(guildId)
        } catch (e: RuntimeException) {
            return
        }
    }

    fun destroy(controller: Controller, msg: String) {
        try {
            destroy(controllers.filterValues {it == controller}.keys.first(), msg)
        } catch (e: RuntimeException) {
            return
        }
    }

    fun count(): Int {
        return controllers.size
    }

    fun isExist(guildId: Long): Boolean {
        return controllers.containsKey(guildId)
    }
}