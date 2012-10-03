package org.irmacard.androidverifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckResult {
	public final static int STATE_VALID   = 0;
	public final static int STATE_INVALID = 1;
	public final static int STATE_FAILED  = 2;
	
	private int state;
	private Date timestamp;
	private DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy 'at' HH:mm:ss");;
	
	public CheckResult(int state) {
		this.state = state;
		this.timestamp = Calendar.getInstance().getTime();
	}
	
	public String getFormattedDate() {
		return dateFormat.format(timestamp);
	}
	
	public String getFormattedValue() {
		switch (state) {
		case STATE_VALID:
			return "Valid credential";
		case STATE_INVALID:
			return "No valid credential";
		case STATE_FAILED:
			return "Verification failed";
		default:
			return "Unknown state";
		}
	}
	
	public int getState() {
		return state;
	}
		
	@Override
	public String toString() {
		return getFormattedDate() + " (" + getFormattedValue() + ")";
	}
}
