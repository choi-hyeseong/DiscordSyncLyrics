package audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lyrics.LyricsWriter
import lyrics.parser.LyricsParser
import java.net.URI

//음악 재생, 로드, 정지를 담당하는 클래스
class PlayerManager(
    private val playerManager: AudioPlayerManager,
    lyricsParser: LyricsParser //외부 api 호출용 parser는 주입
) {
    private val lyricsWriter: LyricsWriter = LyricsWriter(lyricsParser)
    private val musicQueue: MusicQueue = MusicQueue()

    private val audioPlayer: AudioPlayer by lazy {
        playerManager.createPlayer().apply {
            addListener(musicQueue) //큐 등록
            addListener(lyricsWriter) //가사 작성기
        }
    }

    init {
        AudioSourceManagers.registerRemoteSources(playerManager) //resource 등록
    }

    fun getHandler(): AudioPlayerHandler = AudioPlayerHandler(audioPlayer) //음악 재생에 필요한 Handler 제공

    //음악로드, 입력받은 문장을 파싱해서 검색
    fun loadMusic(music: String, callback : MusicSearchCallback) {
        val identifier = parseMusicArgument(music).also { println("Parsed Argument $it") }
        //Handler로 분리했는데, music 파라미터 때문에 매번 새로운 객체를 생성하는중.. 좀 아쉬운 부분
        //개선할려면 lyrics에 넣는게 아니라, lyrics는 onTrackStart시 id 받와와서 파싱하게 하면 좀더 괜찮음 리스너만 등록해줘도 되고
        playerManager.loadItem(identifier, AudioSearchResultHandler(audioPlayer, musicQueue, lyricsWriter, music, callback))
    }

    fun stopMusic() {
        audioPlayer.stopTrack()
    }

    private fun parseMusicArgument(argument: String): String {
        return runCatching {
            //http scheme이 아닌경우 ytsearch로
            if (URI(argument).scheme == null)
            //throw 해서 getOrElse에서 처리하게
                throw Exception("It's not URI Format")
            else
                argument
        }.getOrElse { "ytsearch:$argument" }

    }

}