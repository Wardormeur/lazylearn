package com.wardormeur.lazylearn.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.wardormeur.lazylearn.HistoryActivity;
import com.wardormeur.lazylearn.MainActivity;
import com.wardormeur.lazylearn.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
		return this.parseJSON(params[0]);
   }
	@Override
   protected void onPostExecute(String v) {
		//we control the display
		WebView wv = (WebView)(this.mContext.findViewById(R.id.webView));
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.setWebViewClient(new WebViewClient(){

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		    	Intent main = new Intent(mContext,MainActivity.class);
		    	//hotfix for some URLs
		    	/*if(url.contains("index.php")){
		    		try {
						List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(url),"UTF-8");
						for (NameValuePair param: parameters) {
							if(param.getName()=="title"){
								url = "http://wikipedia.org/wiki/"+param.getValue();
								break;
							}
						}
		    		} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			    
		    	}*/
				main.putExtra("URL", url);
				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(main);
		    	
		    	return true;
		    }
		});
		wv.loadDataWithBaseURL("http://wikipedia.org/", this.html, "text/html; charset=utf-8", "utf-8", null);
		
		this.mContext.setTitle(v);
	
		
   }
	
	private String  parseJSON(JSONObject json){
		try {
			pageName = json.getJSONObject("parse");
			html = pageName.getJSONObject("text").getString("*").replaceAll("//upload.wikimedia", "http://upload.wikimedia");
			return pageName.getString("title");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
} 