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
import inujini_.hatate.data.SpellCardHistory;

import java.util.List;

import lombok.val;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * スペルカード履歴一覧用ArrayAdapter.
 */
public class SpellCardHistoryAdapter extends ArrayAdapter<SpellCardHistory> {

	private LayoutInflater _inflater;

	public SpellCardHistoryAdapter(Context context, List<SpellCardHistory> objects) {
		super(context, 0, objects);
		_inflater = LayoutInflater.from(context);
	}

	static class ViewHolder {
		TextView name;
		TextView timestamp;

		ViewHolder(View v) {
			name = (TextView) v.findViewById(R.id.txvName);
			timestamp = (TextView) v.findViewById(R.id.txvTimestamp);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh;

		if(convertView == null) {
			view = _inflater.inflate(R.layout.adapter_spellcard_history, null);
			vh = new ViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}

		val data = getItem(position);

		vh.name.setText(data.getName());
		vh.timestamp.setText(data.getTimestamp());

		return view;
	}

}
