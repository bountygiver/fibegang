package com.example.androidaudioreceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import AudioReceiverStuffs.AudioDecoderTask;
import AudioReceiverStuffs.AudioPacket;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {
	
	ToggleButton tb;
	boolean listening;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ListeningToggle(View view) {
		tb = (ToggleButton) findViewById(R.id.listenToggle);
		listening = tb.isChecked();
		if (listening == true) {
			startStreaming();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void startStreaming() {
		
		
		Thread streamThread = new Thread(new Runnable() {

	        @Override
	        public void run() {
		DatagramSocket serverSocket;
		try {
			serverSocket = new DatagramSocket(50005);

		    /**
		     * Formula for lag = (byte_size/sample_rate)*2
		     * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
		     * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
		     * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
		     */

		    byte[] receiveData = new byte[4000];
		    AudioPacket packet, lastPacket;
		    lastPacket = packet = null;
	
	        ExecutorService outputThread = Executors.newCachedThreadPool();
	
			while (listening  == true) {
				if (packet != null) lastPacket = packet;
				 packet = new AudioPacket();
				 packet.sampleRate = 8000;
				 packet.channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
				 packet.audioFormat = AudioFormat.ENCODING_PCM_16BIT;
				 packet.bufferSize = receiveData.length;
		        DatagramPacket receivePacket = new DatagramPacket(receiveData,
		                receiveData.length);
	
		        try {
					serverSocket.receive(receivePacket);
		        packet.dataBytes = receivePacket.getData();
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
		});
		streamThread.start();
	}

}
