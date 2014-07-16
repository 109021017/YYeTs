package com.willme.yyets.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.willme.yyets.FeedbackActivity;
import com.willme.yyets.LoginActivity;
import com.willme.yyets.MainActivity;
import com.willme.yyets.R;
import com.willme.yyets.SettingsActivty;
import com.willme.yyets.menu.DrawerItem;
import com.willme.yyets.menu.DrawerItem.DrawerAccountItem;
import com.willme.yyets.menu.DrawerItem.DrawerOrdinaryItem;
import com.willme.yyets.menu.DrawerItem.DrawerSecondaryItem;

public class DrawerFragment extends BaseFragment{
	
	public static final int ID_MENU_PROFILE = 5;
	public static final int ID_MENU_HOME = 6;
	public static final int ID_MENU_FAVORATES = 7;
	public static final int ID_MENU_SETTINGS = 8;
	public static final int ID_MENU_FEEDBACK = 9;
	public static final int ID_MENU_NEWS = 10;
	public static final int ID_MENU_WEIBO = 11;
	
	ListView mListView;
	MemuListAdapter mAdapter;
	ArrayList<DrawerItem> mMemus;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_drawer, container, false);
		mListView = (ListView) rootView.findViewById(R.id.lv_drawer_menu);
		mMemus = new ArrayList<DrawerItem>();
		mMemus.add(new DrawerAccountItem(ID_MENU_PROFILE));
		mMemus.add(new DrawerOrdinaryItem(ID_MENU_HOME, "Home"));
		mMemus.add(new DrawerOrdinaryItem(ID_MENU_FAVORATES, "My Favorites"));
		mMemus.add(new DrawerOrdinaryItem(ID_MENU_NEWS, "News"));
		mMemus.add(new DrawerOrdinaryItem(ID_MENU_WEIBO, "Weibo"));
		mMemus.add(new DrawerSecondaryItem(ID_MENU_SETTINGS, "Settings", R.drawable.ic_gear_40));
		mMemus.add(new DrawerSecondaryItem(ID_MENU_FEEDBACK, "Feedback", R.drawable.ic_drawer_feedback));
		if(mAdapter == null){
			mAdapter = new MemuListAdapter();
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int menuId = (int)id;
				switch (menuId) {
				case ID_MENU_PROFILE:
//					if(TextUtils.isEmpty(YYeTsApp.UserInfo.getUserId())){
						startActivity(new Intent(getActivity(), LoginActivity.class));
						((MainActivity)getActivity()).closeDrawer();
//					}
					break;
				case ID_MENU_HOME:
					
					break;
				case ID_MENU_FAVORATES:
					break;
				case ID_MENU_SETTINGS:
					startActivity(new Intent(getActivity(), SettingsActivty.class));
					((MainActivity)getActivity()).closeDrawer();
					break;
				case ID_MENU_FEEDBACK:
					startActivity(new Intent(getActivity(), FeedbackActivity.class));
					((MainActivity)getActivity()).closeDrawer();
					
					break;
				}
			}
			
		});
		return rootView;
	}
	
	class MemuListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mMemus.size();
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}
		
		@Override
		public int getItemViewType(int position) {
			return mMemus.get(position).getViewType();
		}
		
		@Override
		public Object getItem(int position) {
			return mMemus.get(position);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public long getItemId(int position) {
			return mMemus.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			DrawerItem item = mMemus.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(item.getLayoutId(), null);
			}
			switch (getItemViewType(position)) {
			case DrawerItem.ITEM_TYPE_REGULAR:
				TextView action = (TextView)convertView;
				action.setText(item.getTitle());
				break;
			case DrawerItem.ITEM_TYPE_SECONDARY:
				convertView.findViewById(R.id.action_separator_bottom).setVisibility((getCount()-1 == position) ? View.VISIBLE:View.GONE);
				TextView actionSec = (TextView) convertView.findViewById(R.id.action_text);
				actionSec.setText(item.getTitle());
				actionSec.setCompoundDrawablesWithIntrinsicBounds(((DrawerSecondaryItem)item).getIconId(), 0, 0, 0);
				break;
			}
			return convertView;
		}
		
	}
	
}
