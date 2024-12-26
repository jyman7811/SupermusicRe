package org.example.handler

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.example.controller.ControllerManager

class LeaveGuildHandler : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val channel = event.channelLeft ?: return


        val members = channel.members
        val userMembers = members.filter {!it.user.isBot}
        /**
         * 채널에 봇 혼자 남았을 때 컨트롤러를 삭제합니다.
         */

        if (userMembers.isEmpty()) {
            ControllerManager.destroy(event.guild.idLong, "아무도 없는 모양이네.")
        }
    }
}