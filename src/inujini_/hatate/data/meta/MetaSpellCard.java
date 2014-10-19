package inujini_.hatate.data.meta;


import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public final class MetaSpellCard {


	public static final String TBL_NAME = "SpellCard";
	public static final ColumnProperty CharacterId = new ColumnProperty(
			"CharacterId", ISqlite.FIELD_INTEGER, true, false, false, false, "", "");
	public static final ColumnProperty Count = new ColumnProperty(
			"Count", ISqlite.FIELD_INTEGER, true, false, false, false, "", "0");
	public static final ColumnProperty Id = new ColumnProperty(
			"Id", ISqlite.FIELD_INTEGER, false, true, false, false, "", "");
	public static final ColumnProperty EquipmentFlag = new ColumnProperty(
			"EquipmentFlag", ISqlite.FIELD_INTEGER, true, false, false, false, "", "0");
	public static final ColumnProperty GetFlag = new ColumnProperty(
			"GetFlag", ISqlite.FIELD_INTEGER, true, false, false, false, "", "0");
	public static final ColumnProperty Name = new ColumnProperty(
			"Name", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static final ColumnProperty Power = new ColumnProperty(
			"Power", ISqlite.FIELD_INTEGER, true, false, false, false, "", "");
	public static final ColumnProperty SeriesId = new ColumnProperty(
			"SeriesId", ISqlite.FIELD_TEXT, true, false, false, false, "", "");
	public static boolean hasPrimaryId() {
		return false;
	}
}
