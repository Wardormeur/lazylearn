package com.wardormeur.lazylearn;

import java.util.ArrayList;
import java.util.Map;

import com.wardormeur.lazylearn.services.History;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TabHost;
//i use ActionBar for support of 2.3, but this really suxx compared to the TabHost
public class HistoryActivity extends ActionBarActivity implements TabListener{
	
	//Shared with fragments
	public History hst;
	public Map<String,?> datasource;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.hst = new History(this);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_history);
        
        actionBar.setDisplayShowTitleEnabled(false);

        Tab tab = actionBar.newTab()
                           .setText(R.string.favorites)
                           .setTabListener(this);
        actionBar.addTab(tab,true);

        tab = actionBar.newTab()
                       .setText(R.string.history)
                       .setTabListener(this);
        actionBar.addTab(tab,false);
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        
        return super.onCreateOptionsMenu(menu);
    }

    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.back_to_random:
        	Intent main = new Intent(HistoryActivity.this,MainActivity.class);
			main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(main);
        	return true;
        
        default:
    	   return super.onOptionsItemSelected(item);
       }
        
        
    }
	

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

    	Activity ctx;
    	public PlaceholderFragment(){}
        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_history, container, false);
            return rootView;
        }
        
        @Override
        public void onResume(){
        	this.ctx = this.getActivity();
        	ListView m_listview = (ListView) this.ctx.findViewById(R.id.listHistory);
            ArrayList favs = new ArrayList(((HistoryActivity)(this.ctx)).datasource.values());
            ArrayAdapter<String> adapter =
              new ArrayAdapter<String>(this.ctx, android.R.layout.simple_list_item_1,favs );
            m_listview.setAdapter(adapter);
            m_listview.setItemsCanFocus(true);
            
            m_listview.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> parent, View view,
    					int position, long id) {
    				String content = (String)((TextView)view.findViewById(android.R.id.text1)).getText();
    				String url = "";
    				Map<String,?> data = ((HistoryActivity)(ctx)).datasource;
    				Intent main = new Intent(((HistoryActivity)(ctx)),MainActivity.class);
    				//too lazy to make another view, so.. hax !1
    				for(String key : data.keySet()){
    					if(data.get(key) == content){
    							url = key;
    							break;//while? overrated.	
    					}
    				}
    				main.putExtra("URL", url);
    				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
    				startActivity(main);
    				ctx.finish();
    			}
    		});

            super.onResume();
        	
        }
       
    }
    
    @Override
    protected void onSaveInstanceState(Bundle icicle) {
    	icicle.putBoolean("ROTATE_FFS",true);
    	super.onSaveInstanceState(icicle);    
    }

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		int position = arg0.getPosition();
		switch(position){
		case 0:
			this.datasource = hst.getFavs();
			break;
		case 1:
			this.datasource = hst.getHsts();
			break;
		default:
			this.datasource = hst.getFavs();
			break;
		}
		//meh onTabSelected with inline seems kinda dirty : called b4 onCreate?
			
		PlaceholderFragment daList = new PlaceholderFragment();
        getSupportFragmentManager().beginTransaction()
        .add(R.id.container, daList )
        .commit();

	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

    
}
