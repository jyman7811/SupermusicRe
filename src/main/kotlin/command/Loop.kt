package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.Util
import org.example.controller.Controller
import org.example.controller.ControllerManager

class Loop : Command() {
    override val name: String = "loop"
    override val isOwnerOnly: Boolean = false
    override val description: String = "현재 재생 중인 노래를 반복합니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInVoice(event)
            .checkControllerExsit(event)
        val controller: Controller = ControllerManager.getController(event.guild!!, event.channel)
        if (controller.isLoop) {
            controller.isLoop = false
            event.reply("반복를 비활성화 하였습니다.").queue()
        } else  {
            controller.isLoop = true
            event.reply("반복을 활성화 하였습니다.")
        }
    }

}