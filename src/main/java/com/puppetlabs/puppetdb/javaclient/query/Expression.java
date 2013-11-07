/**
 * Copyright (c) 2013 Puppet Labs, Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *   Puppet Labs
 */
package com.puppetlabs.puppetdb.javaclient.query;

/**
 * An expression is used to add a query a PuppetDB request.
 * 
 * @param <T>
 *            The type that this expression should be applied to
 * @see Query
 */
public interface Expression<T> extends Parameters<T> {
	/**
	 * Produce a JSON representation of this expression onto
	 * the given <code>result</code> builder
	 * 
	 * @param result
	 *            The builder that will receive the JSON representation
	 */
	void toJSON(StringBuilder result);
}
