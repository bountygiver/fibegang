package main;

import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;

import BackEnd.AudioPacket;

public class AudioReceiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatagramSocket serverSocket = new DatagramSocket(50005);

	    /**
	     * Formula for lag = (byte_size/sample_rate)*2
	     * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
	     * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
	     * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
	     */

	    byte[] receiveData = new byte[4000];
	    AudioPacket packet = new AudioPacket();

	    AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

	    boolean status = true;
		while (status  == true) {
	        DatagramPacket receivePacket = new DatagramPacket(receiveData,
	                receiveData.length);

	        serverSocket.receive(receivePacket);

	        ByteArrayInputStream baiss = new ByteArrayInputStream(
	                receivePacket.getData());

	        ais = new AudioInputStream(baiss, format, receivePacket.getLength());
	        toSpeaker(receivePacket.getData());
	}

}
