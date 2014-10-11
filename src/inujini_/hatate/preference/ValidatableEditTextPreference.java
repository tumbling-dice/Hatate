package inujini_.hatate.preference;

import lombok.val;
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

public class ValidatableEditTextPreference extends EditTextPreference {

	private boolean _isNeedValidation;
	private String _errorMessage;
	private TextValidator _validator;

	public interface TextValidator {
		boolean validation(String s);
	}

	public ValidatableEditTextPreference(Context context) {
		super(context);
	}

	public ValidatableEditTextPreference(Context context, int inputType) {
		super(context);
	}

	public ValidatableEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ValidatableEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAddEditTextToDialogView(View dialogView, final EditText editText) {
		super.onAddEditTextToDialogView(dialogView, editText);

		if(!_isNeedValidation) return;

		val container = (ViewGroup) dialogView
				.findViewById(Resources.getSystem().getIdentifier("edittext_container", "id","android"));

		val txtView = new TextView(getContext());
		txtView.setVisibility(View.GONE);
		txtView.setTextColor(Color.RED);

		container.addView(txtView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				val d = (AlertDialog) getDialog();
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

	public void setValidation(final String errorMessage, final TextValidator validator) {
		_isNeedValidation = true;
		_errorMessage = errorMessage;
		_validator = validator;
	}

}
