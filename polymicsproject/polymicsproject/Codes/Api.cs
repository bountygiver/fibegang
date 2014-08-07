using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace StudentDemo
{
    class Api
    {
        public const int CONNECTIONPORT = Globals.CONNECTIONPORT;

        public static void ack(String endpoint, int identity, int sessionid, String sessionkey)
        {
            Payload p = Payload.makePayload("ack");
            p.sessionid = sessionid;
            p.sessionkey = sessionkey;
            p.identity = identity;
        }
    }

    public class Connector
    {
        public const int CONNECTIONPORT = Globals.CONNECTIONPORT;
        public const int TIMEOUTSEC = Globals.TIMEOUT;

        public bool isConnected { private set; get; }
        int queueList = 0;
        ResponsePayload responseWaiter;

        private static Connector conn;

        public UdpClient Socket
        {
            get;
            private set;
        }
        IPEndPoint groupEP;

        public event EventHandler<ConnectorErrorEventArgs> ErrorThrown;
        public event EventHandler ConnectSuccess;
        public event EventHandler<ConnectorReceiveEventArgs> ReceivedData;

        public static Connector getConnector() 
        {
            return conn;
        }

        public static Connector makeConnection(String endpoint)
        {
            return conn = new Connector(endpoint);
        }

        private Connector(String endPoint)
        {
            IPAddress ip = IPAddress.Parse(endPoint);
            groupEP = new IPEndPoint(ip, CONNECTIONPORT);
            isConnected = false;
            Task.Factory.StartNew(() =>
            {
                int retryAttempt = 0;
            Attempt:
                try
                {
                    Socket = new UdpClient();
                    Socket.Connect(groupEP);
                    isConnected = true;
                    var handler = ConnectSuccess;
                    if (handler != null) handler(this, null);
                    startReceiveLoop();
                    Socket.Close();
                }
                catch
                {
                    Thread.Sleep(1010);
                    if (retryAttempt < 10)
                    {
                        retryAttempt++;
                        goto Attempt;
                    }
                    else
                    {
                        var handler = ErrorThrown;
                        if (handler != null) handler(this, new ConnectorErrorEventArgs("Socket is locked and not released in 10 seconds, it is being blocked."));
                    }
                }
                isConnected = false;
                conn = null;
            });
        }

        private void startReceiveLoop()
        {
			Console.WriteLine ("Now receiving");
            while (isConnected)
            {
                try
                {
                    byte[] bytes = Socket.Receive(ref groupEP);
                    StringBuilder sb = new StringBuilder();
                    sb.Append(Encoding.ASCII.GetString(bytes, 0, bytes.Length));
					Console.WriteLine(sb.ToString());
                    try
                    {
                        ResponsePayload rp = JsonConvert.DeserializeObject<ResponsePayload>(sb.ToString());
                        if (queueList > 0)
                        {
                            //while (responseWaiter != null)
                            //{
                            //
                            //}
                            responseWaiter = rp;
                        }
                    }
                    catch
                    {
                        Console.WriteLine("Received data is not a response payload!");
                    }
                    try
                    {
                        NotifyPayload r = JsonConvert.DeserializeObject<NotifyPayload>(sb.ToString());
                        if (r.status == "notify")
                        {
                            var ee = new ConnectorReceiveEventArgs(r);
                            var handler = ReceivedData;
                            if (handler != null) handler(null, ee);
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Received data is not a notify payload! " + e.StackTrace);
                    }
                }
                catch (SocketException)
                {
                    isConnected = false;
                }
            }
        }

        //
        //
        //

        public void SendBlock(Payload payload)
        {
            String s = JsonConvert.SerializeObject(payload);
            byte[] sendbuf = Encoding.ASCII.GetBytes(s);
            if (sendbuf.Length > 4096) throw new Exception("Packet size too large!");
            Socket.Send(sendbuf, sendbuf.Length);
        }

        /* SendAndForget neithers blocks the thread nor wait for response
         * The client also timeouts in 3 seconds.*/
        public void Send(Payload p) 
        {
            Task tk = Task.Factory.StartNew(() =>
            {
                try
                {
                    SendBlock(p);
                }
                catch
                {

                }
            });
        }

        public ResponsePayload waitForPayload()
        {
            int i = ++queueList;
            Task tk = Task.Factory.StartNew(() =>
            {
                while (responseWaiter == null && isConnected)
                {
                }
            });
            tk.Wait(TIMEOUTSEC * i);
            var r = responseWaiter;
            if (r == null)
            {
                r = new ResponsePayload();
                r.message =  isConnected ? "Timeout!": "Connection canceled";
            }
            responseWaiter = null;
            return r;
        }


        /* SendAndReceive(Payload p)
         * Blocks until response is received
         * 
         * Has timeout of 10 second for identity = 0
         * Unlimited timeout for specific identity, please timeout the thread manually.
         * */
        public ResponsePayload SendAndReceive(Payload p)
        {
            if (p.identity != 0)
            {
                SendBlock(p);
                while (responseWaiter == null || responseWaiter.identity != p.identity)
                {

                }
                return responseWaiter;
            }
            else
            {
                SendBlock(p);
                return waitForPayload();
            }
        }

        internal void disconnect()
        {
            try
            {
                isConnected = false;
                Socket.Close();
            }
            catch
            {

            }
        }
    }
}
