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

import static com.puppetlabs.puppetdb.javaclient.query.Query.field;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.query.Field;

/**
 * A POJO that represents a PuppetDB Fact
 */
public class Fact extends Entity {
	@SuppressWarnings("javadoc")
	public static final Field<Fact> CERTNAME = field("certname");

	@SuppressWarnings("javadoc")
	public static final Field<Fact> NAME = field("name");

	@SuppressWarnings("javadoc")
	public static final Field<Fact> VALUE = field("value");

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Resource} instances
	 */
	public static final Type LIST = new TypeToken<List<Fact>>() {}.getType();
	// @fmtOn

	private String certname;

	private String name;

	private String value;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param certname
	 *            the certname to set
	 */
	public void setCertname(String certname) {
		this.certname = certname;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
