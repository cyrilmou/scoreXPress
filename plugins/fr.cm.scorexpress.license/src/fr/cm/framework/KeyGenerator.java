package fr.cm.framework;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KeyGenerator {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyMMddHHmmss");

	public static String getKey() {
		Date d = new Date();
		return sdf.format(d);
	}
}
