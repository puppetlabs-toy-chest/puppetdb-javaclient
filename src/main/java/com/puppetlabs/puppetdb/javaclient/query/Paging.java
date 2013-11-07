package com.puppetlabs.puppetdb.javaclient.query;

import java.util.Map;

/**
 * This class serves two purposes. It can hold paging information that should be sent
 * as part of the request and, in case the <code>includeTotal</code> parameter is true,
 * it will also store the returned total count.
 * 
 * @param <T>
 */
public class Paging<T> implements Parameters<T> {
	private final Parameters<T> parameters;

	private int limit;

	private int offset;

	private boolean includeTotal;

	private int totalCount = -1;

	/**
	 * Constructor to use when Paging without OrderBy is desired
	 * 
	 * @param parameters
	 *            The query predicates. Can be <code>null</code>.
	 * @param offset
	 *            Requested offset of the first entry in the result
	 * @param limit
	 *            Requested maximum number of entries in the result
	 * @param includeTotal
	 *            Include the total number of entries that the query matched (obtained after successful execution with
	 *            {@link #getTotalCount()}.
	 */
	public Paging(Expression<T> parameters, int offset, int limit, boolean includeTotal) {
		this.parameters = parameters;
		this.offset = offset;
		this.limit = limit;
		this.includeTotal = includeTotal;
	}

	/**
	 * Constructor to use when Paging and OrderBy is desired
	 * 
	 * @param orderBy
	 *            The order by and query predicates.
	 * @param offset
	 *            Requested offset of the first entry in the result
	 * @param limit
	 *            Requested maximum number of entries in the result
	 * @param includeTotal
	 *            Include the total number of entries that the query matched (obtained after successful execution with
	 *            {@link #getTotalCount()}.
	 */
	public Paging(OrderBy<T> orderBy, int offset, int limit, boolean includeTotal) {
		this.parameters = orderBy;
		this.offset = offset;
		this.limit = limit;
		this.includeTotal = includeTotal;
	}

	@Override
	public void appendTo(Map<String, String> queryParams) {
		if(parameters != null)
			parameters.appendTo(queryParams);
		if(limit > 0 && limit < Integer.MAX_VALUE)
			queryParams.put("limit", Integer.toString(limit));
		if(offset > 0 && offset <= Integer.MAX_VALUE)
			queryParams.put("map", Integer.toString(offset));
		if(includeTotal)
			queryParams.put("include-total", "true");
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the parameters
	 */
	public Parameters<T> getParameters() {
		return parameters;
	}

	/**
	 * After a successful call, and if the <code>includeTotal</code> is <code>true</code>, then the
	 * total count can be retrieved using this method.
	 * 
	 * @return the totalCount or -1 if not applicable
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @return the includeTotal
	 */
	public boolean isIncludeTotal() {
		return includeTotal;
	}

	/**
	 * @param includeTotal
	 *            the includeTotal to set
	 */
	public void setIncludeTotal(boolean includeTotal) {
		this.includeTotal = includeTotal;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @param totalCount
	 *            the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
