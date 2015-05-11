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



public class ContentLoader extends AsyncTask<JSONObject,Void,Void> {

	private Activity mContext;
	private JSONObject pageName;
	//private Spanned html;
	private String html;
	
	public ContentLoader(Activity ctx){
		this.mContext = ctx;
		
	}

	@Override
	protected Void doInBackground(JSONObject... params) {
		try {
			this.pageName = (JSONObject) params[0].getJSONObject("parse");
			//this.html = Html.fromHtml(pageName.getJSONObject("text").getString("*"),imgGetter,null);
			this.html = pageName.getJSONObject("text").getString("*").replaceAll("//upload.wikimedia", "http://upload.wikimedia");
		 } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("parseJSON", params[0].toString());
		}
		return null;
   }
	@Override
   protected void onPostExecute(Void v) {
		/*TextView content = (TextView) this.mContext.findViewById(R.id.PageContent);
		content.setText(this.html);*/
		WebView wv = (WebView)(this.mContext.findViewById(R.id.webView));
		wv.loadData(this.html, "text/html", "utf-8");
		
		try {
			this.mContext.setTitle(		this.pageName.getString("title"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
   }
	
    private ImageGetter imgGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
        	  HttpGet get = new HttpGet("http://"+source);
              DefaultHttpClient client = new DefaultHttpClient();
              Drawable drawable = null;
              try {
                      HttpResponse response = client.execute(get);
                      InputStream stream = response.getEntity().getContent();
                      FileOutputStream fileout = new FileOutputStream(new File(
                                      Environment.getExternalStorageDirectory()
                                                      .getAbsolutePath()
                                                      + "/test.jpg"));
                      int read = stream.read();
                      while (read != -1) {
                              fileout.write(read);
                              read = stream.read();
                      }
                      fileout.flush();
                      fileout.close();
                      drawable = Drawable.createFromPath(Environment
                                      .getExternalStorageDirectory().getAbsolutePath()
                                      + "/test.jpg");
                      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                                      .getIntrinsicHeight());

              } catch (ClientProtocolException e) {
                      e.printStackTrace();
              } catch (IOException e) {
                      e.printStackTrace();
              }
              return drawable;
        }
 };
	
} 