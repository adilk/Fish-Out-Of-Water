package fitnessapps.foow.components;


import android.os.CountDownTimer;
import android.widget.TextView;

public class MyTimer extends CountDownTimer {

	TextView textView;
	boolean boolFinished;
	long secondsRemaining; 
	long secondsStartedWith;
	
	public MyTimer(long millisInFuture, long countDownInterval, TextView txtView) {
		super(millisInFuture, countDownInterval);
		this.textView = txtView;
		boolFinished = false;
		secondsRemaining = -1;
		secondsStartedWith = millisInFuture/1000;
	}
	
	public boolean isFinished() {
		return boolFinished;
	}
	
	public long getSecondsRemaining() {
		return secondsRemaining;
	}
	
	public long getSecondsStartedWith() {
		return secondsStartedWith;
	}
	
	@Override
	public void onFinish() {
		textView.setText("Times Up!");
		boolFinished = true;
	}
	@Override
	public void onTick(long millisUntilFinished) {
		long secUntilFinished = millisUntilFinished/1000;
		textView.setText("Time Left: " + secUntilFinished);
		secondsRemaining = secUntilFinished;
	}
	
}
