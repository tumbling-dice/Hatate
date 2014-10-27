/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.sqlite.helper;

import inujini_.sqlite.meta.ColumnProperty;
import inujini_.sqlite.meta.ISqlite;


public class QueryBuilder {

	private StringBuilder _query;

	public QueryBuilder() {
		_query = new StringBuilder();
	}

	public QueryBuilder select(ColumnProperty... column) {
		_query.append("SELECT");

		for (int i = 0, count = column.length; i < count; i++) {
			if (i == 0) {
				_query.append(" ").append(column[i].getColumnName());
			} else {
				_query.append(" ,").append(column[i].getColumnName());
			}
		}

		return this;
	}

	public QueryBuilder selectAll() {
		_query.append("SELECT *");
		return this;
	}

	public UpdateQuery update(String table) {
		_query.append("UPDATE ").append(table).append(" SET");
		return new UpdateQuery(this);
	}

	public WhereQuery delete(String table) {
		_query.append("DELETE FROM ").append(table);
		return new WhereQuery(this);
	}

	public QueryBuilder insert(String table, ColumnValuePair... values) {
		_query.append("INSERT INTO ").append(table).append("(");
		StringBuilder valuesBuilder = new StringBuilder("(");

		for (ColumnValuePair columnValuePair : values) {
			ColumnProperty c = columnValuePair.getColumn();

			_query.append(c.getColumnName()).append(",");

			String value = columnValuePair.getValue();

			valuesBuilder.append(c.getType() == ISqlite.FIELD_TEXT
					? "'" + value + "'"
					: value)
					.append(",");
		}

		_query.deleteCharAt(_query.length() - 1);
		valuesBuilder.deleteCharAt(valuesBuilder.length() - 1);

		valuesBuilder.append(")");
		_query.append(") VALUES").append(valuesBuilder);

		return this;
	}

	public QueryBuilder from(String table) {
		_query.append(" FROM ").append(table);
		return this;
	}

	public QueryBuilder limit(int count) {
		_query.append(" LIMIT ").append(count);
		return this;
	}

	public QueryBuilder offset(int count) {
		_query.append(" OFFSET ").append(count);
		return this;
	}

	public QueryBuilder orderByAsc(ColumnProperty... columns) {
		_query.append(" ORDER BY ");
		for (ColumnProperty column : columns) {
			_query.append(column.getColumnName()).append(",");
		}

		_query.deleteCharAt(_query.length() - 1);

		return this;
	}

	public QueryBuilder orderByDesc(ColumnProperty... columns) {
		orderByAsc(columns);
		_query.append(" DESC");

		return this;
	}

	public WhereQuery where() {
		return new WhereQuery(this);
	}

	QueryBuilder append(ColumnProperty c) {
		_query.append(c.getColumnName());
		return this;
	}

	QueryBuilder append(String s) {
		_query.append(s);
		return this;
	}

	QueryBuilder append(int i) {
		_query.append(i);
		return this;
	}

	QueryBuilder appendFormat(String format, Object... params) {
		_query.append(String.format(format, params));
		return this;

	}

	@Override
	public String toString() {
		return _query.append(";").toString();
	}

	public static class UpdateQuery {
		private QueryBuilder _qb;
		private boolean isSet = false;

		public UpdateQuery(QueryBuilder queryBuilder) {
			_qb = queryBuilder;
		}

		public UpdateQuery set(ColumnProperty column, String value) {
			_qb.append(" ");
			if(isSet) _qb.append(",");

			_qb.append(column.getColumnName()).append(" = ")
				.appendFormat(column.getType() == ISqlite.FIELD_TEXT
						? "'%s'": "%s", value);

			isSet = true;
			return this;
		}

		public UpdateQuery set(ColumnProperty column, int value) {
			return set(column, String.valueOf(value));
		}

		public UpdateQuery set(ColumnProperty column, long value) {
			return set(column, String.valueOf(value));
		}

		public UpdateQuery set(ColumnProperty column, boolean value) {
			return set(column, value ? "1" : "0");
		}

		public UpdateQuery set(ColumnValuePair... columns) {
			for (ColumnValuePair column : columns) {
				set(column.getColumn(), column.getValue());
			}
			return this;
		}

		public WhereQuery where() {
			return new WhereQuery(_qb);
		}

		@Override
		public String toString() {
			return _qb.toString();
		}
	}

	public static class WhereQuery {

		private QueryBuilder _qb;

		public WhereQuery(QueryBuilder queryBuilder) {
			_qb = queryBuilder.append(" WHERE");
		}

		public WhereQuery equal(ColumnProperty column, String value) {
			_qb.append(" ")
					.append(column)
					.append(" = ")
					.appendFormat(column.getType() == ISqlite.FIELD_TEXT
							? "'%s'" : "%s", value);
			return this;
		}

		public WhereQuery equal(ColumnProperty column, int value) {
			return equal(column, String.valueOf(value));
		}

		public WhereQuery equal(ColumnProperty column, long value) {
			return equal(column, String.valueOf(value));
		}

		public WhereQuery equal(ColumnProperty column, boolean value) {
			return equal(column, value ? "1" : "0");
		}

		public WhereQuery in(ColumnProperty column, String... values) {
			_qb.append(" ").append(column).append(" IN (");

			for (int i = 0, count = values.length; i < count; i++) {
				if (i == 0) {
					_qb.appendFormat(column.getType() == ISqlite.FIELD_TEXT
							? "'%s'" : "%s", values[i]);
				} else {
					_qb.append(",")
						.appendFormat(column.getType() == ISqlite.FIELD_TEXT
							? "'%s'" : "%s", values[i]);
				}
			}

			_qb.append(")");
			return this;
		}

		public WhereQuery between(ColumnProperty column, String begin, String end) {

			_qb.append(" ").append(column).append(" between ");

			if(column.getType() == ISqlite.FIELD_TEXT) {
				_qb.appendFormat("'%s'", begin).append(" and ").appendFormat("'%s'", end);
			} else {
				_qb.append(begin).append(" and ").append(end);
			}

			return this;
		}

		public WhereQuery and() {
			_qb.append(" AND");
			return this;
		}

		public WhereQuery or() {
			_qb.append(" OR");
			return this;
		}

		public WhereQuery bigger(ColumnProperty column, int i) {
			_qb.append(" ").append(column).append(" > ").append(i);
			return this;
		}

		public WhereQuery smaller(ColumnProperty column, int i) {
			_qb.append(" ").append(column).append(" < ").append(i);
			return this;
		}

		public QueryBuilder toQuery() {
			_qb.append(" ");
			return _qb;
		}

		@Override
		public String toString() {
			return _qb.toString();
		}



	}


}
