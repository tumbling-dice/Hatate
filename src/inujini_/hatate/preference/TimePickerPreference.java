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
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;

/**
 * {@link TimePickerDialog}を表示する{@link Preference}.
 */
public class TimePickerPreference extends Preference {

	private String KEY_HOUR;
	private String KEY_MINUTE;
	private int _defaultHour = 0;
	private int _defaultMinute = 0;
	private Integer _icon;
	private boolean _isIconChanged;

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
		init(context, attrs);
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
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
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

		_icon = t.getResourceId(R.styleable.TimePickerPreference_android_icon, -1);
		_icon = _icon == -1 ? null : _icon;
	}

	@SuppressLint("Override")
	public int getIcon() {
		return _icon != null ? _icon : 0;
	}

	@SuppressLint("Override")
	public void setIcon(int resourceId) {
		_icon = resourceId != 0 ? resourceId : null;
		_isIconChanged = true;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		// on API version over 11
		ImageView imgView = (ImageView) view.findViewById(android.R.id.icon);

		if(imgView == null) {
			// on API version below 11
			imgView = (ImageView) view.findViewById(R.id.preferenceIcon);

			if(imgView == null) {
				imgView = (ImageView) LayoutInflater.from(getContext())
						.inflate(R.layout.icon_preference_imageview, null);

				((ViewGroup) view).addView(imgView, 0);
			}
		}

		Boolean isSetIcon = (Boolean) imgView.getTag();
		isSetIcon = isSetIcon == null ? false : isSetIcon;

		if(isSetIcon && !_isIconChanged) {
			return;
		}

		// init icon margin
		if(!isSetIcon) {
			val lp = (LinearLayout.LayoutParams) imgView.getLayoutParams();
			val density = getContext().getResources().getDisplayMetrics().density;
			lp.leftMargin = (int) (40f / density + 0.5f);
			imgView.setLayoutParams(lp);
		}

		if(_icon != null) {
			imgView.setImageResource(_icon);
			imgView.setVisibility(View.VISIBLE);
		} else {
			imgView.setImageResource(0);
			imgView.setVisibility(View.GONE);
		}

		// set "isSetIcon"
		imgView.setTag(true);
		_isIconChanged = false;

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
