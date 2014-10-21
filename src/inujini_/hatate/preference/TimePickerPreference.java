/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

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

/**
 * {@link TimePickerDialog}を表示する{@link Preference}.
 */
public class TimePickerPreference extends Preference {

	private final String KEY_HOUR;
	private final String KEY_MINUTE;
	private int _defaultHour = 0;
	private int _defaultMinute = 0;

	/**
	 * {@link TimePickerDialog}を表示する{@link Preference}.
	 * 
	 * @param context
	 * @param keyOfHour 時間を保存するkey
	 * @param keyOfMinute 分を保存するkey
	 * @throws IllegalStateException keyOfHourもしくはkeyOfMinuteがnull.
	 */
	public TimePickerPreference(Context context, String keyOfHour, String keyOfMinute) {
		super(context);

		if(keyOfHour == null || keyOfMinute == null) {
			throw new IllegalStateException("key_hour and key_minute must not be null.");
		}

		KEY_HOUR = keyOfHour;
		KEY_MINUTE = keyOfMinute;
	}

	/**
	 * {@link TimePickerDialog}を表示する{@link Preference}.
	 * 
	 * @param context
	 * @param attrs
	 * @throws IllegalStateException key_hourもしくはkey_minuteが設定されていない.
	 */
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

	/**
	 * {@link TimePickerDialog}を表示する{@link Preference}.
	 * 
	 * @param context
	 * @param attrs
	 * @params defStyle
	 * @throws IllegalStateException key_hourもしくはkey_minuteが設定されていない.
	 */
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
