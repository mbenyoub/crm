package com.openerp.base.account;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.R;
import com.openerp.auth.OpenERPAccountManager;
import com.openerp.orm.OEHelper;
import com.openerp.support.AppScope;
import com.openerp.support.BaseFragment;
import com.openerp.support.OEUser;
import com.openerp.support.menu.OEMenu;
import com.openerp.util.Base64Helper;

public class UserProfile extends BaseFragment {
	View rootView = null;
	EditText password = null;
	TextView txvUserLoginName, txvUsername, txvServerUrl, txvTimeZone,
			txvDatabase;
	ImageView imgUserPic;
	AlertDialog.Builder builder = null;
	Dialog dialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		rootView = inflater.inflate(R.layout.fragment_account_user_profile,
				container, false);
		scope = new AppScope(this);
		scope.context().setTitle("OpenERP User Profile");

		setupView();
		return rootView;
	}

	private void setupView() {

		imgUserPic = null;
		imgUserPic = (ImageView) rootView.findViewById(R.id.imgUserProfilePic);
		imgUserPic.setImageBitmap(Base64Helper.getBitmapImage(scope.context(),
				scope.User().getAvatar()));
		txvUserLoginName = (TextView) rootView
				.findViewById(R.id.txvUserLoginName);
		txvUserLoginName.setText(scope.User().getAndroidName());

		txvUsername = (TextView) rootView.findViewById(R.id.txvUserName);
		txvUsername.setText(scope.User().getUsername());

		txvServerUrl = (TextView) rootView.findViewById(R.id.txvServerUrl);
		txvServerUrl.setText(scope.User().getHost());

		txvDatabase = (TextView) rootView.findViewById(R.id.txvDatabase);
		txvDatabase.setText(scope.User().getDatabase());

		txvTimeZone = (TextView) rootView.findViewById(R.id.txvTimeZone);
		String timezone = scope.User().getTimezone();
		txvTimeZone.setText((timezone.equals("false")) ? "GMT" : timezone);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.menu_fragment_account_user_profile, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_account_user_profile_sync:
			dialog = inputPasswordDialog();
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public Object databaseHelper(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OEMenu menuHelper(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	private Dialog inputPasswordDialog() {
		builder = new Builder(scope.context());
		password = new EditText(scope.context());
		password.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		builder.setTitle("Enter Password").setMessage("Provide your password")
				.setView(password);
		builder.setPositiveButton("Update Info", new OnClickListener() {
			public void onClick(DialogInterface di, int i) {
				OEUser userData = null;
				try {
					OEHelper openerp = new OEHelper(scope.context(), scope
							.User().getHost());

					userData = openerp.login(scope.User().getUsername(),
							password.getText().toString(), scope.User()
									.getDatabase(), scope.User().getHost());
				} catch (Exception e) {
				}
				if (userData != null) {
					if (OpenERPAccountManager.updateAccountDetails(
							scope.context(), userData)) {
						Toast.makeText(getActivity(), "Infomation Updated.",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getActivity(), "Invalid Password !",
							Toast.LENGTH_LONG).show();
				}
				setupView();
				dialog.cancel();
				dialog = null;
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface di, int i) {
				dialog.cancel();
				dialog = null;
			}
		});
		return builder.create();

	}
}
