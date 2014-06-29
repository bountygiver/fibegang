package AudioReceiverStuffs;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioDecoderTask implements Runnable {
	
	private AudioPacket packet;

	public AudioDecoderTask(AudioPacket ap) {
		packet = ap;
	}
	
	public void run() {
		if (packet != null) {
			try{
	              Log.d("AudioReceiver", "Play start!");
	              AudioTrack audioTrack = new  AudioTrack(AudioManager.STREAM_MUSIC, packet.sampleRate, packet.channelConfig, packet.audioFormat, packet.bufferSize, AudioTrack.MODE_STREAM);
	              audioTrack.write(packet.dataBytes, 64, packet.dataBytes.length - 64);
	              audioTrack.release();
	              Log.d("AudioReceiver", "Play complete!");

			} catch(Throwable t){
				Log.d("Audio","Playback Failed");
			}
	    }
	}
}
