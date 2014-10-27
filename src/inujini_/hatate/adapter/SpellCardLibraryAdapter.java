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
import inujini_.hatate.data.SpellCard;
import inujini_.hatate.data.TouhouData;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.util.IconUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 *
 * @param <K> Group(親)として表示されるクラス
 */
@ExtensionMethod({Linq.class})
public class SpellCardLibraryAdapter<K extends TouhouData> extends BaseExpandableListAdapter {

	private final Map<K, List<SpellCard>> _items;
	//private final Context _context;
	private final LayoutInflater _inflater;

	static class PViewHolder {
		final TextView name;

		PViewHolder(View v) {
			name = (TextView) v.findViewById(R.id.txvName);
		}
	}

	static class CViewHolder {
		final TextView name;
		final ImageView icon;

		CViewHolder(View v) {
			name = (TextView) v.findViewById(R.id.txvName);
			icon = (ImageView) v.findViewById(android.R.id.icon);
		}
	}

	/**
	 *
	 * @param items
	 * @param context
	 */
	public SpellCardLibraryAdapter(Map<K, List<SpellCard>> items, Context context) {
		_items = items;
		//_context = context;
		_inflater = LayoutInflater.from(context);
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return getChildren(groupPosition).get(childPosition);
	}

	public List<SpellCard> getChildren(int groupPosition) {
		val id = groupPosition + 1;
		for (val item : _items.entrySet()) {
			val series = item.getKey();
			if(series.getId() == id) {
				return item.getValue();
			}
		}

		return new ArrayList<SpellCard>();
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return ((SpellCard) getChild(groupPosition, childPosition)).getId();
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild
			, View convertView, ViewGroup parent) {

		View view = convertView;
		CViewHolder vh = null;

		if(view == null) {
			view = _inflater.inflate(R.layout.adapter_spellcard_library_child, null);
			vh = new CViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (CViewHolder) view.getTag();
		}

		val item = (SpellCard) getChild(groupPosition, childPosition);
		vh.name.setText(item.getName());

		val cId = IconUtil.getIconId(item.getCharacterId());
		vh.icon.setImageResource(cId);
		vh.icon.setVisibility((cId == 0 ? View.GONE : View.VISIBLE));

		return view;
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return getChildren(groupPosition).size();
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		val id = groupPosition + 1;
		for (val item : _items.keySet()) {
			if(item.getId() == id) return item;
		}
		return null;
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return _items.keySet().size();
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		View view = convertView;
		PViewHolder vh = null;

		if(view == null) {
			view = _inflater.inflate(R.layout.adapter_spellcard_library_parent, null);
			vh = new PViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (PViewHolder) view.getTag();
		}

		val item = (K) getGroup(groupPosition);
		vh.name.setText(String.format("%s (%d)", item.getName(), getChildrenCount(groupPosition)));

		return view;
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	/* (非 Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
