package com.puppetlabs.puppetdb.javaclient.model;

import static com.puppetlabs.puppetdb.javaclient.query.Query.field;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.query.Field;

/**
 * A POJO that represents a PuppetDB Report
 */
public class Report extends Entity {
	@SuppressWarnings("javadoc")
	public static final Field<Report> END_TIME = field("end-time");

	@SuppressWarnings("javadoc")
	public static final Field<Report> PUPPET_VERSION = field("puppet-version");

	@SuppressWarnings("javadoc")
	public static final Field<Report> RECEIVE_TIME = field("receive-time");

	@SuppressWarnings("javadoc")
	public static final Field<Report> CONFIGURATION_VERSION = field("configuration-version");

	@SuppressWarnings("javadoc")
	public static final Field<Report> START_TIME = field("start-time");

	@SuppressWarnings("javadoc")
	public static final Field<Report> HASH = field("hash");

	@SuppressWarnings("javadoc")
	public static final Field<Report> CERTNAME = field("certname");

	@SuppressWarnings("javadoc")
	public static final Field<Report> REPORT_FORMAT = field("report-format");

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Report} instances
	 */
	public static final Type LIST = new TypeToken<List<Report>>() {}.getType();
	// @fmtOn

	@SerializedName("end-time")
	private Date endTime;

	@SerializedName("puppet-version")
	private String puppetVersion;

	@SerializedName("receive-time")
	private Date receiveTime;

	@SerializedName("configuration-version")
	private String configurationVersion;

	@SerializedName("start-time")
	private Date startTime;

	private String hash;

	private String certname;

	@SerializedName("report-format")
	private int reportFormat;

	@SerializedName("resource-events")
	private List<Event> resourceEvents;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the configurationVersion
	 */
	public String getConfigurationVersion() {
		return configurationVersion;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * The hash is a read-only attribute. It cannot be set when sending a report to the
	 * PuppetDB server.
	 * 
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @return the puppetVersion
	 */
	public String getPuppetVersion() {
		return puppetVersion;
	}

	/**
	 * @return the receiveTime
	 */
	public Date getReceiveTime() {
		return receiveTime;
	}

	/**
	 * @return the reportFormat
	 */
	public int getReportFormat() {
		return reportFormat;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param certname
	 *            the certname to set
	 */
	public void setCertname(String certname) {
		this.certname = certname;
	}

	/**
	 * @param configurationVersion
	 *            the configurationVersion to set
	 */
	public void setConfigurationVersion(String configurationVersion) {
		this.configurationVersion = configurationVersion;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @param puppetVersion
	 *            the puppetVersion to set
	 */
	public void setPuppetVersion(String puppetVersion) {
		this.puppetVersion = puppetVersion;
	}

	/**
	 * @param receiveTime
	 *            the receiveTime to set
	 */
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	/**
	 * @param reportFormat
	 *            the reportFormat to set
	 */
	public void setReportFormat(int reportFormat) {
		this.reportFormat = reportFormat;
	}

	/**
	 * Set the resource events for this report. The resource-events is a write only attribute
	 * and this method should only be used when sending to the PuppetDB server.
	 * 
	 * @param resourceEvents
	 */
	public void setResourceEvents(List<Event> resourceEvents) {
		this.resourceEvents = resourceEvents;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
