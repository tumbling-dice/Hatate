package inujini_.hatate.data.meta;


import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public final class MetaSeries {


	public static final String TBL_NAME = "Series";
	public static final ColumnProperty Id = new ColumnProperty(
			"Id", ISqlite.FIELD_INTEGER, false, true, false, false, "", "");
	public static final ColumnProperty Name = new ColumnProperty(
			"Name", ISqlite.FIELD_TEXT, true, false, false, true, "", "");
	public static boolean hasPrimaryId() {
		return false;
	}
}
