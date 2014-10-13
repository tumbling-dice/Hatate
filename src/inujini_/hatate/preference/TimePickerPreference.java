package inujini_.hatate.preference;

import inujini_.hatate.R;
import lombok.Cleanup;
import lombok.val;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class TimePickerPreference extends Preference {

	private final String KEY_HOUR;
	private final String KEY_MINUTE;
	private int _defaultHour = 0;
	private int _defaultMinute = 0;


	public TimePickerPreference(Context context, String keyOfHour, String keyOfMinute) {
		super(context);

		if(keyOfHour == null || keyOfMinute == null) {
			throw new IllegalStateException("key_hour and key_minute must not be null.");
		}

		KEY_HOUR = keyOfHour;
		KEY_MINUTE = keyOfMinute;
	}

	public TimePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreference);
		val keyOfHour = t.getString(R.styleable.TimePickerPreference_key_hour);
		val keyOfMinute = t.getString(R.styleable.TimePickerPreference_key_minute);

		if(keyOfHour == null || keyOfMinute == null) {
			throw new IllegalStateException("key_hour and key_minute must not be null.");
		}

		_defaultHour = t.getInt(R.styleable.TimePickerPreference_defualt_hour, 0);
		_defaultMinute = t.getInt(R.styleable.TimePickerPreference_defualt_minute, 0);

		KEY_HOUR = keyOfHour;
		KEY_MINUTE = keyOfMinute;
	}

	public TimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreference);
		val keyOfHour = t.getString(R.styleable.TimePickerPreference_key_hour);
		val keyOfMinute = t.getString(R.styleable.TimePickerPreference_key_minute);

		if(keyOfHour == null || keyOfMinute == null) {
			throw new IllegalStateException("key_hour and key_minute must not be null.");
		}

		_defaultHour = t.getInt(R.styleable.TimePickerPreference_defualt_hour, 0);
		_defaultMinute = t.getInt(R.styleable.TimePickerPreference_defualt_minute, 0);

		KEY_HOUR = keyOfHour;
		KEY_MINUTE = keyOfMinute;
	}

	@Override
	protected void onClick() {
		super.onClick();
		val pref = getSharedPreferences();

		new TimePickerDialog(getContext(), new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {

				if(callChangeListener(picker)) {
					val innerPref = getSharedPreferences().edit();
					innerPref.putInt(KEY_HOUR, hourOfDay);
					innerPref.putInt(KEY_MINUTE, minute);
					innerPref.commit();
				}
			}
		}, pref.getInt(KEY_HOUR, _defaultHour), pref.getInt(KEY_MINUTE, _defaultMinute), true).show();;
	}

}
