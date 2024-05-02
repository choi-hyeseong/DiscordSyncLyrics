package lyrics.parser

import com.google.gson.Gson
import lyrics.dto.LyricsResponseDTO
import lyrics.entity.Lyrics
import okhttp3.OkHttpClient
import okhttp3.Request

class LyricsParser {

    //곡 제목 ex) ive accendio 등을 검색했을때 맨 처음 나오는 가사의 json을 파싱. 정확도가 떨어질 수 있음.

    val url = "https://lrclib.net/api/search?q="
    val okHttpClient = OkHttpClient()
    val gson = Gson()

    // suspend fun으로 해서 다른 쓰레드에서 수행할 수 있게 하는게 맞을 거 같음.
    fun parse(identifier : String) : Lyrics? {
        val requestBuilder = Request.Builder().url(url.plus(identifier)).get() //get 요청 request 생성
        val request = requestBuilder.build()

        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            //성공적으로 요청 됐을경우
            return try {
                //우선 DTO로 파싱
                val responseDTO = gson.fromJson(response.body?.string(), Array<LyricsResponseDTO>::class.java)?.get(0)
                responseDTO?.toLyrics()
            } catch (e : Exception) {
                println("요청에 실패하였습니다. $e")
                null
            }
        }
        else
            return null
    }

}