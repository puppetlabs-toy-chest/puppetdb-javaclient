package com.puppetlabs.puppetdb.javaclient.model;

import static com.puppetlabs.puppetdb.javaclient.query.Query.field;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.model.Catalog.ResourceSpec;
import com.puppetlabs.puppetdb.javaclient.query.Field;

public class EventCount extends Entity {

	public enum CountBy {
		resource, certname
	}

	public enum SummarizeBy {
		resource("resource"), containing_class("containing-class"), certname("certname");

		private final String label;

		private SummarizeBy(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link EventCount} instances
	 */
	public static final Type LIST = new TypeToken<List<EventCount>>() {}.getType();
	// @fmtOn

	@SuppressWarnings("javadoc")
	public static final Field<EventCount> FAILURES = field("failures");

	@SuppressWarnings("javadoc")
	public static final Field<EventCount> SUCCESSES = field("successes");

	@SuppressWarnings("javadoc")
	public static final Field<EventCount> NOOPS = field("noops");

	@SuppressWarnings("javadoc")
	public static final Field<EventCount> SKIPS = field("skips");

	@SerializedName("subject-type")
	private String subjectType;

	private ResourceSpec subject;

	private int failures;

	private int successes;

	private int noops;

	private int skips;

	/**
	 * @return the failures
	 */
	public int getFailures() {
		return failures;
	}

	/**
	 * @return the noops
	 */
	public int getNoops() {
		return noops;
	}

	/**
	 * @return the skips
	 */
	public int getSkips() {
		return skips;
	}

	/**
	 * @return the subject
	 */
	public ResourceSpec getSubject() {
		return subject;
	}

	/**
	 * @return the subjectType
	 */
	public String getSubjectType() {
		return subjectType;
	}

	/**
	 * @return the successes
	 */
	public int getSuccesses() {
		return successes;
	}

	/**
	 * @param failures
	 *            the failures to set
	 */
	public void setFailures(int failures) {
		this.failures = failures;
	}

	/**
	 * @param noops
	 *            the noops to set
	 */
	public void setNoops(int noops) {
		this.noops = noops;
	}

	/**
	 * @param skips
	 *            the skips to set
	 */
	public void setSkips(int skips) {
		this.skips = skips;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(ResourceSpec subject) {
		this.subject = subject;
	}

	/**
	 * @param subjectType
	 *            the subjectType to set
	 */
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	/**
	 * @param successes
	 *            the successes to set
	 */
	public void setSuccesses(int successes) {
		this.successes = successes;
	}
}
