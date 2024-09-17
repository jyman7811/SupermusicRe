package org.example.command

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import org.example.controller.Controller
import org.example.controller.ControllerManager

class CommandEvent(api: JDA, responseNumber: Long, interaction: SlashCommandInteraction) : SlashCommandInteractionEvent(api, responseNumber, interaction) {
     val controller: Controller?
        get() {
            return this.guild?.let { ControllerManager.getController(it, this.channel) }
        }

    /**
     * reply와 동시에 주어진 시간 뒤 메시지를 삭제합니다.
     * queue()가 불필요합니다.
     * @param content 보낼 메시지입니다.
     * @param deleteIn 주어진 숫자(초 단위) 뒤에 메시지를 삭제합니다.
     */
    fun reply(content: String, deleteIn: Int): ReplyCallbackAction {
        super.reply(content)
        TODO("Deleting Message")
    }
}