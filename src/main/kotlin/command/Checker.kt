package org.example.command

import org.example.exception.*

object Checker  {
    fun checkIsInGuild(event: CommandEvent) : Checker {
        event.member?: throw NotInGuildException()
        return this
    }

    /**
     * 음성 채널에 있는지 확인합니다.
     * 서버에 속해 있는지 확인하는 역할도 겸합니다.
     */
    fun checkIsInVoice(event: CommandEvent) : Checker {
        checkIsInGuild(event)
        val voiceState = event.member!!.voiceState ?: throw NotInVoiceException()

        if (!voiceState.inAudioChannel()) throw NotInVoiceException()
        return this
    }


    /**
     * 컨트롤러가 존재하는지 확인합니다.
     * 컨트롤러가 없는 원인은 아직 봇이 채널에 접속하지 않았거나 명령어가 사용된 텍스트 채널이 다르기 때문일 수 있습니다.
     */
    fun checkControllerExsit(event: CommandEvent) : Checker {
        event.controller?: throw ControllerNotExsitException()

        return this
    }

    fun checkIsPlaying(event: CommandEvent) : Checker {
        checkControllerExsit(event)
        return this
    }

    fun checkIsOptionFilled(event: CommandEvent, requiredOption: String, reply: String) : Checker {
        val option = event.getOption(requiredOption) ?: throw OptionIsEmptyException(reply)
        if (option.asString.isEmpty()) throw OptionIsEmptyException(reply)

        return this
    }

    fun checkIsOptionFilled(event: CommandEvent, requiredOptions: Array<String>, reply: String) : Checker {
        requiredOptions.forEach {
            val option = event.getOption(it) ?: throw OptionIsEmptyException(reply)
            if (option.asString.isEmpty()) throw OptionIsEmptyException(reply)
        }

        return this
    }
}