package inujini_.hatate.util;

import inujini_.hatate.service.Houtyou;
import inujini_.hatate.service.OneMoreLovely;

import java.lang.ref.SoftReference;
import java.util.Calendar;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

@ExtensionMethod({PrefGetter.class})
public class Util {

	private static SoftReference<AlarmManager> _alarmManager;

	@SuppressLint("DefaultLocale")
	public static long getAlermTime(int hour, int minute) {
		val calendar = Calendar.getInstance();

		val currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		val currentMinute = calendar.get(Calendar.MINUTE);

		if(Integer.parseInt(String.format("%02d%02d", hour, minute))
				<= Integer.parseInt(String.format("%02d%02d", currentHour, currentMinute))) {
			calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
		}

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTimeInMillis();
	}

	public static void setAlarm(Context context) {
		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP
				, getAlermTime(context.getHour(), context.getMinute())
				, AlarmManager.INTERVAL_DAY, getAlarmIntent(context));
	}

	public static void removeAlarm(Context context) {
		getAlarmManager(context).cancel(getAlarmIntent(context));
	}

	public static PendingIntent getAlarmIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, Houtyou.class)
			, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static void setSnooze(Context context) {
		getAlarmManager(context).set(AlarmManager.RTC_WAKEUP
				, (System.currentTimeMillis() + context.getSnoozeTimeMill())
				, getSnoozeIntent(context));
	}

	public static void removeSnooze(Context context) {
		getAlarmManager(context).cancel(getSnoozeIntent(context));
	}

	public static PendingIntent getSnoozeIntent(Context context) {
		return PendingIntent.getBroadcast(context, -1, new Intent(context, OneMoreLovely.class)
				, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static AlarmManager getAlarmManager(Context context) {
		AlarmManager am = null;
		if(_alarmManager != null) {
			am = _alarmManager.get();
		}

		if(am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			_alarmManager = new SoftReference<AlarmManager>(am);
		}

		return am;
	}


}
