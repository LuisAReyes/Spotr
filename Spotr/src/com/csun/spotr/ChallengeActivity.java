package com.csun.spotr;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.NewQAAdapter;
import net.londatiga.android.QuickAction;
import net.londatiga.android.R;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.csun.spotr.gui.ChallengeItemAdapter;

/**
 * @author: Chan Nguyen
 * 
 */
public class ChallengeActivity extends TabActivity {
	private ListView challengeListView;
	private ChallengeItemAdapter challengeItems;

	private static final String headers[] = { "Challenge 1", "Challenge 2", "Challenge 3", "Challenge 4", "Challenge 5", "Challenge 6", "Challenge 7", "Challenge 8", "Challenge 9", "Challenge 10", "Challenge 11", "Challenge 12", "Challenge 13", "Challenge 14", "Challenge 15", };

	private static final String bodies[] = { "Description of challenge 1", "Description of challenge 2", "Description of challenge 3", "Description of challenge 4", "Description of challenge 5", "Description of challenge 6", "Description of challenge 7", "Description of challenge 8", "Description of challenge 9", "Description of challenge 10", "Description of challenge 11", "Description of challenge 12", "Description of challenge 13", "Description of challenge 14", "Description of challenge 15", };

	private static final String rating[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };

	private static final boolean flags[] = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
	
//	/**
//	 * Listview selected row
//	 */
//	private int mSelectedRow = 0;
//	
//	/**
//	 * Right arrow icon on each listview row
//	 */
//	private ImageView mMoreIv = null;
//	
//	private static final int ID_ADD = 1;
//	private static final int ID_ACCEPT = 2;
//	private static final int ID_UPLOAD = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenge);
		
		TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
		spec.setContent(R.id.tablelayout_challenge);
		spec.setIndicator("Me", getResources().getDrawable(R.drawable.tab_user));
		getTabHost().addTab(spec);
		
		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.tablelayout_challenge);
		spec.setIndicator("Spots", getResources().getDrawable(R.drawable.tab_spot));
		getTabHost().addTab(spec);
		
		spec = getTabHost().newTabSpec("tag3");
		spec.setContent(R.id.tablelayout_challenge);
		spec.setIndicator("Friends", getResources().getDrawable(R.drawable.tab_group));
		getTabHost().addTab(spec);

		getTabHost().setCurrentTab(0);

		
		challengeListView = (ListView) findViewById(R.id.challenge_xml_listview_challenge);
		challengeItems = new ChallengeItemAdapter(this, headers, bodies, rating, flags);
		challengeListView.setAdapter(challengeItems);
		
		registerForContextMenu(challengeListView);

		
		challengeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		
//        NewQAAdapter adapter = new NewQAAdapter(this);
//        final String[] data = {"Test 01", "Test 02", "Test 03", "Test 04", "Test 05", "Test 06", "Test 07", "Test 08",
//			   	  "Test 09", "Test 10"};
//
//        adapter.setData(data);
//        challengeListView.setAdapter(adapter);
//        
//        ActionItem rate = new ActionItem(ID_ADD, "Rate", getResources().getDrawable(R.drawable.adventurer));
//		ActionItem info = new ActionItem(ID_ACCEPT, "Info", getResources().getDrawable(R.drawable.adventurer));
//        ActionItem go = new ActionItem(ID_UPLOAD, "GO!", getResources().getDrawable(R.drawable.adventurer));
//		
//		final QuickAction mQuickAction 	= new QuickAction(this);
//		
//		mQuickAction.addActionItem(rate);
//		mQuickAction.addActionItem(info);
//		mQuickAction.addActionItem(go);
//		
//		//setup the action item click listener
//		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
//			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
//				ActionItem actionItem = quickAction.getActionItem(pos);
//				
//				if (actionId == ID_ADD) { //Add item selected
//					Toast.makeText(getApplicationContext(), "Add item selected on row " + mSelectedRow, Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(getApplicationContext(), actionItem.getTitle() + " item selected on row " 
//							+ mSelectedRow, Toast.LENGTH_SHORT).show();
//				}	
//			}
//		});
//		
//		//setup on dismiss listener, set the icon back to normal
//		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {			
//			public void onDismiss() {
//				mMoreIv.setImageResource(R.drawable.ic_list_more);
//			}
//		});
//		
//		challengeListView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				mSelectedRow = position; //set the selected row
//				
//				mQuickAction.show(view);
//				
//				//change the right arrow icon to selected state 
//				mMoreIv = (ImageView) view.findViewById(R.id.challenge_icon);
//				mMoreIv.setImageResource(R.drawable.ic_list_more_selected);
//			}
//		});

	}
	
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
      }
    
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        return super.onContextItemSelected(item);
    }

}