/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.preference;

import inujini_.hatate.service.Houtyou;
import lombok.val;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

/**
 * {@link Houtyou}の動作を確認する{@link IconPreference}.
 */
public class PreviewPreference extends IconPreference {

	/**
	 * {@link Houtyou}の動作を確認する{@link IconPreference}.
	 *
	 * @param context
	 */
	public PreviewPreference(Context context) {
		super(context);
	}

	/**
	 * {@link Houtyou}の動作を確認する{@link IconPreference}.
	 *
	 * @param context
	 * @param attrs
	 */
	public PreviewPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * {@link Houtyou}の動作を確認する{@link IconPreference}.
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PreviewPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onClick() {
		val intent = new Intent();
		intent.putExtra(Houtyou.KEY_IS_PREVIEW, true);
		new Houtyou().onReceive(getContext(), intent);
	}

}
