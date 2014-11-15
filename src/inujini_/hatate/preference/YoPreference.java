/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.preference;

import java.util.regex.Pattern;

import lombok.val;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;


/**
 *
 */
public class YoPreference extends IconEditTextPreference {

	public YoPreference(Context context) {
		super(context);
	}

	public YoPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YoPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected void onAddEditTextToDialogView(View dialogView, final EditText editText) {
		super.onAddEditTextToDialogView(dialogView, editText);
		editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		val p = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);

		val filter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (p.matcher(source).find()) {
					return source.toString().toUpperCase();
				}
                return "";
			}
		};

		editText.setFilters(new InputFilter[]{ filter });
	}
}
