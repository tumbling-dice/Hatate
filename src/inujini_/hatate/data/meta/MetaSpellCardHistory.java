package inujini_.hatate.data.meta;


import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public final class MetaSpellCardHistory {


	public static final String TBL_NAME = "SpellCardHistory";
	public static final ColumnProperty Id = new ColumnProperty(
			"Id", ISqlite.FIELD_INTEGER, true, false, false, false, "", "");
	public static final ColumnProperty Name = new ColumnProperty(
			"Name", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static final ColumnProperty HistoryOrder = new ColumnProperty(
			"HistoryOrder", ISqlite.FIELD_INTEGER, false, true, true, false, "", "");
	public static final ColumnProperty Timestamp = new ColumnProperty(
			"Timestamp", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static boolean hasPrimaryId() {
		return false;
	}
}
