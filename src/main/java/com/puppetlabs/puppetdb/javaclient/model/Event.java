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
import java.util.Collections;
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
	public static final Field<Event> RUN_START_TIME = field("run-start-time");

	@SuppressWarnings("javadoc")
	public static final Field<Event> RUN_END_TIME = field("run-end-time");

	@SuppressWarnings("javadoc")
	public static final Field<Event> REPORT_RECEIVE_TIME = field("report-receive-time");

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

	@SuppressWarnings("javadoc")
	public static final Field<Event> FILE = field("file");

	@SuppressWarnings("javadoc")
	public static final Field<Event> LINE = field("line");

	@SuppressWarnings("javadoc")
	public static final Field<Event> CONTAINING_CLASS = field("containing-class");

	@SuppressWarnings("javadoc")
	public static final Field<Event> LATEST_REPORT = field("latest-report");

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

	@SerializedName("run-start-time")
	private Date runStartTime;

	@SerializedName("run-end-time")
	private Date runEndTime;

	@SerializedName("report-receive-time")
	private Date reportReceiveTime;

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

	private String file;

	private int line;

	@SerializedName("containment-path")
	private List<String> containmentPath;

	@SerializedName("containing-class")
	private String containingClass;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the containingClass
	 */
	public String getContainingClass() {
		return containingClass;
	}

	/**
	 * @return the containmentPath
	 */
	public List<String> getContainmentPath() {
		return containmentPath == null
				? Collections.<String> emptyList()
				: containmentPath;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
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
	 * @return the reportReceiveTime
	 */
	public Date getReportReceiveTime() {
		return reportReceiveTime;
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
	 * @return the runEndTime
	 */
	public Date getRunEndTime() {
		return runEndTime;
	}

	/**
	 * @return the runStartTime
	 */
	public Date getRunStartTime() {
		return runStartTime;
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
	 * @param certname
	 *            the certname to set
	 */
	public void setCertname(String certname) {
		this.certname = certname;
	}

	/**
	 * @param containingClass
	 *            the containingClass to set
	 */
	public void setContainingClass(String containingClass) {
		this.containingClass = containingClass;
	}

	/**
	 * @param containmentPath
	 *            the containmentPath to set
	 */
	public void setContainmentPath(List<String> containmentPath) {
		this.containmentPath = containmentPath;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @param line
	 *            the line to set
	 */
	public void setLine(int line) {
		this.line = line;
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
	 * @param report
	 *            the report to set
	 */
	public void setReport(String report) {
		this.report = report;
	}

	/**
	 * @param reportReceiveTime
	 *            the reportReceiveTime to set
	 */
	public void setReportReceiveTime(Date reportReceiveTime) {
		this.reportReceiveTime = reportReceiveTime;
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
	 * @param runEndTime
	 *            the runEndTime to set
	 */
	public void setRunEndTime(Date runEndTime) {
		this.runEndTime = runEndTime;
	}

	/**
	 * @param runStartTime
	 *            the runStartTime to set
	 */
	public void setRunStartTime(Date runStartTime) {
		this.runStartTime = runStartTime;
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
