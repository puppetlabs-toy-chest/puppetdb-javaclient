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
package com.puppetlabs.puppetdb.javaclient.impl;

import org.apache.http.conn.ssl.SSLSocketFactory;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.puppetlabs.puppetdb.javaclient.APIPreferences;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;

/**
 * Default Guice Injection module
 */
public class DefaultModule extends AbstractModule {
	private final APIPreferences preferences;

	/**
	 * Create a module with preferences.
	 * 
	 * @param preferences
	 *            The preferences
	 */
	public DefaultModule(APIPreferences preferences) {
		this.preferences = preferences;
	}

	@Override
	protected void configure() {
		bind(APIPreferences.class).toInstance(preferences);
		bind(Gson.class).toProvider(GsonProvider.class);
		bind(SSLSocketFactory.class).toProvider(PEM_SSLSocketFactoryProvider.class).in(Singleton.class);
		bind(HttpConnector.class).to(HttpComponentsConnector.class);
		bind(PuppetDBClient.class).to(PuppetDBClientImpl.class);
	}
}
