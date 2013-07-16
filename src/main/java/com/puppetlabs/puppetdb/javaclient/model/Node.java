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
import java.util.Date;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.query.Field;

/**
 * A POJO that represents a PuppetDB Node
 */
public class Node extends Entity {
	@SuppressWarnings("javadoc")
	public static final Field<Node> NAME = field("name");

	@SuppressWarnings("javadoc")
	public static final Field<Node> DEACTIVATED = field("deactivated");

	@SuppressWarnings("javadoc")
	public static final Field<Node> CATALOG_TIMESTAMP = field("catalog_timestamp");

	@SuppressWarnings("javadoc")
	public static final Field<Node> FACTS_TIMESTAMP = field("facts_timestamp");

	@SuppressWarnings("javadoc")
	public static final Field<Node> REPORT_TIMESTAMP = field("report_timestamp");

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Resource} instances
	 */
	public static final Type LIST = new TypeToken<List<Node>>() {}.getType();
	// @fmtOn

	private String name;

	private Date deactivated;

	private Date catalog_timestamp;

	private Date facts_timestamp;

	private Date report_timestamp;

	/**
	 * @return the catalog_timestamp
	 */
	public Date getCatalogTimestamp() {
		return catalog_timestamp;
	}

	/**
	 * @return the deactivated
	 */
	public Date getDeactivated() {
		return deactivated;
	}

	/**
	 * @return the facts_timestamp
	 */
	public Date getFactsTimestamp() {
		return facts_timestamp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the report_timestamp
	 */
	public Date getReportTimestamp() {
		return report_timestamp;
	}

	/**
	 * @param catalog_timestamp
	 *            the catalog_timestamp to set
	 */
	public void setCatalogTimestamp(Date catalog_timestamp) {
		this.catalog_timestamp = catalog_timestamp;
	}

	/**
	 * @param deactivated
	 *            the deactivated to set
	 */
	public void setDeactivated(Date deactivated) {
		this.deactivated = deactivated;
	}

	/**
	 * @param facts_timestamp
	 *            the facts_timestamp to set
	 */
	public void setFactsTimestamp(Date facts_timestamp) {
		this.facts_timestamp = facts_timestamp;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param report_timestamp
	 *            the report_timestamp to set
	 */
	public void setReportTimestamp(Date report_timestamp) {
		this.report_timestamp = report_timestamp;
	}
}
