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
import inujini_.hatate.util.IconUtil;

import java.util.List;

import lombok.val;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * スペルカード履歴一覧用ArrayAdapter.
 * @see SpellCardHistoryActivity
 */
public class SpellCardHistoryAdapter extends ArrayAdapter<SpellCardHistory> {

	private LayoutInflater _inflater;

	/**
	 * スペルカード履歴一覧用ArrayAdapter.
	 * @param context
	 * @param objects
	 * @see SpellCardHistoryActivity
	 */
	public SpellCardHistoryAdapter(Context context, List<SpellCardHistory> objects) {
		super(context, 0, objects);
		_inflater = LayoutInflater.from(context);
	}

	static class SpellCardHistoryViewHolder {
		TextView name;
		TextView timestamp;
		ImageView icon;

		SpellCardHistoryViewHolder(View v) {
			name = (TextView) v.findViewById(R.id.txvName);
			timestamp = (TextView) v.findViewById(R.id.txvTimestamp);
			icon = (ImageView) v.findViewById(android.R.id.icon);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		SpellCardHistoryViewHolder vh;

		if(convertView == null) {
			view = _inflater.inflate(R.layout.adapter_spellcard_history, null);
			vh = new SpellCardHistoryViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (SpellCardHistoryViewHolder) view.getTag();
		}

		val data = getItem(position);

		vh.name.setText(data.getName());
		vh.timestamp.setText(data.getTimestamp());

		val cId = IconUtil.getIconId(data.getCharacterId());
		vh.icon.setImageResource(cId);
		vh.icon.setVisibility((cId == 0 ? View.GONE : View.VISIBLE));

		return view;
	}

}
