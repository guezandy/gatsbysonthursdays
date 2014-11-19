package com.appdupe.uberforxserviceseeker.helper;

import java.text.DecimalFormat;

import android.os.CountDownTimer;

public class TimerHelper extends CountDownTimer {

	public interface OnTimeTick {
		public void onTick(long millisUntilFinished, String formatedTime);

		public void onFinish();
	}

	private static final long MILLIS_HOUR = 3600 * 1000;
	private static final long MILLIS_MINUTE = 60 * 1000;
	private static final long MILLIS_SECOND = 1000;

	private OnTimeTick onTimeTick;

	public TimerHelper(long millisInFuture, long countDownInterval,
			OnTimeTick onTimeTick) {
		super(millisInFuture, countDownInterval);

		this.onTimeTick = onTimeTick;
	}

	@Override
	public void onFinish() {

		if (onTimeTick != null) {
			onTimeTick.onFinish();
		}
	}

	@Override
	public void onTick(long millisUntilFinished) {

		if (onTimeTick != null) {
			onTimeTick.onTick(millisUntilFinished,
					getFormatedTime(millisUntilFinished));
		}
	}

	private String getFormatedTime(long millisUntilFinished) {
		long hour = millisUntilFinished / MILLIS_HOUR;
		long minute = millisUntilFinished - (hour * MILLIS_HOUR);
		minute = minute / MILLIS_MINUTE;
		long second = millisUntilFinished - (hour * MILLIS_HOUR)
				- (minute * MILLIS_MINUTE);
		second = second / MILLIS_SECOND;
		DecimalFormat money = new DecimalFormat("00");

		return money.format(hour) + money.format(minute) + money.format(second);
	}
	//
	// private String getFormatedTime(long millisUntilFinished) {
	// long hour = millisUntilFinished / MILLIS_HOUR;
	//
	// long minute = millisUntilFinished - (hour * MILLIS_HOUR);
	// minute = minute / MILLIS_MINUTE;
	//
	// long second = millisUntilFinished - (hour * MILLIS_HOUR)
	// - (minute * MILLIS_MINUTE);
	// second = second / MILLIS_SECOND;
	//
	// return hour + " Hr " + minute + " min " + second + " sec";
	// }

}
