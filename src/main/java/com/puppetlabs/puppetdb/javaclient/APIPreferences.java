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
package com.puppetlabs.puppetdb.javaclient;

import java.io.File;

/**
 * Preferences used when connecting to the PuppetDB instance
 */
public interface APIPreferences {
	/**
	 * Connection timeout in milliseconds
	 * 
	 * @return A millisecond timeout
	 */
	int getConnectTimeout();

	/**
	 * Read timeout in milliseconds.
	 * 
	 * @return A millisecond timeout
	 */
	int getReadTimeout();

	/**
	 * Mandatory setting. The SSL port of the PuppetDB host
	 * 
	 * @return A port number
	 */
	int getServiceSSLPort();

	/**
	 * Mandatory setting. The DNS name of the PuppetDB host
	 * 
	 * @return The DNS name of the host
	 */
	String getServiceHostname();

	/**
	 * Returns the path of the PEM file that holds the certificate for the Certification Authority.
	 * 
	 * @return An absolute path
	 */
	File getCaCertPEM();

	/**
	 * Mandatory setting. Returns the path of the PEM file for the Certificate.
	 * 
	 * @return An absolute path
	 */
	File getCertPEM();

	/**
	 * Mandatory setting. Returns the path of the PEM file for the Private Key.
	 * 
	 * @return An absolute path
	 */
	File getPrivateKeyPEM();

	/**
	 * If <code>true</code> then the SSL host name validation will be turned off.
	 * 
	 * @return <code>true</code> to disable SSL host name validation
	 */
	boolean isAllowAllHosts();
}
