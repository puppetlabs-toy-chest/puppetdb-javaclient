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
 * Default Guice Injection module
 */
public class BasicAPIPreferences implements APIPreferences {
	/**
	 * Default connection timeout when establishing a new connection to the PuppetDB service
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

	/**
	 * Default response timeout for requests sent to the PuppetDB service
	 */
	public static final int DEFAULT_READ_TIMEOUT = 5000;

	private int connectTimeout = DEFAULT_CONNECTION_TIMEOUT;

	private int readTimeout = DEFAULT_READ_TIMEOUT;

	private boolean allowAllHosts = false;

	private int serviceSSLPort;

	private String serviceHostname;

	private File caCertPEM;

	private File certPEM;

	private File privateKeyPEM;

	/**
	 * @return the connectTimeout
	 */
	@Override
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the readTimeout
	 */
	@Override
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * @param readTimeout
	 *            the readTimeout to set
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * @return the caCertPEM
	 */
	@Override
	public File getCaCertPEM() {
		return caCertPEM;
	}

	/**
	 * @param caCertPEM
	 *            the caCertPEM to set
	 */
	public void setCaCertPEM(File caCertPEM) {
		this.caCertPEM = caCertPEM;
	}

	/**
	 * @return the certPEM
	 */
	@Override
	public File getCertPEM() {
		return certPEM;
	}

	/**
	 * @param certPEM
	 *            the certPEM to set
	 */
	public void setCertPEM(File certPEM) {
		this.certPEM = certPEM;
	}

	/**
	 * @return the privateKeyPEM
	 */
	@Override
	public File getPrivateKeyPEM() {
		return privateKeyPEM;
	}

	/**
	 * @param privateKeyPEM
	 *            the privateKeyPEM to set
	 */
	public void setPrivateKeyPEM(File privateKeyPEM) {
		this.privateKeyPEM = privateKeyPEM;
	}

	@Override
	public boolean isAllowAllHosts() {
		return allowAllHosts;
	}

	/**
	 * @param allowAllHosts
	 *            set to <code>true</code> to disable host name checking
	 */
	public void setAllowAllHosts(boolean allowAllHosts) {
		this.allowAllHosts = allowAllHosts;
	}

	@Override
	public int getServiceSSLPort() {
		return serviceSSLPort;
	}

	/**
	 * @param serviceSSLPort
	 *            the serviceSSLPort to set
	 */
	public void setServiceSSLPort(int serviceSSLPort) {
		this.serviceSSLPort = serviceSSLPort;
	}

	@Override
	public String getServiceHostname() {
		return serviceHostname;
	}

	/**
	 * @param serviceHostname
	 *            the serviceHostname to set
	 */
	public void setServiceHostname(String serviceHostname) {
		this.serviceHostname = serviceHostname;
	}
}
