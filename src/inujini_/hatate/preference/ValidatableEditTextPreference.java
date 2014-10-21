/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.preference;

import lombok.val;
import lombok.NonNull;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ���͓��e�����؂��A���[���Ɉᔽ���Ă����ꍇ�͒l��o�^�����Ȃ�{@link EditTextPreference}.
 */
public class ValidatableEditTextPreference extends EditTextPreference {

	private boolean _isNeedValidation;
	private String _errorMessage;
	private TextValidator _validator;

	/**
	 * ���͓��e���؃��[��.
	 */
	public interface TextValidator {
		/**
		 * ���͓��e����.
		 * 
		 * @param s ���͓��e
		 * @return ���[���ɓK���Ă���ꍇ��true�A�ᔽ���Ă���ꍇ��false.
		 */
		boolean validation(String s);
	}

	/**
	 * ���͓��e�����؂��A���[���Ɉᔽ���Ă����ꍇ�͒l��o�^�����Ȃ�{@link EditTextPreference}.
	 * 
	 * @param context
	 */
	public ValidatableEditTextPreference(Context context) {
		super(context);
	}

	/**
	 * ���͓��e�����؂��A���[���Ɉᔽ���Ă����ꍇ�͒l��o�^�����Ȃ�{@link EditTextPreference}.
	 * 
	 * @param context
	 * @param attrs
	 */
	public ValidatableEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ���͓��e�����؂��A���[���Ɉᔽ���Ă����ꍇ�͒l��o�^�����Ȃ�{@link EditTextPreference}.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ValidatableEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAddEditTextToDialogView(View dialogView, final EditText editText) {
		super.onAddEditTextToDialogView(dialogView, editText);

		if(!_isNeedValidation) return;

		// val container = (ViewGroup) dialogView.findViewById(android.R.id.edittext_container);
		val container = (ViewGroup) dialogView
				.findViewById(Resources.getSystem().getIdentifier("edittext_container", "id","android"));

		val txtView = new TextView(getContext());
		txtView.setVisibility(View.GONE);
		txtView.setTextColor(Color.RED);
		txtView.setTag("txtValidation");

		container.addView(txtView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

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
				val d = (AlertDialog) super.getDialog();
				if(d == null) return;

				val btn = d.getButton(DialogInterface.BUTTON_POSITIVE);

				if (!_validator.validation(s.toString()) && txtView != null) {
					txtView.setVisibility(View.VISIBLE);
					txtView.setText(_errorMessage);
					btn.setEnabled(false);
				} else {
					txtView.setVisibility(View.GONE);
					btn.setEnabled(true);
				}
			}
		});
	}

	/**
	 * ���؃��[���o�^.
	 * 
	 * @param errorMessage ���[���ᔽ���ɕ\�����郁�b�Z�[�W
	 * @param validator ���؃��[��
	 */
	public void setValidation(@NonNull String errorMessage, @NonNull TextValidator validator) {
		_isNeedValidation = true;
		_errorMessage = errorMessage;
		_validator = validator;
	}

}
