package com.willme.yyets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class SettingsActivty extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static class SettingsFragment extends PreferenceFragment{
		
	}
	
}
