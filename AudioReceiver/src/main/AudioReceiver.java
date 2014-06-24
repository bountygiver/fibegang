package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import BackEnd.AudioDecoderTask;
import BackEnd.AudioPacket;

public class AudioReceiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatagramSocket serverSocket;
		try {
			serverSocket = new DatagramSocket(50005);

		    /**
		     * Formula for lag = (byte_size/sample_rate)*2
		     * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
		     * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
		     * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
		     */

		    byte[] receiveData = new byte[24000];
		    AudioPacket packet, lastPacket;
		    lastPacket = packet = null;
	
		    AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
	        ExecutorService outputThread = Executors.newCachedThreadPool();
	
		    boolean status = true;
			while (status  == true) {
				if (packet != null) lastPacket = packet;
				 packet = new AudioPacket();
		        DatagramPacket receivePacket = new DatagramPacket(receiveData,
		                receiveData.length);
	
		        try {
					serverSocket.receive(receivePacket);
	
		        ByteArrayInputStream baiss = new ByteArrayInputStream(
		                receivePacket.getData());
	
		        packet.codecStream = new AudioInputStream(baiss, format, receivePacket.getLength());
		        packet.dataBytes = receivePacket.getData();
		        packet.format = format;
		        System.out.println("Data received! " + receiveData.length);
		        if (lastPacket != null) {
		        	AudioDecoderTask task = new AudioDecoderTask(lastPacket);
		        	outputThread.submit(task);
		        	//task.run();
		        }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		serverSocket.close();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
