/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.preference;

import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * アイテム選択時にイベントを発生させる{@link ListPreference}.
 */
public class EventableListPreference extends ListPreference {

	/**
	 * アイテム選択時のイベントリスナー.
	 */
	public interface OnChosenListener {
		/**
		 * アイテム選択時イベント
		 * 
		 * @param index アイテムの位置
		 * @param entry 選択された値（android:entries）
		 * @param entryValue 選択された値（android:entryValues）
		 */
		public void onChosen(int index, String entry, String entryValue);
	}

	private int _selectedEntryIndex;
	@Accessors(prefix="_") @Setter private OnChosenListener _onChosenListener;

	/**
	 * アイテム選択時にイベントを発生させる{@link ListPreference}.
	 * 
	 * @param context
	 */
	public EventableListPreference(Context context) {
		super(context);
	}

	/**
	 * アイテム選択時にイベントを発生させる{@link ListPreference}.
	 * 
	 * @param context
	 * @param attrs
	 */
	public EventableListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

		val entries = super.getEntries();
		val entryValues = super.getEntryValues();

		if (entries == null || entryValues == null) {
			throw new IllegalStateException(
					"EventableListPreference requires an entries array and an entryValues array.");
		}

		builder.setSingleChoiceItems(entries, super.findIndexOfValue(super.getValue())
			, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_selectedEntryIndex = which;
					if(_onChosenListener != null) {
						_onChosenListener.onChosen(which
								, EventableListPreference.super.getEntries()[which].toString()
								, EventableListPreference.super.getEntryValues()[which].toString());
					}
				}
		});

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventableListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
				dialog.dismiss();
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		val entryValues = super.getEntryValues();

		if (positiveResult && _selectedEntryIndex >= 0 && entryValues != null) {
			val value = entryValues[_selectedEntryIndex].toString();
			if (callChangeListener(value)) {
				super.setValue(value);
			}
		}
	}

}