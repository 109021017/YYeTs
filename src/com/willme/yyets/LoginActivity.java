package com.willme.yyets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.willme.yyets.fragment.LoginFragment;

public class LoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(android.R.id.content);
		if(fragment == null){
			fm.beginTransaction().add(android.R.id.content, new LoginFragment()).commit();
		}
	}
	
}
