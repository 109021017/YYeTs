package com.willme.yyets.menu;

import com.willme.yyets.R;

public abstract class DrawerItem {
	
	public static final int ITEM_VIEW_TYPE_COUNT = 3;
	
	public static final int ITEM_TYPE_REGULAR = 0;
	
	public static final int ITEM_TYPE_SECONDARY = 1;
	
	public static final int ITEM_TYPE_ACCOUNT = 2;

	private int id;
	protected String title;
	protected int iconId;
	private boolean visibility = true;
	
	public DrawerItem(int id){
		this.id = id;
	}
	
	public abstract int getLayoutId();
	
	public abstract int getViewType();
	
	public int getId(){
		return id;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getIconId(){
		return iconId;
	}
	
	public boolean isVisiable(){
		return visibility;
	}
	
	public void setVisibility(boolean v){
		this.visibility = v;
	}
	
	public static class DrawerOrdinaryItem extends DrawerItem{
		
		public DrawerOrdinaryItem(int id, String title) {
			super(id);
			this.title = title;
		}
		
		@Override
		public int getLayoutId(){
			return R.layout.item_drawer_regular;
		}

		@Override
		public int getViewType(){
			return ITEM_TYPE_REGULAR;
		}
	}
	
	public static class DrawerSecondaryItem extends DrawerItem{
		
		public DrawerSecondaryItem(int id, String title, int iconId) {
			super(id);
			this.title = title;
			this.iconId = iconId;
		}

		@Override
		public int getLayoutId(){
			return R.layout.item_drawer_secondary_action;
		}
		
		@Override
		public int getViewType() {
			return ITEM_TYPE_SECONDARY;
		}
	}
	
	public static class DrawerAccountItem extends DrawerItem{
		
		public DrawerAccountItem(int id) {
			super(id);
		}

		public int getLayoutId(){
			return R.layout.item_drawer_account;
		}
		
		@Override
		public int getViewType() {
			return ITEM_TYPE_ACCOUNT;
		}
	}
	
}
