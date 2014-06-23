package BackEnd;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class AudioDecoderTask implements Runnable {
	
	private AudioPacket packet;

	public AudioDecoderTask(AudioPacket ap) {
		packet = ap;
	}
	
	public void run() {
		if (packet != null) {
			try {

		        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, packet.format);
		        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

		        sourceDataLine.open(packet.format);

		        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		        volumeControl.setValue(100.0f);

		        sourceDataLine.start();
		        sourceDataLine.open(packet.format);

		        sourceDataLine.start();

		        System.out.println("format? :" + sourceDataLine.getFormat());

		        sourceDataLine.write(packet.dataBytes, 0, packet.dataBytes.length);
		        System.out.println(packet.dataBytes.toString());
		        sourceDataLine.drain();
		        sourceDataLine.close();
		    } catch (Exception e) {
		        System.out.println("Not working in speakers...");
		        e.printStackTrace();
		    }
		}
	}
}
