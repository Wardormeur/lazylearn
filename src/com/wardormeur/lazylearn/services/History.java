package com.wardormeur.lazylearn.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.SharedPreferences;

public class History {
	private Activity ctx;
	private SharedPreferences favs;
	private SharedPreferences favsT;
	private SharedPreferences hst;
	private SharedPreferences hstT;

	//dafuq, who said i was missing the "Page" object?
	public String lastTitle;
	//dafuq, who said i was missing the "Page" object?
	public String lastUrl; 
    
	
	public History(Activity ctx){
		this.ctx = ctx;
		this.favs = ctx.getSharedPreferences("favs",0);
		this.favsT = ctx.getSharedPreferences("favsT",0);
		this.hst = ctx.getSharedPreferences("history",0);
		this.hstT = ctx.getSharedPreferences("historyT",0);
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
	
	public Map<String,String> getHsts(){
		ArrayList<String> hst = this.loadArray(this.hst);
		ArrayList<String> hstT = this.loadArray(this.hstT);
		HashMap<String,String> hashList = new HashMap<String,String>();
		for(int i=0; i<hst.size();i++){
			hashList.put(hst.get(i),hstT.get(i));
		}

		return hashList;
	}
	
	public void clean (){
		this.favs.edit().clear().commit();
		this.favsT.edit().clear().commit();
		this.hst.edit().clear().commit();
		this.hstT.edit().clear().commit();
	}
	
	
//http://stackoverflow.com/questions/7057845/save-arraylist-to-sharedpreferences
	public void hist(String url, String title){
		Map<String,String> hashList = getHsts();
		//need to 
		hashList.put(url, title);
		this.saveArray(hst, new ArrayList(hashList.keySet()));
		this.saveArray(hstT, new ArrayList(hashList.values()));
	}
	
	//This function is actually laughable : howto get an Entry without declaring a custom class to use the interface
	public Map.Entry<String,String> getLast(){
		ArrayList<String> url = this.loadArray(this.hst);
		ArrayList<String> title = this.loadArray(this.hstT);
		HashMap<String,String> tuple = new HashMap<String,String>();
		if(url.size()>0){
			tuple.put(
					url.get(url.size()-1),
					title.get(title.size()-1)
				);
			
			Iterator<Entry<String, String>> it = tuple.entrySet().iterator(); 
			for (int i = 0; i<tuple.entrySet().size()-1;i++){
				it.next();
			}
			return it.next();
		}
		//I WANT AN ENTRY, ILL GET IT
		//Actually, i dont knwo if order is respected between loop & iterator
		//It doesnt, so it doesnt work, actually. Well, was fun anyway
		return null;
	}
	
	
	private boolean saveArray(SharedPreferences sp, ArrayList<String> sKey)
	{
	    SharedPreferences.Editor mEdit1 = sp.edit();
	    mEdit1.putInt("History_size", sKey.size()); /* sKey is an array */ 

	    for(int i=0;i<sKey.size();i++)  
	    {
	        mEdit1.remove("Status_" + i);
	        mEdit1.putString("Status_" + i, sKey.get(i));  
	    }

	    return mEdit1.commit();     
	}
	
	
	private ArrayList<String> loadArray(SharedPreferences sp)
	{  
		ArrayList<String> sKey = new ArrayList<String>();
	    sKey.clear();
	    int size = sp.getInt("History_size", 0);  

	    for(int i=0;i<size;i++) 
	    {
	        sKey.add(sp.getString("Status_" + i, null));
	    }
	    return sKey;
	}
	
}
