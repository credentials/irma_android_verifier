package org.irmacard.androidverifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.scuba.util.Hex;

public class Verification {
	public final static int RESULT_VALID   = 0;
	public final static int RESULT_INVALID = 1;
	public final static int RESULT_FAILED  = 2;
	
	private int result;
	private Date timestamp;
	private byte[] cardUID;
	private String info;
	private String feedback;
	
	private DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy 'at' HH:mm:ss");;
	
	public Verification(int result, byte[] cardUID, String info, String feedback) {
		this.result = result;
		this.cardUID = cardUID;
		this.info = info;
		this.timestamp = Calendar.getInstance().getTime();
		this.feedback = feedback;
	}
	
	public String getFormattedDate() {
		return dateFormat.format(timestamp);
	}
	
	public String getFormattedValue() {
		switch (result) {
		case RESULT_VALID:
			return "Valid credential";
		case RESULT_INVALID:
			return "No valid credential";
		case RESULT_FAILED:
			return "Verification failed";
		default:
			return "Unknown state";
		}
	}
	
	public int getResult() {
		return result;
	}
	
	public byte[] getCardUID() {
		return cardUID;
	}
	
	public String getCardUIDString() {
		return Hex.bytesToHexString(cardUID);
	}
	
	public String getInfo() {
		return info;
	}
	
	public String getFeedback() {
		return feedback;
	}
		
	@Override
	public String toString() {
		return getFormattedDate() + " (" + getFormattedValue() + ")";
	}
}
