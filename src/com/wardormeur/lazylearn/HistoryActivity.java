package com.wardormeur.lazylearn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.wardormeur.lazylearn.services.ContentLoader;
import com.wardormeur.lazylearn.services.History;
import com.wardormeur.lazylearn.services.Recoverer;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.text.Spanned;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.support.v7.widget.ShareActionProvider;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class HistoryActivity extends ActionBarActivity {
	private ShareActionProvider mShareActionProvider;
	private History hst;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    
    @Override
    public void onResume(){
        this.hst = new History(this);
    	ListView m_listview = (ListView) findViewById(R.id.listHistory);
        //Map<String, ?> history = getSharedPreferences("history", 0).getAll().keySet();
        ArrayList favs = new ArrayList(hst.getFavs().values());
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,favs );
        m_listview.setAdapter(adapter);
        m_listview.setItemsCanFocus(true);
        m_listview.setOnItemClickListener(new OnItemClickListener() {
			
			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String content = (String)((TextView)view.findViewById(android.R.id.text1)).getText();
				String url = "";
				Map<String,?> favs = hst.getFavs();
				Intent main = new Intent(HistoryActivity.this,MainActivity.class);
				//too lazy to make another view, so.. hax !1
				for(String key : favs.keySet()){
					if(favs.get(key) == content){
							url = key;
							break;//while? overrated.	
					}
				}
				main.putExtra("URL", url);
				main.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(main);
	       
				
			}
		});
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
            View rootView = inflater.inflate(R.layout.fragment_history, container, false);
            return rootView;
        }
    }

    
}
