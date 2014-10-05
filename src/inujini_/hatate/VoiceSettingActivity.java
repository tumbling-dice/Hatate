package inujini_.hatate;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class VoiceSettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.voice_setting);
	}



}
