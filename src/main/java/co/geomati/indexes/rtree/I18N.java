package co.geomati.indexes.rtree;

import java.text.MessageFormat;

public class I18N {

	public static String get(String msg, Object... arguments) {
		return MessageFormat.format(msg, arguments);
	}

}
