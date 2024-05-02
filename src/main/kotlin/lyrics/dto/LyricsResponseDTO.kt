package lyrics.dto

import lyrics.entity.Lyrics
import lyrics.entity.SyncedLyrics
import java.text.SimpleDateFormat
import java.util.*

data class LyricsResponseDTO(
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Int,
    val instrumental: Boolean,
    val plainLyrics: String,
    val syncedLyrics: String
) {

    //매우 지저분한 코드.. 추후 수정예정 파싱 과정이 여기 다 있어서 좋지 않음
    fun toLyrics(): Lyrics = Lyrics(
        id,
        name,
        trackName,
        artistName,
        albumName,
        duration,
        instrumental,
        plainLyrics,
        LinkedList(syncedLyrics.split("\n").filter { it.isNotBlank() }.map {
            //"[00:06.18] ~"형식으로 되어 있음
            val split = it.split("]") //]기준으로 자름
            val time = split[0].replace("[", "") //00:06:18
                .split(".")[0] //00:06만 남음
                .split(":") //00과 06으로 분리
            val lyric = split[1].trim()
            SyncedLyrics(time[0].toLong() * 60 + time[1].toLong(), lyric)
        })
    )
}
