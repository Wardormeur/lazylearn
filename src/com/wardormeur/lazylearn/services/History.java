package com.wardormeur.lazylearn.services;

import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;

public class History {
	private Activity ctx;
	private SharedPreferences favs;
	private SharedPreferences hst;
	public History(Activity ctx){
		this.ctx = ctx;
		this.favs = ctx.getSharedPreferences("favs",0);
		this.hst = ctx.getSharedPreferences("history",0);
	}
	public boolean toggleFav(String url,String title){
		boolean status = getStatus(url);
		if(status){//not registred
			fav(url,title);
		}else{//alrdy was, we undo
			unfav(url);
		}
		return status;
	}
	
	public boolean getStatus(String url){
		boolean status = false;
		if(favs.getString(url, "nope") == "nope"){//not registred
			status = true;
		}else{//alrdy was, we undo
			status = false;
		}
		return status;
	}
	
	
	public void fav(String url,String title){
		this.favs.edit().putString(url, title).commit();
	}
	public void unfav(String url){
		this.favs.edit().remove(url).commit();
	}
	
	public Map<String,?> getFavs(){
		return this.favs.getAll();
	}
	
	public Map<String,?> getHsts(){
		return this.hst.getAll();
	}
}
