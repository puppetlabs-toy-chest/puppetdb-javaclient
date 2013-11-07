package com.puppetlabs.puppetdb.javaclient.query;

import java.util.Map;

/**
 * Interface that is the base for order-by, paging, and query expressions
 * 
 * @param <T>
 *            The type to be queried
 */
public interface Parameters<T> {
	/**
	 * Appends the parameters represented by this instance to the given map
	 * 
	 * @param queryParams
	 *            The map that receives the parameters
	 */
	void appendTo(Map<String, String> queryParams);
}
