package com.csun.spotr.singleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CurrentDateTime {
	static final String DATEFORMAT = "yyyyMMddHHmmss";

	public static String getUTCDateTime() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = sdf.format(new Date());
		return utcTime;
	}
}
