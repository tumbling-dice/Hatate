/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.util;

import inujini_.hatate.NotificationActivity;
import inujini_.hatate.function.Function.Action;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.service.Houtyou;
import inujini_.hatate.service.OneMoreLovely;
import inujini_.hatate.sqlite.DatabaseHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.Calendar;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 行き場のないpublic staticなメソッドを集めたUtility class.
 */
@ExtensionMethod({PrefGetter.class, Linq.class})
public class Util {

	private static SoftReference<AlarmManager> _alarmManager;

	/**
	 * アラームをセットする時間の取得.
	 * @param hour
	 * @param minute
	 * @return <p>今日 + hour + minute + 0秒のミリ秒.</p>
	 *         <p>ただし、現在時刻より前のhour + minuteが指定された場合は翌日 + hour + minute + 0秒のミリ秒となる.</p>
	 */
	@SuppressLint("DefaultLocale")
	public static long getAlarmTime(int hour, int minute) {
		val calendar = Calendar.getInstance();

		// 現在時刻とhour + minuteを比較し、現在時刻の方が大きかったら
		// 日を+1する
		// Note: AlarmManagerで現在日時より前の値をセットするとその瞬間に起動してしまう
		val currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		val currentMinute = calendar.get(Calendar.MINUTE);

		if(Integer.parseInt(String.format("%02d%02d", hour, minute))
				<= Integer.parseInt(String.format("%02d%02d", currentHour, currentMinute))) {
			calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
		}

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		// 0秒を明示的にセットしておかないと中途半端な時間にアラームが起動する
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTimeInMillis();
	}

	/**
	 * <p>アラームのセット.</p>
	 * <p>{@link NotificationActivity}で設定した時刻をもとに1日おきに定時実行するIntentを設定する.</p>
	 * @param context
	 * @see #getAlarmTime(int, int)
	 * @see #getAlarmIntent(Context)
	 * @see AlarmManager#setRepeating(int, long, long, PendingIntent)
	 */
	public static void setAlarm(Context context) {
		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP
				, getAlarmTime(context.getHour(), context.getMinute())
				, AlarmManager.INTERVAL_DAY, getAlarmIntent(context));
	}

	/**
	 * アラームの解除.
	 * @param context
	 * @see #getAlarmIntent(Context)
	 * @see AlarmManager#cancel(PendingIntent)
	 */
	public static void removeAlarm(Context context) {
		getAlarmManager(context).cancel(getAlarmIntent(context));
	}

	/**
	 * アラームとして定時実行するPendingIntentの作成.
	 * @param context
	 * @return {@link Houtyou}を実行するPendingIntent
	 * @see PendingIntent#getBroadcast(Context, int, Intent, int)
	 */
	public static PendingIntent getAlarmIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, Houtyou.class)
			, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	/**
	 * <p>スヌーズ設定.</p>
	 * <p>現在日時のミリ秒 + {@link NotificationActivity}で設定したスヌーズ間隔のミリ秒で実行するIntentを設定する.</p>
	 * @param context
	 * @see #getSnoozeIntent(Context)
	 * @see PrefGetter#getSnoozeTimeMill()
	 * @see AlarmManager#set(int, long, PendingIntent)
	 */
	public static void setSnooze(Context context) {
		getAlarmManager(context).set(AlarmManager.RTC_WAKEUP
				, (System.currentTimeMillis() + context.getSnoozeTimeMill())
				, getSnoozeIntent(context));
	}

	/**
	 * スヌーズ解除.
	 * @param context
	 * @see #getSnoozeIntent(Context)
	 * @see AlarmManager#cancel(PendingIntent)
	 */
	public static void removeSnooze(Context context) {
		getAlarmManager(context).cancel(getSnoozeIntent(context));
	}

	/**
	 * スヌーズとして定時実行するPendingIntentの作成.
	 * @param context
	 * @return {@link OneMoreLovely}を実行するPendingIntent
	 * @see PendingIntent#getBroadcast(Context, int, Intent, int)
	 */
	public static PendingIntent getSnoozeIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, OneMoreLovely.class)
				, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	public static void setRepeatYo(Context context) {
		context.getSharedPreference(RepeatYoService.PREF_YO, 0)
			.edit().putBoolean(RepeatYoService.IS_SENT_YO, false).commit();
		
		getAlarmManager(context).setRepeating(AlarmManager.RTC
			, System.currentTimeMillis() + 1
			, 65000L
			, getRepeatYoIntent(context));
	}
	
	public static PendingIntent getRepeatYoIntent(Context context) {
		return PendingIntent.getService(context, 0, new Intent(context, RepeatYoService.class)
				, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	public static void removeRepeatYo(Context context) {
		context.getSharedPreference(RepeatYoService.PREF_YO, 0)
			.edit().putBoolean(RepeatYoService.IS_SENT_YO, true).commit();
		getAlarmManager(context).cancel(getRepeatYoIntent(context));
	}

	/**
	 * AlarmManagerの取得とキャッシング.
	 * @param context
	 * @return contextから取得した{@link AlarmManager} or キャッシュされた{@link AlarmManager}
	 * @see Context#getSystemService(String)
	 * @see Context#ALARM_SERVICE
	 */
	public static AlarmManager getAlarmManager(Context context) {
		AlarmManager am = null;
		if(_alarmManager != null) {
			am = _alarmManager.get();
			if(am != null) return am;
		}

		// Note: Context#getSystemServiceはめちゃめちゃ重いのでキャッシュしておくにこしたことはない
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		_alarmManager = new SoftReference<AlarmManager>(am);
		return am;
	}

	/**
	 * assets内のファイルをInputStreamReaderとして取得.
	 * @param context
	 * @param fileName ファイル名
	 * @return fileNameで指定されたファイルのInputStreamReader
	 * @throws IOException
	 */
	public static InputStreamReader getAssetStream(Context context, String fileName) throws IOException {
		return new InputStreamReader(context.getResources().getAssets().open(fileName));
	}

	/**
	 * <p>内部DBのアップデート（非同期版）.</p>
	 * <p>DBがまだ作成されていない、もしくはアップデートされていない場合に実行.</p>
	 * <p>ProgressDialogを表示し、非同期処理でDBのアップデートを行う.</p>
	 * @param context ActivityContext（{@link ProgressDialog}を呼び出すため）
	 * @see dbUpdate(Context)
	 * @see DatabaseHelper#isDbOpened(Context)
	 * @see DatabaseHelper#isDbUpdated(Context)
	 */
	public static void dbUpdateAsync(final Context context) {
		if(!DatabaseHelper.isDbOpened(context)
			|| !DatabaseHelper.isDbUpdated(context)) {

			val prog = new ProgressDialog(context);
			prog.setTitle("DB Update");
			prog.setMessage("内部データベースを更新しています...");
			prog.setCancelable(false);
			prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			new ReactiveAsyncTask<Context, Void, Void>(new Func1<Context, Void>() {
				@Override
				public Void call(Context c) {
					val d = new DatabaseHelper(c);
					d.getWritableDatabase().close();
					d.close();
					return null;
				}
			}).setOnPreExecute(new Action() {
				@Override
				public void call() {
					prog.show();
				}
			}).setOnPostExecute(new Action1<Void>() {
				@Override
				public void call(Void arg0) {
					if(prog != null && prog.isShowing())
						prog.dismiss();
				}
			}).setOnError(new Action1<Exception>() {
				@Override
				public void call(Exception e) {
					if(prog != null && prog.isShowing())
						prog.dismiss();

					if(e != null) e.printStackTrace();
					Toast.makeText(context, "エラーが発生しました。", Toast.LENGTH_SHORT).show();

				}
			}).execute(context);
		}
	}

	/**
	 * <p>内部DBのアップデート.</p>
	 * <p>DBがまだ作成されていない、もしくはアップデートされていない場合にのみ実行.</p>
	 * @param context
	 * @see dbUpdateAsync(Context)
	 * @see DatabaseHelper#isDbOpened(Context)
	 * @see DatabaseHelper#isDbUpdated(Context)
	 */
	public static void dbUpdate(Context context) {
		if(!DatabaseHelper.isDbOpened(context)
				|| !DatabaseHelper.isDbUpdated(context)) {
			val d = new DatabaseHelper(context);
			d.getWritableDatabase().close();
			d.close();
		}
	}

	/* シリアライズ / デシリアライズ
	public static <T extends Serializable> void serialize(T obj, String fileName, Context context) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			try {
				fos = context.openFileOutput(fileName, 0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			try {
				if (fos != null) {
					oos = new ObjectOutputStream(fos);
					if (oos != null) oos.writeObject(obj);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} finally {
			if(fos != null) {
				try {
					if(oos != null) oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(String fileName, Context context) {

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		T obj = null;

		try {
			try {
				fis = context.openFileInput(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}

			try {
				ois = new ObjectInputStream(fis);
				obj = (T) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		} finally {
			if(fis != null) {
				try {
					if(ois != null) ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return obj;
	}
	*/

}
