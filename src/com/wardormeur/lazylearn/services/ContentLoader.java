package com.wardormeur.lazylearn.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.wardormeur.lazylearn.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;



public class ContentLoader extends AsyncTask<JSONObject,Void,String> {

	private Activity mContext;
	private JSONObject pageName;
	//private Spanned html;
	private String html;
	public String lastTitle;
	
	public ContentLoader(Activity ctx){
		this.mContext = ctx;
		
	}

	@Override
	protected String doInBackground(JSONObject... params) {
		try {
			this.pageName = (JSONObject) params[0].getJSONObject("parse");
			//this.html = Html.fromHtml(pageName.getJSONObject("text").getString("*"),imgGetter,null);
			this.html = pageName.getJSONObject("text").getString("*").replaceAll("//upload.wikimedia", "http://upload.wikimedia");
			return this.pageName.getString("title");
		 } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("parseJSON", params[0].toString());
		}
		return null;
   }
	@Override
   protected void onPostExecute(String v) {
		//we control the display
		WebView wv = (WebView)(this.mContext.findViewById(R.id.webView));
		wv.loadData(this.html, "text/html; charset=utf-8", "utf-8");
		
		this.mContext.setTitle(v);
	
		
   }
	
} 