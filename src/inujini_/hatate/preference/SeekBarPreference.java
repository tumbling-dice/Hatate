package inujini_.hatate.preference;

import inujini_.hatate.R;

import java.lang.ref.WeakReference;

import lombok.Cleanup;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {

	@Accessors(prefix="_") @Getter private int _max;
	@Accessors(prefix="_") @Getter private int _currentValue;

	private WeakReference<TextView> _txvValue;
	private WeakReference<SeekBar> _seekBar;

	private static final class SeekBarPreferenceViewHolder {
		public final TextView txtValue;
		public final SeekBar seekBar;

		public SeekBarPreferenceViewHolder(View view) {
			txtValue = (TextView) view.findViewById(R.id.txvValue);
			seekBar = (SeekBar) view.findViewById(R.id.seekbar);
		}
	}

	public SeekBarPreference(Context context, int max) {
		super(context);
		_max = max;
	}

	public SeekBarPreference(Context context, int max, int currentValue) {
		super(context);
		_max = max;
		_currentValue = currentValue;
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);

		// get max value
		_max = t.getInt(R.styleable.SeekBarPreference_max, 0);

		// get default value
		// constractor can not get persisted value
		// because SheredPreference(PreferenceManager) has not initialize yet.
		_currentValue = t.getInt(R.styleable.SeekBarPreference_android_defaultValue, 0);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		// set persited value
		val currentValue = super.getPersistedInt(-1);
		_currentValue = currentValue != -1 ? currentValue : _currentValue;

		val root = (LinearLayout) super.onCreateView(parent);
		for (int i = 0, size = root.getChildCount(); i < size; i++) {
			val v = root.getChildAt(i);
			if(!(v instanceof RelativeLayout)) continue;

			val r = (RelativeLayout) v;
			val seekbarLayout
				= (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.seekbar_preference, null);

			val params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT
					, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, Resources.getSystem().getIdentifier("summary", "id", "android"));

			val density = getContext().getResources().getDisplayMetrics().density;
			// 10dp
			params.topMargin = (int) (10f / density + 0.5f);

			seekbarLayout.setLayoutParams(params);
			r.addView(seekbarLayout);

			break;
		}

		return root;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		val tag = view.getTag();
		SeekBarPreferenceViewHolder vh = null;

		if(tag != null && tag instanceof SeekBarPreferenceViewHolder) {
			vh = (SeekBarPreferenceViewHolder) tag;
		} else {
			vh = new SeekBarPreferenceViewHolder(view);
			view.setTag(vh);
			vh.seekBar.setOnSeekBarChangeListener(this);
		}

		vh.seekBar.setProgress(_currentValue);
		vh.seekBar.setMax(_max);

		vh.txtValue.setText(String.format("%d/%d", _currentValue, _max));
		if(_txvValue != null) _txvValue.clear();
		_txvValue = new WeakReference<TextView>(vh.txtValue);

		if(_seekBar != null) _seekBar.clear();
		_seekBar = new WeakReference<SeekBar>(vh.seekBar);
	}

	public void setMax(int max) {
		_max = max;
		changeTextView();

		if(_seekBar == null) return;

		val seekBar = _seekBar.get();
		if(seekBar != null) {
			seekBar.setMax(max);
			seekBar.setProgress(_currentValue);
		}
	}

	public void setCurrentValue(int currentValue) {
		_currentValue = currentValue;
		changeTextView();

		if(_seekBar == null) return;

		val seekBar = _seekBar.get();
		if(seekBar != null) seekBar.setProgress(currentValue);

		saveValue(_currentValue);
	}

	private void changeTextView() {
		if(_txvValue == null) return;

		val txtView = _txvValue.get();
		if(txtView != null) txtView.setText(String.format("%d/%d", _currentValue, _max));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		_currentValue = progress;
		changeTextView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		_currentValue = seekBar.getProgress();
		saveValue(_currentValue);
	}

	private void saveValue(int v) {
		if(super.callChangeListener(v))
			super.getEditor().putInt(super.getKey(), v).commit();
	}

}
