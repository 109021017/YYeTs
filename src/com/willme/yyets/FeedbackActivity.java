package com.willme.yyets;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;

public class FeedbackActivity extends BaseActivity {
	private FeedbackAgent agent;
	private Conversation defaultConversation;
	private ReplyListAdapter adapter;
	private ListView replyListView;
	RelativeLayout header;
	int headerHeight;
	int headerPaddingOriginal;
	EditText userReplyContentEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.feedback_title);
		setContentView(R.layout.umeng_fb_activity_conversation);
		agent = new FeedbackAgent(this);
		defaultConversation = agent.getDefaultConversation();

		replyListView = (ListView) findViewById(R.id.umeng_fb_reply_list);

		adapter = new ReplyListAdapter(this);
		replyListView.setAdapter(adapter);
		replyListView.setSelection(adapter.getCount() - 1);
		sync();
		userReplyContentEdit = (EditText) findViewById(R.id.umeng_fb_reply_content);

		findViewById(R.id.umeng_fb_send).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

						String content = userReplyContentEdit.getEditableText()
								.toString().trim();
						if (TextUtils.isEmpty(content))
							return;

						userReplyContentEdit.getEditableText().clear();

						defaultConversation.addUserReply(content);

						sync();

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm != null)
							imm.hideSoftInputFromWindow(
									userReplyContentEdit.getWindowToken(), 0);
					}
				});

	}

	void sync() {
		Conversation.SyncListener listener = new Conversation.SyncListener() {

			@Override
			public void onSendUserReply(List<Reply> replyList) {
				adapter.notifyDataSetChanged();
				replyListView.setSelection(adapter.getCount() - 1);
			}

			@Override
			public void onReceiveDevReply(List<DevReply> replyList) {
			}
		};
		defaultConversation.sync(listener);
	}

	class ReplyListAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public ReplyListAdapter(Context context) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			List<Reply> replyList = defaultConversation.getReplyList();
			return (replyList == null) ? 0 : replyList.size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			Reply reply = defaultConversation.getReplyList().get(position);
			return reply instanceof DevReply ? 1 : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			Reply reply = defaultConversation.getReplyList().get(position);
			if (convertView == null) {
				convertView = mInflater
						.inflate(
								getItemViewType(position) == 0 ? R.layout.item_list_feedback_right
										: R.layout.item_list_feedback_left,
								null);

				holder = new ViewHolder();

				holder.replyDate = (TextView) convertView
						.findViewById(R.id.umeng_fb_reply_date);

				holder.replyContent = (TextView) convertView
						.findViewById(R.id.umeng_fb_reply_content);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.replyDate.setText(DateFormat.format(
					getString(R.string.date_time_format), reply.getDatetime()));
			holder.replyContent.setText(reply.getContent());
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return defaultConversation.getReplyList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			TextView replyDate;
			TextView replyContent;
		}
	}

}
