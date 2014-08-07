using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;
using System.Threading.Tasks;
using System.Collections.Generic;
using StudentDemo;

namespace polymicsproject
{
	partial class LoginPageViewController : UIViewController
	{
		public LoginPageViewController (IntPtr handle) : base (handle)
		{
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
			btnLogin.TouchUpInside += LoginBtnClick;
			txtUsername.ShouldReturn += JumptoPassword;
			txtPassword.ShouldReturn += LoginReturn;
		}

		bool JumptoPassword (UITextField textField)
		{
			txtPassword.Select (textField);
			return true;
		}

		bool LoginReturn (UITextField textField)
		{
			textField.ResignFirstResponder ();
			LoginBtnClick (null, null);
			return true;
		}

		async void LoginBtnClick (object sender, EventArgs e)
		{
			bool x = false;
			String username = txtUsername.Text;
			String password = txtPassword.Text;
			LoadSpinner loadingOverlay = new LoadSpinner (UIScreen.MainScreen.Bounds);
			View.Add (loadingOverlay);

			await Task.Run (() => {
				x = Application.engine.login(username, password);
			});

			loadingOverlay.Hide ();
			UIApplication.SharedApplication.NetworkActivityIndicatorVisible = false;

			if (!x) {
				UIAlertView err = new UIAlertView ("Login failed", Application.engine.CurrentError, null, "OK", null);
				err.Show ();
			} else {

				x = await DoGetRooms ();

				if (x) {
					this.NavigationController.PopViewControllerAnimated (false);
					RoomSelectController roomView = Storyboard.InstantiateViewController ("RoomSelectController") as RoomSelectController;
					roomView.classList = new List<ClassItem> (Application.engine.classAvailable);

					this.NavigationController.PushViewController (roomView, true);
				}
			}
		}

		async Task<bool> DoGetRooms() {
			bool x = false;
			await Task.Run (() => {
				try {
					x = Application.engine.list();
				} catch {
					x = false;
				}
			});
			if (!x) {
				UIAlertView err = new UIAlertView ("Failed to retrieve classes!", Application.engine.CurrentError, null, "OK", null);
				err.Show ();
			}
			return x;
		}
	}
}
