using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using MonoTouch.AVFoundation;
using MonoTouch.Foundation;
using MonoTouch.AudioToolbox;

namespace StudentDemo
{
    public class FibeClient
    {
        public const int CONNECTIONPORT = Globals.CONNECTIONPORT;

        Connector connector;
        public String endpoint { get; private set; }

        public String currentError;
        public int identity;
        String sessionKey;
        int sessionID;
        Dictionary<String, FibeClass> classJoined;

        public ObservableCollection<ClassItem> classAvailable { get; private set; }

        public String SessionKey
        {
            get
            {
                return sessionKey;
            }
        }

        public int SessionID
        {
            get
            {
                return sessionID;
            }
        }

        public FibeClient()
        {
            classJoined = new Dictionary<String, FibeClass>();
            classAvailable = new ObservableCollection<ClassItem>();
        }


        public bool ConnectTo(String s) 
        {
            FlushConnection();
            try
            {
                IPAddress[] ip = new IPAddress[] { IPAddress.Parse(s) };
                endpoint = s;
            }
            catch
            {
                try
                {
                    IPAddress[] ip = Dns.GetHostAddresses(s);
                    Random r = new Random();
                    endpoint = ip[r.Next(0, ip.Length - 1)].ToString();
                    connector = Connector.makeConnection(endpoint);
                    DateTime t = DateTime.Now;
                    while (connector.isConnected == false)
                    {
                        Thread.Sleep(500);
                        if ((DateTime.Now - t).Seconds > 10) break;
                    }
                    return connector.isConnected;
                }
                catch
                {

                }
                return false;
            }
            connector = Connector.makeConnection(endpoint);
            return true;
        }

        public String CurrentError
        {
            get
            {
                if (currentError != null && currentError.Length > 0) return currentError;
                return "An unknown error has occurred!";
            }
        }

        public bool VerifyAuthentication(String s)
        {
            return true;
        }

        public FibeClass JoinClass(String[] path)
        {
            StringBuilder sb = new StringBuilder();
            FibeClass c;
            foreach (String s in path)
            {
                sb.Append(s);
                if (s != path.Last()) sb.Append(", ");
            }

            if (classJoined.ContainsKey(sb.ToString())) 
            {
                c = classJoined[sb.ToString()];
            } else {
                c = new FibeClass(path);
                classJoined.Add(sb.ToString(), c);
                c.client = this;
                c.Title = sb.ToString() + " - " + sessionID;
                c.connector = connector;
            }
            return c;
        }

        public void FlushConnection()
        {
            if (connector != null) connector.disconnect();
        }

        internal bool login(string username, string password)
        {
            Payload p = Payload.makePayload();
            p.request = "login";
            p.addPayload("username", username);
            p.addPayload("password", password);
            p.addPayload("sessionkey", "");
            connector.Send(p);
            ResponsePayload r = connector.waitForPayload();
            if (r.status == "success")
            {
                identity = r.identity;
                Object k;
                r.payload.TryGetValue("sessionid", out k);
                sessionID = Convert.ToInt32(k);
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        }

        internal bool register(string username, string password)
        {
            Payload p = Payload.makePayload();
            p.request = "regist";
            p.addPayload("username", username);
            p.addPayload("password", password);
            connector.Send(p);
            ResponsePayload r = connector.waitForPayload();
            if (r.status == "success")
            {
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        }

        internal bool list()
        {
            Payload p = Payload.makePayload();
            p.sessionid = this.SessionID;
            p.request = "ls";
            connector.Send(p);
            ResponsePayload r = connector.waitForPayload();
            if (r.status == "success")
            {
                var k = r.payload["list"];
                var aa = JsonConvert.DeserializeObject<Dictionary<String, Object>>(k.ToString());
				classAvailable.Clear ();
                foreach (KeyValuePair<String, Object> s in aa)
                {
                        classAvailable.Add(new ClassItem(s.Key, s.Value.ToString())); // Run on UI Thread
                }
                return true;
            }
            else
            {
                if (r == null)
                {
                    r = new ResponsePayload();
                    r.message = "Socket timeout!";
                }
                currentError = r.message;
                return false;
            }
        }

        internal bool createGroup(String name)
        {
            Payload p = makePayload();
            p.request = "create_group";
            p.addPayload("name", name);
            connector.Send(p);
            ResponsePayload r = connector.waitForPayload();
            if (r.status == "success")
            {
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        }

        public Payload makePayload()
        {
            Payload p = Payload.makePayload();
            p.identity = identity;
            p.sessionkey = sessionKey;
            p.sessionid = sessionID;
            return p;
        }
    }

    public class FibeClass : INotifyPropertyChanged
    {

        public event PropertyChangedEventHandler PropertyChanged;
        private void RaisePropertyChanged([CallerMemberName] string caller = "")
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(caller));
            }
        }


        public const int CONNECTIONPORT = Globals.CONNECTIONPORT;


        public String Title { get; set; }
        public String[] path { get; private set; }
        public int ID { get; set; }
        private DateTime ping, pong;
        private String _lastping = "X";
        public FibeClient client;
        public Connector connector;

        public String Ping
        {
            get
            {
                try
                {
                    _lastping = pong > ping ? (pong - ping).Milliseconds.ToString() : _lastping;
                    return "Ping: " + _lastping + " ms";
                }
                catch
                {
                    return "Ping: X ms";
                }
            }
        }

        void pingHost() 
        {
            Task.Factory.StartNew(() =>
            {
                ping = DateTime.Now;
                var p = Payload.makePayload("ping");
                connector.Send(p);
                NotifyPayload r = null;
                connector.ReceivedData += (sender, e) =>
                {
                    r = e.payload;
                };
                Task t = Task.Factory.StartNew(() =>
                {
                    while (r == null)
                    {

                    }
                });
                t.Wait(1000);
                if (r != null && r.status == "pong" && r.identity == p.identity) pong = DateTime.Now;
            });
        }

        private bool _isAsking;
        public bool IsAsking
        {
            get
            {
                return _isAsking;
            }
            set
            {
                _isAsking = value;
                //RaisePropertyChanged();
            }
        }

        public PingRequest CurrentAsk { get; set; }

		public PingRequest addPing(String tags)
        {
            String[] tagsArr;
            if (tags.Contains(";"))
            {
                tagsArr = tags.Split(';');
            }
            else
            {
                tagsArr = new String[] { tags };
            }
            CurrentAsk = new PingRequest(this, tagsArr);
            RaisePropertyChanged("CurrentAsk");
			return CurrentAsk;
        }

        public PingRequest addPing(IList<String> tags)
        {
            CurrentAsk = new PingRequest(this, tags.ToArray());
            RaisePropertyChanged("CurrentAsk");
            return CurrentAsk;
        }

        public FibeClass(String[] path)
        {
            this.path = path;
        }

        public bool CancelRequest()
        {
            if (CurrentAsk == null) return false;
            CurrentAsk.stopListening();
            CurrentAsk = null;
            RaisePropertyChanged("CurrentAsk");

            //Send request to remove from queue here!

			Payload p = new Payload();
			p.request = "cancel";
			p.sessionkey = client.SessionKey;
			p.sessionid = client.SessionID;
			p.path = path.Concat(new String[] {"audio"}).ToList();
			Random rnd = new Random();
			p.identity = rnd.Next(Int32.MaxValue);
			ResponsePayload r = new ResponsePayload();
			r.message = "Connection: timeout!";
			connector.Send (p);

            return true;
        }
    }

    public class PingRequest : IDisposable, INotifyPropertyChanged
    {

        public event PropertyChangedEventHandler PropertyChanged;
        private void RaisePropertyChanged([CallerMemberName] string caller = "")
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(caller));
            }
        }


        public const int CONNECTIONPORT = Globals.CONNECTIONPORT;
        public const int AUDIOPORT = Globals.AUDIOPORT;

        bool _isTalking;
        public bool isTalking
        {
            get
            {
                return _isTalking;
            }
            private set
            {
                if (_isTalking != value)
                {
                    if (!value)
                    {
                        StopTalking();
                    }
                }
                _isTalking = value;
            }
        }

        public FibeClass ClassRequested { get; set; }
        public DateTime When { get; set; }

        double volume = 0;

        public double InputLoudness
        {
            get
            {
                return volume;
            }
            private set
            {
                volume = Math.Max(value - 0.1, 0);
                RaisePropertyChanged("V1");
                RaisePropertyChanged("V2");
                RaisePropertyChanged("V3");
            }
        }
        public Double V3
        {
            get
            {
                if (InputLoudness > 0.6) return ((Math.Min(InputLoudness, 0.9) - 0.6) / 0.3);
                return 0;
            }
        }
        public Double V2
        {
            get
            {
                if (InputLoudness > 0.3) return ((Math.Min(InputLoudness, 0.6) - 0.3) / 0.3);
                return 0;
            }
        }
        public Double V1
        {
            get
            {
                return (Math.Min(InputLoudness, 0.3) / 0.3);
            }
        }


		public static NSDictionary settings;

        int packetIdentity;

        int timestamp
        {
            get { return Convert.ToInt32((When - new DateTime(1950, 1, 1)).TotalSeconds); }
        }

        public ObservableCollection<String> Tags { get; set; }

        public String tagString
        {
            get
            {
                return String.Join(", ", Tags);
            }
        }

        public FibeClient client;
        Connector connector;
        int AudioSessionId;

        public event EventHandler onAudioAccept;
		public event EventHandler onAudioStop;

        private UdpClient audioCaller;

        public PingRequest(FibeClass fibeClass, string[] tagsArr)
        {
            this.ClassRequested = fibeClass;
            client = fibeClass.client;
            Tags = new ObservableCollection<string>(tagsArr);
            RaisePropertyChanged("tagString");
            When = DateTime.Now;
            isTalking = false;
            connector = fibeClass.connector;

            Payload p = client.makePayload();
            p.path = ClassRequested.path.Concat(new String[] {"audio"}).ToArray();
            p.request = "enqueue";
            int timespan = this.timestamp;
            p.addPayload("time", timespan.ToString());
            p.addPayload("tags", tagsArr);
            p.sessionkey = client.SessionKey;
            p.sessionid = client.SessionID;
            Random rnd = new Random();
            p.identity = packetIdentity = rnd.Next(Int32.MaxValue);
            connector.Send(p);
            connector.ReceivedData += waitForPermit;
        }

        private void waitForPermit(object sender, ConnectorReceiveEventArgs e)
        {
            if (e.payload.identity == packetIdentity && e.payload.status == "notify")
            {
                if (e.payload.type == "permit")
                {
                    connector.ReceivedData -= waitForPermit;
                    audioCaller = null;
                    int retryAttempts = 0;
                    while (audioCaller == null)
                    {
                        try
                        {
                            audioCaller = new UdpClient();
                        }
                        catch
                        {
                            if (retryAttempts > 10) 
                            { 
                                //Cancel queue
                                return;
                            }
                            retryAttempts++;
                            Thread.Sleep(1010);
                        }
                    }
                    IPAddress ip = IPAddress.Parse(client.endpoint);
                    IPEndPoint groupEP = new IPEndPoint(ip, AUDIOPORT);
                    audioCaller.Connect(groupEP);
                    this.AudioSessionId = Convert.ToInt32(e.payload.payload["sessionid"]);
                    this.startTalking(audioCaller);
                    var handler = onAudioAccept;
                    if (handler != null) onAudioAccept(this, null);
                    connector.ReceivedData += CancelRequested;
				} else if (e.payload.type == "revoke") {
					this.stopTalking ();
				}
            }
        }

        private void CancelRequested(object sender, ConnectorReceiveEventArgs e)
        {
			Console.Write ("xxx");
			if (e.payload.type == "cancel" || e.payload.type == "revoke")
            {
                isTalking = false;
				var handler = onAudioStop;
				if (handler != null)
					handler (this, null);
            }
        }


		InputAudioQueue recorder;
		AudioStreamBasicDescription playerFormat = AudioStreamBasicDescription.CreateLinearPCM (8000, 1, 16, false);

        private void startTalking(UdpClient audioCaller)
		{
			//Stop old recording session

			//Generate new WaveFormat
			//    recorder.WaveFormat = new WaveFormat(16000, 16, 1);
			//    recorder.BufferMilliseconds = 50;
			//    recorder.DataAvailable += SendAudio; //Add event to SendAudio

			recorder = new InputAudioQueue (playerFormat);


			for (int i = 0; i < 3; i++) {
				IntPtr aBUff;
				recorder.AllocateBuffer (1280, out aBUff);
				recorder.EnqueueBuffer (aBUff, 1280, null);
			}
			isTalking = true;
			recorder.InputCompleted += SendAudio;
			recorder.Start ();
		}

        public void stopListening()
        {
            try
            {
                connector.ReceivedData -= waitForPermit;
            }
            catch
            {

            }
        }

        public void stopTalking()
        {
            isTalking = false;
        }

        private bool StopTalking()
        {
            audioCaller.Close();
            connector.ReceivedData -= CancelRequested;

            //Stop recording

            Payload p = new Payload();
            p.request = "cancel";
            p.sessionkey = client.SessionKey;
            p.sessionid = client.SessionID;
            p.path = ClassRequested.path.Concat(new String[] {"audio"}).ToList();
            Random rnd = new Random();
            p.identity = packetIdentity = rnd.Next(Int32.MaxValue);
            ResponsePayload r = new ResponsePayload();
            r.message = "Connection: timeout!";
            Task k = Task.Factory.StartNew(() => { r = connector.SendAndReceive(p); });
            k.Wait(5000);
            if (r.status == "success")
            {
                return true;
            }
            else
            {
                client.currentError = r.message;
                return false;
            }

        }


		public void SendAudio(object sender, InputCompletedEventArgs e)
        {
            MemoryStream ms = new MemoryStream();

            String s = "a000";

            byte[] bufWriter = Encoding.ASCII.GetBytes(s.ToCharArray(), 0, 4);
            ms.Write(bufWriter, 0, 4);

            bufWriter = BitConverter.GetBytes(AudioSessionId);
            if (BitConverter.IsLittleEndian) Array.Reverse(bufWriter);
            ms.Write(bufWriter, 0, 4);

            //bufWriter = BitConverter.GetBytes(0);
			long time = (long) (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;
			bufWriter = BitConverter.GetBytes(time);
            if (BitConverter.IsLittleEndian) Array.Reverse(bufWriter);
            ms.Write(bufWriter, 0, 8);

			/*Console.WriteLine ("MS Length before: " + ms.Length);

			unsafe {
				byte* bufptr = (byte*) e.IntPtrBuffer;

				for (int i = 0; i < 1280; i++) {
					byte pt = (*(bufptr + i));
					//if (BitConverter.IsLittleEndian) Array.Reverse(pt);
					ms.Write (new byte[] { pt }, 0, 1);
					Console.Write ("\\x" + pt);
				}
			}
			Console.WriteLine ("\nMS Length after: " + ms.Length);*/

			var buffer = (AudioQueueBuffer)System.Runtime.InteropServices.Marshal.PtrToStructure(e.IntPtrBuffer, typeof(AudioQueueBuffer));

			var send = new byte[buffer.AudioDataByteSize];
			System.Runtime.InteropServices.Marshal.Copy(buffer.AudioData, send, 0, (int)buffer.AudioDataByteSize);
			ms.Write (send, 0, send.Length);
            //short sample16Bit = BitConverter.ToInt16(payload, 0);
            //InputLoudness = Math.Abs(sample16Bit / 32000.00);

			if (isTalking) recorder.EnqueueBuffer (e.IntPtrBuffer, 1280, null);


            byte[] sendbuf = ms.ToArray();
            //if (sendbuf.Length > 4096) throw new Exception("Packet size too large!");
            Task tk = Task.Factory.StartNew(() =>
            {
                try
                {
                    var aSender = audioCaller.BeginSend(sendbuf, sendbuf.Length, null, null);
                    aSender.AsyncWaitHandle.WaitOne(TimeSpan.FromSeconds(3));
                    if (aSender.IsCompleted) audioCaller.EndSend(aSender);
                }
                catch
                {

                }
            });
        }


        public void Dispose()
        {
            try
            {
                connector.ReceivedData -= waitForPermit;
            }
            catch
            {

            }
            try
            {
                if (audioCaller != null)
                {
                    audioCaller.Close();
                }
            }
            catch
            {

            }
        }
    }
}
