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

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.query.Field;

/**
 * A POJO that represents a PuppetDB Event
 */
public class Event extends Entity {
	/**
	 * An enum representing the outcome of an event
	 */
	@SuppressWarnings("javadoc")
	public enum Status {
		success, failure, noop, skipped
	}

	@SuppressWarnings("javadoc")
	public static final Field<Event> CERTNAME = field("certname");

	@SuppressWarnings("javadoc")
	public static final Field<Event> REPORT = field("report");

	@SuppressWarnings("javadoc")
	public static final Field<Event> STATUS = field("status");

	@SuppressWarnings("javadoc")
	public static final Field<Event> TIMESTAMP = field("timestamp");

	@SuppressWarnings("javadoc")
	public static final Field<Event> RESOURCE_TYPE = field("resource-type");

	@SuppressWarnings("javadoc")
	public static final Field<Event> RESOURCE_TITLE = field("resource-title");

	@SuppressWarnings("javadoc")
	public static final Field<Event> PROPERTY = field("property");

	@SuppressWarnings("javadoc")
	public static final Field<Event> NEW_VALUE = field("new-value");

	@SuppressWarnings("javadoc")
	public static final Field<Event> OLD_VALUE = field("old-value");

	@SuppressWarnings("javadoc")
	public static final Field<Event> MESSAGE = field("message");

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Event} instances
	 */
	public static final Type LIST = new TypeToken<List<Event>>() {}.getType();
	// @fmtOn

	private String certname;

	private String report;

	private Status status;

	private Date timestamp;

	@SerializedName("resource-type")
	private String resourceType;

	@SerializedName("resource-title")
	private String resourceTitle;

	private String property;

	@SerializedName("new-value")
	private String newValue;

	@SerializedName("old-value")
	private String oldValue;

	private String message;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @return the oldValue
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @return the report
	 */
	public String getReport() {
		return report;
	}

	/**
	 * @return the resourceTitle
	 */
	public String getResourceTitle() {
		return resourceTitle;
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @param resourceTitle
	 *            the resourceTitle to set
	 */
	public void setResourceTitle(String resourceTitle) {
		this.resourceTitle = resourceTitle;
	}

	/**
	 * @param resourceType
	 *            the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
