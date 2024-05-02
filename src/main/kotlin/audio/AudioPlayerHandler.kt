package audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioPlayerHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {

    private val audioFrame: MutableAudioFrame = MutableAudioFrame()
    private val buffer : ByteBuffer = ByteBuffer.allocate(1024)

    init {
        audioFrame.setBuffer(buffer)
    }

    override fun canProvide(): Boolean {
        return audioPlayer.provide(audioFrame)
    }

    override fun provide20MsAudio(): ByteBuffer? {
        return this.buffer.flip()
    }

    override fun isOpus(): Boolean {
        return true
    }


}