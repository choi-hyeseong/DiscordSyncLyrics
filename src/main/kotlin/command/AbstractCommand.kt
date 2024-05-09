package command

//onMessageReceived를 사용하는 커맨드
abstract class AbstractCommand(private val command : String) : ListenerAdapter() {

    abstract fun onCommand(event: MessageReceivedEvent) 

    override fun onMessageReceived(event: MessageReceivedEvent) {
        //bot의 메시지인경우 리턴
        if (event.author.isBot)
            return
        //해당 커맨드가 아닌경우 리턴
        if (!message.startsWith(command)) 
            return
        onCommand(event)
    }

    // arg값 가져오는 함수
    protected fun getArgument(event: MessageReceivedEvent, index : Int) : String? {
        val splitArgument = getArguments(event)
        return if (splitArgument.size <= index) //out of index 
            null
        else
            splitArgument[index]
    }

    // arg 합쳐서 가져오기
    protected fun getConcatedArgument(event : MessageReceivedEvent, from : Int) : String? {
        val splitArgument = getArguments(event)
        // out of index
        if (splitArgument.size <= from)
            return null
        
        val builder : StringBuilder = StringBuilder()
        // argument concat
        splitArgument.forEachIndexed { index, argument -> 
            if (index >= from)
                builder.append(argument).append(" ")
        }
        // remove space of end
        return builder.toString().trim()
    }

    // 분리 arg값 가져오기
    private fun getArguments(event: MessageReceivedEvent) : List<String> {
        var message = event.message.contentStripped //string으로 strip된 메시지
        message = message.replace(command, "").trim() //커맨드 제거
        return message.split(" ").toList()
    }

}