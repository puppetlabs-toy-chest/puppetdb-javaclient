package com.puppetlabs.puppetdb.javaclient.impl;

import com.puppetlabs.puppetdb.javaclient.model.Entity;

/**
 * A command response, containing a single key ‘uuid’, whose value is a UUID corresponding to the
 * submitted command. This can be used, for example, by clients to correlate submitted commands with server-side logs.
 */
public class CommandResponse extends Entity {
	private String uuid;

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
