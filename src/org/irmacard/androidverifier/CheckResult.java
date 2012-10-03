package org.irmacard.androidverifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckResult {
	private boolean value;
	private Date timestamp;
	private DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy 'at' HH:mm:ss");;
	
	public CheckResult(boolean value) {
		this.value = value;
		this.timestamp = Calendar.getInstance().getTime();
	}
	
	public String getFormattedDate() {
		return dateFormat.format(timestamp);
	}
	
	public String getFormattedValue() {
		return value ? "Valid credential": "No valid credential";
	}
	
	public boolean getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return getFormattedDate() + " (" + getFormattedValue() + ")";
	}
}
