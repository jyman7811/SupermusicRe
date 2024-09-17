package org.example.handler

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.example.controller.ControllerManager

class LeaveGuildHandler : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val channel = event.channelLeft ?: return


        if (channel.members.size == 0) return ControllerManager.destroy(event.guild.idLong, "모든 사용자가 나가서 연결을 해제 하였습니다!")

        /**
         * 채널에 봇 혼자 남았을 때 컨트롤러를 삭제합니다.
         */
        if (channel.members.size == 1 && channel.members[0].user == event.jda.selfUser) {
            ControllerManager.destroy(event.guild.idLong, "모든 사용자가 나가서 연결을 해제 하겠습니다!")
        }
    }
}