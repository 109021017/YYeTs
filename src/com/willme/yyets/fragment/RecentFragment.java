package com.willme.yyets.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.willme.yyets.R;
import com.willme.yyets.api.FavoriteRequest;
import com.willme.yyets.api.FavoriteResponse;
import com.willme.yyets.db.DBHelper;
import com.willme.yyets.entities.YYResource;
import com.willme.yyets.push.NotificationService;
import com.willme.yyets.utils.ImageFetcher;

public class RecentFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<ArrayList<YYResource>>, OnScrollListener,
		OnItemClickListener {

    private ImageFetcher mImageFetcher;

	private static final String STATE_POSITION = "position";
	private static final String STATE_TOP = "top";
	private int mListViewStateTop;
	private int mListViewStatePosition;

	private ArrayList<YYResource> mFavorites = new ArrayList<YYResource>();
	private ListView mListView;
	private RecentAdapter mAdapter;
	private static final int RECENT_LOADER_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageFetcher = ImageFetcher.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mListViewStatePosition = savedInstanceState.getInt(STATE_POSITION,
					-1);
			mListViewStateTop = savedInstanceState.getInt(STATE_TOP, 0);
		} else {
			mListViewStatePosition = -1;
			mListViewStateTop = 0;
		}
		View rootView = inflater.inflate(R.layout.fragment_recent, container,
				false);
		mListView = (ListView) rootView.findViewById(R.id.listView);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mAdapter = new RecentAdapter();
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(RECENT_LOADER_ID, null, this);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (isAdded()) {
			View v = mListView.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			outState.putInt(STATE_POSITION, mListView.getFirstVisiblePosition());
			outState.putInt(STATE_TOP, top);
		}
		super.onSaveInstanceState(outState);
	}

	public void refresh() {
		refresh(false);
	}

	public void refresh(boolean forceRefresh) {
		if (isListLoading() && !forceRefresh) {
			return;
		}

		mFavorites.clear();
		mAdapter.notifyDataSetInvalidated();

		if (isAdded()) {
			Loader<ArrayList<YYResource>> loader = getLoaderManager()
					.getLoader(RECENT_LOADER_ID);
			((RecentLoader) loader).init();
		}

		loadMoreResults();
	}

	public void loadMoreResults() {
		if (isAdded()) {
			Loader<ArrayList<YYResource>> loader = getLoaderManager()
					.getLoader(RECENT_LOADER_ID);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {
		/*
		 * // Pause disk cache access to ensure smoother scrolling if
		 * (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
		 * mImageLoader.stopProcessingQueue(); } else {
		 * mImageLoader.startProcessingQueue(); }
		 */
	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (!isListLoading() && loaderHasMoreResults() && visibleItemCount != 0
				&& firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
			loadMoreResults();
		}
	}

	@Override
	public Loader<ArrayList<YYResource>> onCreateLoader(int id, Bundle args) {
		return new RecentLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<YYResource>> listLoader,
			ArrayList<YYResource> result) {
		if (result != null) {
			mFavorites = result;
		}
		mAdapter.notifyDataSetChanged();
		if (mListViewStatePosition != -1 && isAdded()) {
			mListView.setSelectionFromTop(mListViewStatePosition,
					mListViewStateTop);
			mListViewStatePosition = -1;
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<YYResource>> listLoader) {
	}

	private boolean isListLoading() {
		if (isAdded()) {
			final Loader<ArrayList<YYResource>> loader = getLoaderManager()
					.getLoader(RECENT_LOADER_ID);
			if (loader != null) {
				return ((RecentLoader) loader).isLoading();
			}
		}
		return true;
	}

	private boolean loaderHasMoreResults() {
		if (isAdded()) {
			final Loader<ArrayList<YYResource>> loader = getLoaderManager()
					.getLoader(RECENT_LOADER_ID);
			if (loader != null) {
				return ((RecentLoader) loader).hasMoreResults();
			}
		}
		return false;
	}

	private boolean loaderHasError() {
		if (isAdded()) {
			final Loader<ArrayList<YYResource>> loader = getLoaderManager()
					.getLoader(RECENT_LOADER_ID);
			if (loader != null) {
				return ((RecentLoader) loader).hasError();
			}
		}
		return false;
	}

	private static class RecentLoader extends
			AsyncTaskLoader<ArrayList<YYResource>> {

		private ArrayList<YYResource> mFavorites;
		private boolean mHasError;
		private boolean mIsLoading;
		private int mCurPage;
		private int mTotalPage;
		private boolean mLoadFromDb;
		private DBHelper mDbHelper;

		public RecentLoader(Context context) {
			super(context);
			init();
		}

		public void init() {
			mIsLoading = false;
			mHasError = false;
			mCurPage = 0;
			mTotalPage = -1;
			mLoadFromDb = true;
			if (mDbHelper == null) {
				mDbHelper = new DBHelper(getContext());
			}
		}

		@Override
		public ArrayList<YYResource> loadInBackground() {
			mIsLoading = true;
			if (mLoadFromDb) {
				ArrayList<YYResource> list;
				list = mDbHelper.getFavoriteRes();
				return list;
			}
			FavoriteResponse favResponse = null;
			try {
				favResponse = FavoriteRequest.getFavoriteList(mCurPage + 1);
			} catch (Exception e) {
				e.printStackTrace();
				mHasError = true;
			}
			if (favResponse != null) {
				mCurPage = favResponse.getCurrentPage();
				mTotalPage = favResponse.getTotalPage();
				ArrayList<YYResource> list = favResponse.getFavoriteList();
				if (mCurPage == 1 && list != null && list.size() > 0) {
					NotificationService.findUpdateAndNotfy(getContext(),
							mDbHelper, list);
					mDbHelper.updateFavoriteRes(list);
				}
				return list;
			} else {
				return null;
			}
		}

		@Override
		public void deliverResult(ArrayList<YYResource> data) {
			mIsLoading = false;
			if (data != null) {
				if (mLoadFromDb) {
					mLoadFromDb = false;
					forceLoad();
				}
				if (mFavorites == null) {
					mFavorites = data;
				} else {
					if (mCurPage <= 1) {
						mFavorites.clear();
					}
					mFavorites.addAll(data);
				}
			}
			if (isStarted()) {
				super.deliverResult(mFavorites == null ? null
						: new ArrayList<YYResource>(mFavorites));
			}
		}

		public boolean isLoading() {
			return mIsLoading;
		}

		public boolean hasMoreResults() {
			return mCurPage != mTotalPage;
		}

		public boolean hasError() {
			return mHasError;
		}

		@Override
		protected void onStartLoading() {
			if (mFavorites != null) {
				deliverResult(null);
			} else {
				forceLoad();
			}
		}

		@Override
		protected void onStopLoading() {
			mIsLoading = false;
			cancelLoad();
		}

		@Override
		protected void onReset() {
			super.onReset();
			onStopLoading();
			mFavorites = null;
		}

		@SuppressWarnings("unused")
		public void refresh() {
			reset();
			startLoading();
		}

	}

	private class RecentAdapter extends BaseAdapter {

		private static final int VIEW_TYPE_RESOURCE = 0;
		private static final int VIEW_TYPE_LOADING = 1;

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return getItemViewType(position) == VIEW_TYPE_RESOURCE;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public int getCount() {
			return mFavorites.size() + (
			// show the status list row if...
					((isListLoading() && mFavorites.size() == 0) // ...this is
																	// the first
																	// load
							|| loaderHasMoreResults() // ...or there's another
														// page
					|| loaderHasError()) // ...or there's an error
					? 1
							: 0);
		}

		@Override
		public int getItemViewType(int position) {
			return (position >= mFavorites.size()) ? VIEW_TYPE_LOADING
					: VIEW_TYPE_RESOURCE;
		}

		@Override
		public Object getItem(int position) {
			return (getItemViewType(position) == VIEW_TYPE_RESOURCE) ? mFavorites
					.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			// TODO: better unique ID heuristic
			return (getItemViewType(position) == VIEW_TYPE_RESOURCE) ? mFavorites
					.get(position).getId().hashCode()
					: -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (getItemViewType(position) == VIEW_TYPE_LOADING) {
				if (convertView == null) {
					convertView = getLayoutInflater(null).inflate(
							R.layout.item_list_recent_status, parent, false);
				}

				if (loaderHasError()) {
					convertView.findViewById(android.R.id.progress)
							.setVisibility(View.GONE);
					((TextView) convertView.findViewById(android.R.id.text1))
							.setText(R.string.load_error);
				} else {
					convertView.findViewById(android.R.id.progress)
							.setVisibility(View.VISIBLE);
					((TextView) convertView.findViewById(android.R.id.text1))
							.setText(R.string.loading);
				}

				return convertView;

			} else {
				YYResource resource = (YYResource) getItem(position);
				if (convertView == null) {
					convertView = getLayoutInflater(null).inflate(
							R.layout.item_list_recent, parent, false);
				}
				TextView tv_Title = (TextView) convertView
						.findViewById(R.id.tv_title);
				tv_Title.setText(resource.getName());
				TextView tv_Desc = (TextView) convertView
						.findViewById(R.id.tv_desc);
				tv_Desc.setText(resource.getUpdateInfo());
				TextView tv_Time = (TextView) convertView
						.findViewById(R.id.tv_time);
				tv_Time.setText(resource.getUpdateTimeFormated(getActivity()));
				String imgurl = resource.getBigImgUrl();
				if(!TextUtils.isEmpty(imgurl)){
					ImageView iv_Cover = (ImageView) convertView.findViewById(R.id.iv_cover);
					mImageFetcher.loadImage(imgurl, iv_Cover);
				}
				return convertView;
			}
		}
	}

}
