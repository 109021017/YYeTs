package com.willme.yyets.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class YYeTsRestClient {

	private static AsyncHttpClient client = new AsyncHttpClient();
	private static PersistentCookieStore cookie;

	public static void init(Context context){
		cookie = new PersistentCookieStore(context);
		client.setCookieStore(cookie);
	}
	
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	
	public static PersistentCookieStore getCookieStore(){
		return cookie;
	}

}
