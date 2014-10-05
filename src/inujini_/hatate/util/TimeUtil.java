package inujini_.hatate.util;

import inujini_.hatate.service.Houtyou;

import java.util.Calendar;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

@ExtensionMethod({PrefGetter.class})
public class TimeUtil {

	@SuppressLint("DefaultLocale")
	public static long getAlermTime(int hour, int minute) {
		val calendar = Calendar.getInstance();

		val currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		val currentMinute = calendar.get(Calendar.MINUTE);

		if(Integer.parseInt(String.format("%02d%02d", hour, minute))
				< Integer.parseInt(String.format("%02d%02d", currentHour, currentMinute))) {
			calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
		}

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);

		return calendar.getTimeInMillis();
	}

	public static void setAlerm(Context context) {
		val intent = new Intent(context, Houtyou.class);

		val pendingIntent = PendingIntent.getBroadcast(context, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		val alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP
				, getAlermTime(context.getHour(), context.getMinute())
				, AlarmManager.INTERVAL_DAY, pendingIntent);
	}
}
