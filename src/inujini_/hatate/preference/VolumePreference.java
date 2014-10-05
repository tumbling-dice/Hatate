package inujini_.hatate.preference;

import inujini_.hatate.R;

import java.lang.ref.WeakReference;

import lombok.Cleanup;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import android.content.Context;
import android.media.AudioManager;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VolumePreference extends Preference implements OnSeekBarChangeListener {

	public final int MAX_VOLUME;
	@Accessors(prefix="_") @Getter private int _currentVolume;
	@Accessors(prefix="_") @Getter private final int _volumeType;

	private ProgressTracker _tracker;

	private static class ProgressTracker {
		private final WeakReference<TextView> textView;

		public ProgressTracker(TextView textView) {
			this.textView = new WeakReference<TextView>(textView);
		}

		public void track(int value) {
			if(textView.get() != null) textView.get().setText(String.format("設定値：%d", value));
		}
	}

	public VolumePreference(Context context, int volumeType) {
		super(context);

		if(volumeType < 0 || volumeType > 5)
			throw new IllegalArgumentException(String.format("you should use AudioManager's STREAM enums.\n volumeType:%d"
					, volumeType));

		_volumeType = volumeType;

		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		MAX_VOLUME = am.getStreamMaxVolume(volumeType);
		_currentVolume = am.getStreamVolume(volumeType);

		setLayoutResource(R.layout.volume_preference);
	}

	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");

		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		MAX_VOLUME = am.getStreamMaxVolume(_volumeType);
		_currentVolume = am.getStreamVolume(_volumeType);

		setLayoutResource(R.layout.volume_preference);
	}

	public VolumePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");

		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		MAX_VOLUME = am.getStreamMaxVolume(_volumeType);
		_currentVolume = am.getStreamVolume(_volumeType);

		setLayoutResource(R.layout.volume_preference);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		_tracker.track(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		_currentVolume = seekBar.getProgress();
		((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
			.setStreamVolume(_volumeType, _currentVolume, 0);
	}

	@Override
	protected void onBindView(View view) {
		((TextView) view.findViewById(R.id.txvTitle)).setText(getTitle());
		((TextView) view.findViewById(R.id.txvSummary)).setText(getSummary());
		((TextView) view.findViewById(R.id.txvMaxVolume)).setText(String.valueOf(MAX_VOLUME));

		val seekBar = (SeekBar) view.findViewById(R.id.skbVolume);
		seekBar.setMax(MAX_VOLUME);
		seekBar.setProgress(_currentVolume);
		seekBar.setOnSeekBarChangeListener(this);

		_tracker = new ProgressTracker((TextView) view.findViewById(R.id.txvCuttentVolume));
		_tracker.track(_currentVolume);

		super.onBindView(view);
	}

}
