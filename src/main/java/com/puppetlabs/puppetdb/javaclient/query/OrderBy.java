package com.puppetlabs.puppetdb.javaclient.query;

import java.util.List;
import java.util.Map;

/**
 * Amends a query expression with instructions to order the result in accorance with a list
 * of {@link OrderByField} instances.
 * 
 * @param <T>
 */
public class OrderBy<T> implements Parameters<T> {

	/**
	 * Describes a field that can be used for ordering in either ascending
	 * or descending mode.
	 * 
	 * @param <T>
	 */
	public static class OrderByField<T> {
		private final Field<T> field;

		private final boolean descending;

		OrderByField(Field<T> field, boolean descending) {
			this.field = field;
			this.descending = descending;
		}

		void toJSON(StringBuilder result) {
			result.append("{\"field\":");
			field.toJSON(result);
			if(descending)
				result.append(",\"order\": \"desc\"");
			result.append('}');
		}
	}

	private final Expression<T> query;

	private final List<OrderByField<T>> fields;

	OrderBy(Expression<T> query, List<OrderByField<T>> fields) {
		this.query = query;
		this.fields = fields;
	}

	@Override
	public void appendTo(Map<String, String> queryParams) {
		if(query != null)
			query.appendTo(queryParams);
		if(fields != null && fields.size() > 0)
			queryParams.put("order-by", toString());
	}

	public void toJSON(StringBuilder result) {
		result.append('[');
		int top = fields.size();
		for(int idx = 0; idx < top; ++idx) {
			if(idx > 0)
				result.append(',');
			fields.get(idx).toJSON(result);
		}
		result.append(']');
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		toJSON(bld);
		return bld.toString();
	}
}
