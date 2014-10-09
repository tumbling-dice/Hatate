package inujini_.hatate.reactive;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func;
import inujini_.function.Function.Func1;
import lombok.val;
import android.os.AsyncTask;

/**
 * AsyncTaskの各種イベントをクロージャでフックするクラス
 * @param <TSource> Background実行時に渡したいクラス
 * @param <TProgress> 途中経過で欲しいクラス
 * @param <TReturn> Backgroundから返ってくるクラス
 */
public class ReactiveAsyncTask<TSource, TProgress, TReturn> extends AsyncTask<TSource, TProgress, ReactiveAsyncResult<TReturn>> {

	/** 実行前イベント */
	private Action _onPreExecute;
	/** バックグラウンド処理 */
	private Func1<TSource, TReturn> _onBackground;
	/** _onProgressに渡す引数を作成するFunction */
	private Func<TProgress> _progressArg;
	/** 途中経過 */
	private Action1<TProgress> _onProgress;
	/** 実行後イベント */
	private Action1<TReturn> _onPostExecute;
	/** _onBackgroundでエラーが発生した時の処理 */
	private Action1<Exception> _onError;

	private boolean isCanceled;

	/**
	 * コンストラクタ
	 * @param onBackground バックグラウンドで実行する処理
	 */
	public ReactiveAsyncTask(Func1<TSource, TReturn> onBackground) {
		_onBackground = onBackground;
	}

	/**
	 * 実行前イベント
	 * @param action
	 * @return
	 */
	public ReactiveAsyncTask<TSource, TProgress, TReturn> setOnPreExecute(Action action) {
		_onPreExecute = action;
		return this;
	}

	/**
	 * 途中経過
	 * @param progressArg 途中経過として渡したい引数を作成するFunction
	 * @param action
	 * @return
	 */
	public ReactiveAsyncTask<TSource, TProgress, TReturn> setOnProgress(Func<TProgress> progressArg, Action1<TProgress> action) {
		_progressArg = progressArg;
		_onProgress = action;
		return this;
	}

	/**
	 * 実行後イベント
	 * @param action
	 * @return
	 */
	public ReactiveAsyncTask<TSource, TProgress, TReturn> setOnPostExecute(Action1<TReturn> action) {
		_onPostExecute = action;
		return this;
	}

	/**
	 * エラー時イベント
	 * @param action
	 * @return
	 */
	public ReactiveAsyncTask<TSource, TProgress, TReturn> setOnError(Action1<Exception> action) {
		_onError = action;
		return this;
	}

	@Override
	protected void onPreExecute() {
		if(_onPreExecute != null) _onPreExecute.call();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ReactiveAsyncResult<TReturn> doInBackground(TSource... arg0) {

		val r = new ReactiveAsyncResult<TReturn>();

		try {
			r.setResult(_onBackground.call(arg0[0]));
			if(_progressArg != null) publishProgress(_progressArg.call());
		} catch(RuntimeException e) {
			r.setError(e);
		}

		return r;
	}

	@Override
	protected void onProgressUpdate(TProgress... progress) {
		_onProgress.call(progress[0]);
	}

	@Override
	protected void onPostExecute(ReactiveAsyncResult<TReturn> r) {
		if(!r.hasError() && _onPostExecute != null) {
			_onPostExecute.call(r.getResult());
		} else {
			if(_onError != null) {
				_onError.call(r.getError());
			}
		}

		destroy();
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		this.isCanceled = true;
		destroy();
	}

	private void destroy() {
		_onPreExecute = null;
		_onBackground = null;
		_progressArg = null;
		_onProgress = null;
		_onPostExecute = null;
		_onError = null;
	}


}
