import ossaudiodev
import socket
import audio_config
import util

stream = ossaudiodev.open("r")

SEND_ADDR  = ('23.239.9.164', 51453)

AUD_CHANNEL = int(input("Channel = "))

stream.setparameters(audio_config.SAMPLE_FORMAT,
                     audio_config.N_CHANNELS,
                     audio_config.SAMPLE_RATE)

offset = 0

while True:
	data   = stream.read(audio_config.READ_SIZE)
	offset = offset + audio_config.READ_SIZE
	sock   = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	sock.sendto(util.mkheader_raw("audio", 0, b'')+util.mkheader_aud(b'PCMS16LE', offset, AUD_CHANNEL)+data, SEND_ADDR)