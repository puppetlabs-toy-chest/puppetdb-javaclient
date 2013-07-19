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

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.puppetlabs.puppetdb.javaclient.impl.DefaultModule;

/**
 * A factory used for creating fully configured PuppetDBClient instances
 */
public class PuppetDBClientFactory {

	/**
	 * Returns a Module that contains all default bindings
	 * 
	 * @param preferences
	 *            Preferences used when connecting to the PuppetDB instance
	 * @return The default bindings module
	 */
	public static Module getDefaultBindings(APIPreferences preferences) {
		return new DefaultModule(preferences);
	}

	/**
	 * Create a new PuppetDBClient that will connect using the default
	 * bindigns module and the given <code>preferences</code>.
	 * 
	 * @param preferences
	 *            The preferences used for hte connection
	 * @param overrides
	 *            Modules overriding or extending the default bindings
	 * @return The created client instance
	 */
	public static PuppetDBClient newClient(APIPreferences preferences, Module... overrides) {
		Module module = getDefaultBindings(preferences);
		if(overrides.length > 0)
			module = Modules.override(module).with(overrides);
		return newClient(module);
	}

	/**
	 * Create a new PuppetDBClient using the bindings of one or several
	 * Guice modules.
	 * 
	 * @param module
	 *            The Guice module where the bindings have been defined
	 * @return The created client instance
	 */
	public static PuppetDBClient newClient(Module module) {
		return Guice.createInjector(module).getInstance(PuppetDBClient.class);
	}
}
