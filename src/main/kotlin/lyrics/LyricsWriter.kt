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

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        CoroutineScope(Dispatchers.IO).launch {
            val lyrics : Lyrics? = async { lyricsParser.parse(lyricsQueue.poll()) }.await() //lyrics 가져오기 (coroutine) await 되어서 가져올대까지 기다림
            //매칭되는 가사가 없을경우 return
            if (lyrics == null || lyrics.syncedLyrics.isEmpty())
                return@launch

            message = jda.getTextChannelsByName("일반",true)[0].sendMessage(lyrics.syncedLyrics.poll().lyrics).complete()
            job = async {
                //가사 불러오기용 쓰레드
                //코드는 지저분하지만 일단 돌아만 가게..
                while (true) {
                    try {
                        // TODO send empty message 해결
                        if (lyrics.syncedLyrics.peek().time <= player!!.playingTrack?.position?.div(1000L)!!)
                            message = message!!.editMessage(lyrics.syncedLyrics.poll().lyrics).complete()
                        Thread.sleep(500)
                    }
                    catch (e : InterruptedException) {
                        break
                    }
                }
            }
        }

    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        job?.cancel() //코루틴 종료
        message?.delete()?.queue() //종료시 삭제
    }
}