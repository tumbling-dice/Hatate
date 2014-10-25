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
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class IconPreference extends Preference {

	private Integer _icon;
	private boolean _isIconChanged;

	public IconPreference(Context context) {
		super(context);
	}

	public IconPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public IconPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.IconPreference);
		_icon = t.getResourceId(R.styleable.IconPreference_android_icon, -1);
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

}
