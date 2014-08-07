using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;
using StudentDemo;

namespace polymicsproject
{
	partial class InQueueViewController : UIViewController
	{
		public FibeClass joined {
			get;
			set;
		}

		public PingRequest request {
			get;
			set;
		}

		public InQueueViewController (IntPtr handle) : base (handle)
		{
		}

		public override void WillMoveToParentViewController (UIViewController parent)
		{
			base.WillMoveToParentViewController (parent);

			if (parent == null) {
				Console.WriteLine ("Popped!");
				request.stopTalking ();
				joined.CancelRequest();
			}
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();

			request.onAudioAccept += (object sender, EventArgs e) => {
				Console.WriteLine("Called!");
				btnStop.SetTitle("Stop", UIControlState.Normal);
			};

			request.onAudioStop += (object sender, EventArgs e) => {
				CancelReq();
			};

			btnStop.TouchUpInside += (object sender, EventArgs e) => {
				CancelReq();
			};

			btnStop.SetTitle ("Cancel", UIControlState.Normal);
		}

		public void CancelReq() {
			this.NavigationController.PopViewControllerAnimated(true);
		}
	}
}
