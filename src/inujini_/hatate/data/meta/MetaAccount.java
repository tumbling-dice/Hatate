package inujini_.hatate.data.meta;


import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public final class MetaAccount {


	public static final String TBL_NAME = "Account";
	public static final ColumnProperty Id = new ColumnProperty(
			"_id", ISqlite.FIELD_INTEGER, true, true, true, true, "", "");
	public static final ColumnProperty AccessSecret = new ColumnProperty(
			"AccessSecret", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static final ColumnProperty AccessToken = new ColumnProperty(
			"AccessToken", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static final ColumnProperty UseFlag = new ColumnProperty(
			"UseFlag", ISqlite.FIELD_INTEGER, false, false, false, false, "", "1");
	public static final ColumnProperty ScreenName = new ColumnProperty(
			"ScreenName", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static final ColumnProperty UserId = new ColumnProperty(
			"UserId", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static boolean hasPrimaryId() {
		return true;
	}
}
