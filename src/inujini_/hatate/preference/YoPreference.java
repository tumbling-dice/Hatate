/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.preference;


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

	@Override
	protected void onAddEditTextToDialogView(View dialogView, final EditText editText) {
		super.onAddEditTextToDialogView(dialogView, editText);
		
		editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

		/*
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// not implement
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// not implement
			}

			@Override
			public void afterTextChanged(Editable s) {
				// not implement
			}
		});
		*/
	}
}
