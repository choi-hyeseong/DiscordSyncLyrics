import audio.PlayerManager
import audio.MusicQueue
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import command.PlayCommand
import lyrics.LyricsWriter
import lyrics.parser.LyricsParser
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

lateinit var jda : JDA //전역 JDA

fun main(args : Array<String>) {
   val lyricsParser = LyricsParser()


     //config 생성하는것 대신 arg로 토큰 받음
    if (args.isEmpty()) {
        println("토큰이 제공되지 않았습니다.")
        return
    }
    val builder = JDABuilder.create(args[0], GatewayIntent.values().toList())

    val musicQueue : MusicQueue = MusicQueue() //음악 재생용 Listener(Queue)
    val lyricsWriter : LyricsWriter = LyricsWriter(LyricsParser())
    val audioPlayerManager = DefaultAudioPlayerManager() //실질적인 오디오를 제공하는 매니저 (LavaPlayer)
    val playerManager = PlayerManager(audioPlayerManager, musicQueue, lyricsWriter) //큐에다가 음악을 집어넣고 재생, 정지할 수 있는 실질적 관리자

    //builder
    jda = builder.addEventListeners(PlayCommand(playerManager)).build().apply {
        //update command
    }.awaitReady() //셋업 완료시까지 await

}