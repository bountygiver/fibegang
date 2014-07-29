using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StudentDemo
{
    public class ConnectorErrorEventArgs : EventArgs
    {
        public String Message { get; set; }

        public ConnectorErrorEventArgs(String msg)
        {
            Message = msg;
        }
    }

    public class ConnectorReceiveEventArgs : EventArgs
    {
        public NotifyPayload payload { get; set; }

        public ConnectorReceiveEventArgs(NotifyPayload rp)
        {
            payload = rp;
        }
    }
}
