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
 * An identifier of a field, a named fact, or a named resource parameter. In essence, and instance of
 * this class is always the left hand side of all comparisons.
 * 
 * @param <T>
 *            The type that the identifier is applicable to
 */
public interface Identifier<T> extends Expression<T> {
}
