using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StudentDemo
{
    public class Payload
    {
        public String request { get; set; }
        public int sessionid { get; set; }
        public IList<String> path { get; set; }
        public int identity { get; set; }
        public IDictionary<String, Object> payload;
        public String sessionkey { get; set; }

        public void addPayload(String key, Object content)
        {
            payload.Add(key, content);
        }

        public int payloadSize()
        {
            return payload.Count;
        }

        public static Payload makePayload(String Action)
        {
            var p = makePayload();
            p.request = Action;
            return p;
        }

        public static Payload makePayload()
        {
            Payload p = new Payload();
            p.path = new List<String>();
            p.payload = new Dictionary<String, Object>();
            p.request = "";
            p.sessionid = 0;
            p.identity = 0;
            p.sessionkey = "";
            return p;
        }
    }

    public class ResponsePayload
    {
        public int identity { get; set; }
        public String status { get; set; }
        public String message { get; set; }
        public IDictionary<String, Object> payload { get; set; }
    }

    public class NotifyPayload
    {
        public int identity { get; set; }
        public String status { get; set; }
        public String type { get; set; }
        public IDictionary<String, Object> payload { get; set; }
    }
}
