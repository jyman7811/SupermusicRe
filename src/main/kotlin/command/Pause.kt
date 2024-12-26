package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.controller.Controller
import org.example.controller.ControllerManager

class Pause : Command() {
    override val name: String = "pause"
    override val isOwnerOnly: Boolean = false
    override val description: String = "현재 재생 중인 노래를 일시정지합니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInVoice(event)
            .checkControllerExsit(event)
        val controller: Controller = ControllerManager.getController(event.guild!!, event.messageChannel)
        val pauseStatus = controller.getPaused()

        return if (pauseStatus) {
            controller.setPaused(false)
            event.reply("다시 재생해줄게.").queue()
        } else {
            controller.setPaused(true)
            event.reply("일시정지했어.").queue()
        }
    }
}