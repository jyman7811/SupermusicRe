package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Remove : Command() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val isOwnerOnly: Boolean
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")
    override val options: Array<OptionData>
        get() = TODO("Not yet implemented")

    override fun run(event: CommandEvent) {
        TODO("Not yet implemented")
    }


    // 번호가 아닌 인터랙션 컴포넌트를 이용해 제거 가능하도록 구현 예정
}