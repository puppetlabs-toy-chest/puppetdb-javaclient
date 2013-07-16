package com.puppetlabs.puppetdb.javaclient.impl;

import com.puppetlabs.puppetdb.javaclient.model.Entity;

/**
 * Commands are used to change PuppetDB’s model of a population. Commands are represented by command objects.
 */
public class CommandObject extends Entity {
	private String command;

	private Integer version;

	private Object payload;

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the payload
	 */
	public Object getPayload() {
		return payload;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param command
	 *            the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public void setPayload(Object payload) {
		this.payload = payload;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
}
