/**
 * CheckResult.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Maarten Everts, TNO, July 2012,
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, July 2012.
 */

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
