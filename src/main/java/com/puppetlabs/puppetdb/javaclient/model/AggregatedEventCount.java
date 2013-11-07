package com.puppetlabs.puppetdb.javaclient.model;


public class AggregatedEventCount extends Entity {
	private int failures;

	private int successes;

	private int noops;

	private int skips;

	private int total;

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
	 * @return the successes
	 */
	public int getSuccesses() {
		return successes;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
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
	 * @param successes
	 *            the successes to set
	 */
	public void setSuccesses(int successes) {
		this.successes = successes;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
}
