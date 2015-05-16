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
        this.hst = new History(this);
        
//        this.hst.clean();
       //rotation happns 
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
      this.loadPage();

    }
    @Override
    public void onNewIntent(Intent intent){
    	 Bundle extras = intent.getExtras();
          
    	if(extras != null){
      	   String fromHistory =  extras.get("URL").toString();
  	       if(!TextUtils.isEmpty(fromHistory)){
  	    	   newPage(fromHistory);
  	       }
    	}else{
    		this.newPage("");
    	}
    	this.loadPage();
    	
    	super.onNewIntent(intent);
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
			item.setIcon(
						hst.getStatus(this.hst.lastUrl)
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out ! "+(this.hst.getLast() != null ?this.hst.getLast().getKey():""));
        mShareActionProvider.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }

    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.random:
            // search action
        	this.newPage("");
        	this.loadPage();
            return true;
        case R.id.save:
        	//hst return a bool regarding the actual state of the fav
			hst.toggleFav(this.hst.lastUrl,this.hst.lastTitle); //NO, WERE NOT GONNA STOCK IT, interface ftw			
        	todo = true;
        	supportInvalidateOptionsMenu();
        	return true;
        case R.id.favorites:
        	Intent hist = new Intent(MainActivity.this,HistoryActivity.class);
        	startActivity(hist);
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
    		this.hst.lastUrl = rcv.lastUrl.toString();
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
    public void loadPage(){
    	ContentLoader cL = new ContentLoader(this);
        try {
     	   this.hst.lastTitle =  cL.execute(this.page).get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		this.hst.hist(this.hst.lastUrl, this.hst.lastTitle);
     	supportInvalidateOptionsMenu();
    	
    }
    
}
