package com.willme.yyets.fragment;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.willme.yyets.R;
import com.willme.yyets.YYeTsApp;
import com.willme.yyets.api.Constants;
import com.willme.yyets.api.YYeTsRestClient;
import com.willme.yyets.push.NotificationService;
import com.willme.yyets.utils.JsonUtils;

public class LoginFragment extends BaseFragment {

	private EditText mAccountField, mPasswordField;
	private TextView mErrorInfoText;
	private Button mSignInButton;
	private ProgressDialog mProgressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		mErrorInfoText = (TextView) rootView.findViewById(R.id.tv_error_info);
		mAccountField = (EditText) rootView.findViewById(R.id.et_account);
		mPasswordField = (EditText) rootView.findViewById(R.id.et_password);
		mPasswordField.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				signIn();
				return true;
			}
		});
		mSignInButton = (Button) rootView.findViewById(R.id.btn_sign_in);
		mSignInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				signIn();
			}

		});
		return rootView;

	}

	private void signIn() {

		String account = mAccountField.getText().toString();
		String password = mPasswordField.getText().toString();
		if (TextUtils.isEmpty(account)) {
			mAccountField.setError(getString(R.string.username_empty_error));
			mAccountField.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			mPasswordField.setError(getString(R.string.password_empty_error));
			mPasswordField.requestFocus();
			return;
		}

		signIn(account, password);
	}

	private void signIn(String account, String password) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage(getString(R.string.signing_in));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
		RequestParams params = new RequestParams();
		params.put("account", account);
		params.put("password", password);
		params.put("from", "loginpage");
		params.put("remember", "1");
		YYeTsRestClient.post(Constants.LOGIN_URL, params,
				new JsonHttpResponseHandler() {
					
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						boolean status = JsonUtils.getBooleanFromJson(response, "status", false);
						if(status){
							JSONObject data;
							try {
								data = response.getJSONObject("data");
								YYeTsApp.UserInfo.setUserId(JsonUtils.getStringFromJson(data, "uid", null));
								YYeTsApp.UserInfo.setNickname(JsonUtils.getStringFromJson(data, "nickname", null));
								YYeTsApp.UserInfo.setTinyAvatarUrl(JsonUtils.getStringFromJson(data, "avatar_t", null));
								getActivity().startService(new Intent(getActivity(), NotificationService.class));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else{
							String info = JsonUtils.getStringFromJson(response, "info", null);
							if(!TextUtils.isEmpty(info)){
								mErrorInfoText.setText(info);
								mErrorInfoText.setVisibility(View.VISIBLE);
							}
						}
					}
					
					@Override
					public void onFinish() {
						super.onFinish();
						mProgressDialog.dismiss();
					}
					
				});
	}

}
