package com.wardormeur.lazylearn;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.wardormeur.lazylearn.services.ContentLoader;
import com.wardormeur.lazylearn.services.History;
import com.wardormeur.lazylearn.services.Recoverer;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;

public class MainActivity extends ActionBarActivity {
	private JSONObject page;
	private ShareActionProvider mShareActionProvider;
	private History hst;
	private boolean todo = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
       Bundle extras = this.getIntent().getExtras();
       if(extras != null){
    	   String fromHistory =  extras.get("URL").toString();
	       if(!TextUtils.isEmpty(fromHistory)){
	    	   newPage(fromHistory);
	       }
	     
       }else{
	    	   //Else if we got a rotation
		       if (savedInstanceState != null){
		    	    try {
						this.page = new JSONObject(savedInstanceState.getString("CURRENT_PAGE"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		       //last choice possible of reload : get a new page
		       }else{
		           newPage("");
		    	  
		       } 
	       }
       new ContentLoader(this).execute(this.page);
       this.hst = new History(this);
    }
    @Override
    protected void onSaveInstanceState(Bundle icicle) {
    	super.onSaveInstanceState(icicle);    
    	icicle.putString("CURRENT_PAGE", this.page.toString());
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {    
    	if(todo){
    		MenuItem item = menu.findItem(R.id.save);
            String url = getSharedPreferences("history",0).getString("last", "");
			item.setIcon(//oh god, this is ugly.
						hst.getStatus(url)
						?R.drawable.ic_favorite_outline_white_36dp:R.drawable.ic_favorite_white_36dp);
    		todo = false;
    	}
        return super.onPrepareOptionsMenu(menu);
    }   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider using support version of action provider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out ! "+getSharedPreferences("history",0).getString("last", ""));
        mShareActionProvider.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }

    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.random:
            // search action
        	newPage("");
        	new ContentLoader(this).execute(this.page);
            return true;
        case R.id.save:
        	//hst return a bool regarding the actual state of the fav
        	try {
				hst.toggleFav(getSharedPreferences("history",0).getString("last", ""), this.page.getJSONObject("parse").getString("title"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	todo = true;
        	supportInvalidateOptionsMenu();
        	return true;
        case R.id.favorites:
        	startActivity(new Intent(MainActivity.this,HistoryActivity.class));
        	return true;
       default:
    	   return super.onOptionsItemSelected(item);
       }
        
        
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void newPage(String url){
    	try {
    		Recoverer rcv = new Recoverer();
    		if(!TextUtils.isEmpty(url)){
    			rcv.setUrl(url);
    		}
    		this.page = rcv.execute(this).get();
    		getSharedPreferences("history",0).edit().putString("last", rcv.lastUrl.toString()).commit();
    		todo = true;
    		supportInvalidateOptionsMenu();
    		
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	
    }
    
}
