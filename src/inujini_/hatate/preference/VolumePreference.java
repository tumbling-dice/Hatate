package inujini_.hatate.preference;

import java.lang.ref.WeakReference;

import inujini_.hatate.R;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import android.content.Context;
import android.media.AudioManager;
import android.preference.Preference;
import android.util.AttributeSet;

public class VolumePreference extends SeekBarPreference {

	@Accessors(prefix="_") @Getter private int _volumeType;
	@Accessors(prefix="_") @Getter @Setter private OnPreferenceChangeListener _onPreferenceChangeListener;
	private WeakReference<AudioManager> _manager;

	public VolumePreference(Context context, int volumeType) {
		super(context, 0);

		if(volumeType < 0 || volumeType > 5)
			throw new IllegalArgumentException(String.format("you should use AudioManager's STREAM enums.\n volumeType:%d"
					, volumeType));

		_volumeType = volumeType;

		init(_volumeType, context);
	}

	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");

		init(_volumeType, context);
	}

	public VolumePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");

		init(_volumeType, context);
	}

	private void init(int volumeType, Context context) {
		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		super.setMax(am.getStreamMaxVolume(volumeType));
		super.setCurrentValue(am.getStreamVolume(volumeType));

		_manager = new WeakReference<AudioManager>(am);

		super.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				getAudioManager().setStreamVolume(_volumeType, (Integer) newValue, 0);

				if(_onPreferenceChangeListener != null)
					_onPreferenceChangeListener.onPreferenceChange(preference, newValue);

				return true;
			}
		});
	}

	@Override
	public void setCurrentValue(int currentValue) {
		super.setCurrentValue(currentValue);
		getAudioManager().setStreamVolume(_volumeType, currentValue, 0);
	}

	@Override
	public void setMax(int max) {
		throw new UnsupportedOperationException("VolumePreference's max value is defined by volume type and device.");
	}

	public void setVolumeType(int volumeType) {
		if(volumeType < 0 || volumeType > 5)
			throw new IllegalArgumentException(String.format("you should use AudioManager's STREAM enums.\n volumeType:%d"
					, volumeType));

		_volumeType = volumeType;
		val am = getAudioManager();
		super.setMax(am.getStreamMaxVolume(volumeType));
		super.setCurrentValue(am.getStreamVolume(volumeType));
	}

	private AudioManager getAudioManager() {
		AudioManager m = _manager.get();
		if(m == null) {
			m = ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE));
			_manager = new WeakReference<AudioManager>(m);
		}

		return m;
	}
}
