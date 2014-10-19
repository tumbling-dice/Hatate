package inujini_.hatate.data;

import java.io.Serializable;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;
import lombok.Data;

@Data
@SqliteTable("SpellCardHistory")
public class SpellCardHistory implements Serializable, ISqlite {
	private static final long serialVersionUID = 5303526450785933435L;

	@SqliteField(name="Id", type=FIELD_INTEGER, notNull=true)
	private long id;

	@SqliteField(name="Name", type=FIELD_TEXT, notNull=true)
	private String name;

	@SqliteField(name="Timestamp", type=FIELD_TEXT, notNull=true)
	private String timestamp;

	@SqliteField(name="HistoryOrder", type=FIELD_INTEGER, primary=true, autoincrement=true)
	private int order;
}
