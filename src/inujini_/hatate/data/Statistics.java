package inujini_.hatate.data;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;

import java.io.Serializable;

import lombok.Data;

@Data
@SqliteTable("Statistics")
public class Statistics implements Serializable, ISqlite {

	private static final long serialVersionUID = 5624460324478653199L;

	@SqliteField(name="Count", type=FIELD_TEXT, defaultValue="0")
	private int count;

	@SqliteField(name="Love", type=FIELD_TEXT, defaultValue="0")
	private int love;


}
