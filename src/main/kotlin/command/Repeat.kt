package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.controller.Controller
import org.example.controller.ControllerManager
import org.example.controller.Repeat

class Repeat : Command() {
    override val name: String = "repeat"
    override val isOwnerOnly: Boolean = false
    override val description: String = "반복 기능을 활성화합니다. 한 번 사용하면 전체 반복, 두 번은 한 곡 반복입니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInVoice(event)
            .checkControllerExsit(event)
        val controller: Controller = ControllerManager.getController(event.guild!!, event.messageChannel)
        when (controller.repeat.status) {
            0 -> {
                controller.repeat = Repeat.REPEAT
                event.reply("전체 트랙을 반복합니다.").queue()
            }
            1 -> {
                controller.repeat = Repeat.REPEAT_ONE
                event.reply("한 곡만 반복합니다.").queue()
            }
            2 -> {
                controller.repeat = Repeat.NONE
                event.reply("반복를 비활성화 하였습니다.").queue()
            }
        }
    }

}