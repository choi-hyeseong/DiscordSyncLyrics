package lyrics

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import jda
import kotlinx.coroutines.*
import lyrics.entity.Lyrics
import lyrics.parser.LyricsParser
import net.dv8tion.jda.api.entities.Message
import java.util.LinkedList
import java.util.Queue
import kotlin.concurrent.thread

class LyricsWriter(private val lyricsParser: LyricsParser) : AudioEventAdapter() {

    private var job : Job? = null //코루틴
    private var message : Message? = null //가사 포함된 메시지
    private val lyricsQueue : Queue<String> = LinkedList() //검색한 lyrics 저장용 queue

    fun addLyrics(music : String) {
        // 검색한 단어 큐에 저장. 근데 player재생시 youtube id 나오는데 거기서 제목 추출하는게 더 좋을듯
        lyricsQueue.add(music)
    }

    //실제 가사 출력하는 함수 - 무한루프
    private suspend fun runLyricsPrint(audioPlayer: AudioPlayer, lyrics: Lyrics) {
        val syncedLyrics = lyrics.syncedLyrics
        val channel = jda.getTextChannelsByName("일반",true)[0]
        //제일 앞의 가사 출력
        message = channel.sendMessage(syncedLyrics.poll().text).complete()
        while (true) {
            val isPrintSuccess = runCatching {
                // TODO send empty message 해결
                val nextLyricsTime = syncedLyrics.peek().time
                val playingTime = audioPlayer.playingTrack!!.position.div(1000L) //초단위?
                if (nextLyricsTime <= playingTime)
                    message = message!!.editMessage(syncedLyrics.poll().text).complete()
                    
                Thread.sleep(500)
            }.isSuccess //오류 없이 출력이 되었는지 확인
            if (!isPrintSuccess)
                break //실패시 while문 종료
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack?) {
        CoroutineScope(Dispatchers.IO).launch {
            val lyrics : Lyrics? = async { lyricsParser.parse(lyricsQueue.poll()) }.await() //lyrics 가져오기 (coroutine) await 되어서 가져올대까지 기다림
            //매칭되는 가사가 없을경우 return
            if (lyrics == null || lyrics.syncedLyrics.isEmpty())
                return@launch
            job = async { runLyricsPrint(player, lyrics) }
        }

    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        job?.cancel() //코루틴 종료
        // TODO unknown message 해결 - 가사 못찾는 경우 발생함
        message?.delete()?.queue() //종료시 삭제
    }
}