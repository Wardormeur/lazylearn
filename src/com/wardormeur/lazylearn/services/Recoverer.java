package com.wardormeur.lazylearn.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;



public class Recoverer extends AsyncTask<Context,Void,JSONObject>{
	private String base = "m.wikipedia.org/";
	private String rpurl = "wiki/Special:Random"; 
	//private String rpapi = "w/api.php?action=query&disableeditsection&disablepp&prop=extracts&format=json&titles=";
	private String rpapi = "w/api.php?action=parse&disableeditsection&disablepp&disabletoc&format=json&redirects&page=";
	private String rurl = "";
	public URL lastUrl ;
	
	private boolean fromHistory = false;
	private String lang = "en";
	public void setParams(Context ctx){//ehere starts DI nightmare
		
		//check if lang is handled with wikipedia?
		this.lang = ctx.getResources().getConfiguration().locale.getLanguage();
		
		this.rurl = "https://"+this.lang+"."+this.base+this.rpurl;
	}
	
	@Override
	protected JSONObject doInBackground(Context... params) {
		setParams(params[0]);
		if(!this.fromHistory){
			this.lastUrl = getRandomURL();
		}else{
			this.fromHistory = false;//lastUrl is already set 
		}
		return getContent(craftURL(this.lastUrl));
	}
	
	public void setUrl(String url){
		try {
			this.lastUrl = new URL(url);
			this.fromHistory = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//featured content shared between users?
	public URL getRandomURL(){
		//Random url is crafted OTG
		URL obj;
		try {
			obj = new URL(rurl);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setInstanceFollowRedirects(false);  //you still need to handle redirect manully.
			//we know it's gonna redirect..
			return new URL(conn.getHeaderField("Location"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject getContent(URL rpage){
		//Use Wikimedia api
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) rpage.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
                     conn.getInputStream()));
			String inputLine = "";
			String temp;
			while ((temp = in.readLine()) != null) {
				inputLine = inputLine + temp;
			}
				
			in.close();
			return new JSONObject(inputLine); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject();
	}
	private URL craftURL(URL toGet){
		try {
			String[] parts = toGet.getPath().split("/");
			URL url = new URL("https://"+this.lang+"."+this.base+this.rpapi+parts[parts.length-1]);
			Log.i("crafted", url.toString());
			return url;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
