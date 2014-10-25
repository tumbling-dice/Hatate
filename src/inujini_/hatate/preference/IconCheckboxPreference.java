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
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 *
 */
public class IconCheckboxPreference extends CheckBoxPreference {

	private Integer _iconOn;
	private Integer _iconOff;
	private boolean _isIconChanged;

	/**
	 *
	 * @param context
	 */
	public IconCheckboxPreference(Context context) {
		super(context);
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 */
	public IconCheckboxPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public IconCheckboxPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.IconCheckboxPreference);
		_iconOn = t.getResourceId(R.styleable.IconCheckboxPreference_iconOn, -1);
		_iconOn = _iconOn == -1 ? null : _iconOn;
		_iconOff = t.getResourceId(R.styleable.IconCheckboxPreference_iconOff, -1);
		_iconOff = _iconOff == -1 ? null : _iconOff;
	}


	public int getIconOn() {
		return _iconOn != null ? _iconOn : 0;
	}

	public void setIconOn(int resourceId) {
		_iconOn = resourceId != 0 ? resourceId : null;
		_isIconChanged = true;
	}

	public int getIconOff() {
		return _iconOff != null ? _iconOff : 0;
	}

	public void setIconOff(int resourceId) {
		_iconOff = resourceId != 0 ? resourceId : null;
		_isIconChanged = true;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		setIcon(view, (getPersistedBoolean(false) ? _iconOn : _iconOff));
	}

	private void setIcon(View view, Integer resourceId) {
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

		if(resourceId != null) {
			imgView.setImageResource(resourceId);
			imgView.setVisibility(View.VISIBLE);
		} else {
			imgView.setImageResource(0);
			imgView.setVisibility(View.GONE);
		}

		// set "isSetIcon"
		imgView.setTag(true);
		_isIconChanged = false;
	}



}
