package inujini_.hatate.data.meta;


import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public final class MetaStatistics {


	public static final String TBL_NAME = "Statistics";
	public static final ColumnProperty Count = new ColumnProperty(
			"Count", ISqlite.FIELD_TEXT, false, false, false, false, "", "0");
	public static final ColumnProperty Love = new ColumnProperty(
			"Love", ISqlite.FIELD_TEXT, false, false, false, false, "", "0");
	public static boolean hasPrimaryId() {
		return false;
	}
}
