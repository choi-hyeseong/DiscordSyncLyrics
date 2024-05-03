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
    fun loadMusic(music: String) {
        val identifier = parseMusicArgument(music).also { println("Parsed Argument $it") }
        playerManager.loadItem(identifier, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                lyricsWriter.addLyrics(music) //먼저 writer에 추가해줘야 함.
                //일단 로드되면 스타트 해보고, noInterrupt이기 때문에 이미 재생중이라 재생 안되면 큐에 등록
                if (!audioPlayer.startTrack(track, true))
                    musicQueue.addMusic(track)


            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                lyricsWriter.addLyrics(music)
                val firstTrack = playlist.tracks.first()
                if (!audioPlayer.startTrack(firstTrack, true))
                    musicQueue.addMusic(firstTrack)

            }

            override fun noMatches() {
                println("not matched")
            }

            override fun loadFailed(exception: FriendlyException?) {
                println("load failed $exception")
            }
        })
    }

    fun stopMusic() {
        audioPlayer.stopTrack()
    }

    private fun parseMusicArgument(argument: String): String {
        //http scheme이 아닌경우 ytsearch로
        return try {
            if (URI(argument).scheme == null)
                throw Exception("It's not URI Format")
            else
                argument
        }
        catch (e: Exception) {
            "ytsearch:$argument"
        }
    }


}