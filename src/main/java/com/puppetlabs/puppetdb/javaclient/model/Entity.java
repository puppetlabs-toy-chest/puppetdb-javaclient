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
package com.puppetlabs.puppetdb.javaclient.model;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.impl.GsonProvider;

/**
 * Super class of all model entitites. Provides basic JSON capability.
 */
public class Entity {
	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Resource} instances
	 */
	public static final Type LIST_STRING = new TypeToken<List<String>>() {}.getType();

	/**
	 * A type representing a {@link Map} with a string key and a string value
	 */
	public static final Type MAP_STRING_OBJECT = new TypeToken<Map<String,Object>>() {}.getType();

	/**
	 * A type representing a {@link Map} with a string key and a string value
	 */
	public static final Type MAP_STRING_STRING = new TypeToken<Map<String,String>>() {}.getType();
	// @fmtOn

	protected static void quoteSym(Enum<?> sym, StringBuilder result) {
		result.append('"');
		result.append(sym.name());
		result.append('"');
	}

	protected static boolean safeEquals(Object a, Object b) {
		return a == b || a != null && b != null && a.equals(b);
	}

	protected static int safeHash(Object a) {
		return a == null
				? 773
				: a.hashCode();
	}

	/**
	 * Produces a JSON representation of the Entity
	 * 
	 * @return The JSON string
	 */
	@Override
	public final String toString() {
		return GsonProvider.toJSON(this);
	}
}
