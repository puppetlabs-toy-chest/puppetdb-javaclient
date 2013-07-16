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
 * Marker interface for Fields valid to use in a query. The actual set of fields
 * are implemented as enums in the respective model class.
 * 
 * @param <T>
 *            The model class that declares the field
 * 
 * @see Expression
 */
public interface Field<T> extends Identifier<T> {
}
