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

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO that represents multiple PuppetDB Facts that share the same certname.
 */
public class Facts extends Entity {

	// wire-format uses name/certname inconsistently Here it is supposed to be 'name'
	@SerializedName("name")
	private String certname;

	private Map<String, String> values;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the value map
	 */
	public Map<String, String> getValues() {
		return values;
	}

	/**
	 * @param certname
	 *            the certname to set
	 */
	public void setCertname(String certname) {
		this.certname = certname;
	}

	/**
	 * @param values
	 *            the value map to set
	 */
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
}
