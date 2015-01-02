/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.adapter;

import inujini_.hatate.R;
import inujini_.hatate.data.TwitterAccount;

import java.util.List;

import lombok.val;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

/**
 * TwitterAccountを表示するArrayAdapter
 * @see AccountListActivity
 */
public class AccountAdapter extends ArrayAdapter<TwitterAccount> {

	private final LayoutInflater _inflater;

	/**
	 * TwitterAccountを表示するArrayAdapter
	 * @param context
	 * @param list
	 * @see AccountListActivity
	 */
	public AccountAdapter(Context context, List<TwitterAccount> list) {
		super(context, 0, list);
		_inflater = LayoutInflater.from(context);
	}

	static class AccountViewHolder {
		CheckedTextView name;

		AccountViewHolder(View v) {
			name = (CheckedTextView) v.findViewById(android.R.id.text1);
		}
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		View view = convertView;
		AccountViewHolder vh = null;

		if(view == null) {
			view = _inflater.inflate(R.layout.adapter_account_list, null);
			vh = new AccountViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (AccountViewHolder) view.getTag();
		}

		val item = getItem(position);

		vh.name.setText(item.getScreenName());
		vh.name.setChecked(item.isUse());

		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
