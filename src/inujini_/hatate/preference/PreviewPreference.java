package inujini_.hatate.preference;

import inujini_.hatate.service.Houtyou;
import lombok.val;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class PreviewPreference extends Preference {

	public PreviewPreference(Context context) {
		super(context);
	}

	public PreviewPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

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
