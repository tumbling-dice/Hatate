package inujini_.hatate.preference;

import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class EventableListPreference extends ListPreference {

	public interface OnChosenListener {
		public void onChosen(int index, String entry, String entryValue);
	}

	private int _selectedEntryIndex;
	@Accessors(prefix="_") @Setter private OnChosenListener _onChosenListener;

	public EventableListPreference(Context context) {
		super(context);
	}

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