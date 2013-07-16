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

import static com.puppetlabs.puppetdb.javaclient.query.Query.field;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.puppetlabs.puppetdb.javaclient.query.Field;

/**
 * A POJO that represents a PuppetDB Resource
 */
public class Resource extends Entity {
	@SuppressWarnings("javadoc")
	public static final Field<Resource> TAG = field("tag");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> CERTNAME = field("certname");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> TYPE = field("type");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> TITLE = field("title");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> EXPORTED = field("exported");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> SOURCEFILE = field("sourcefile");

	@SuppressWarnings("javadoc")
	public static final Field<Resource> SOURCELINE = field("sourceline");

	// @fmtOff
	/**
	 * A type representing a {@link List} of {@link Resource} instances
	 */
	public static final Type LIST = new TypeToken<List<Resource>>() {}.getType();
	// @fmtOn

	private Integer sourceline;

	private String sourcefile;

	private Integer line; // Alias used when sending as a catalog resource

	private String file; // Alias used when sending as a catalog resource

	private Boolean exported;

	private List<String> tags;

	private String title;

	private String type;

	private String certname;

	private Map<String, Object> parameters;

	/**
	 * @return the certname
	 */
	public String getCertname() {
		return certname;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * @return the sourcefile
	 */
	public String getSourcefile() {
		return sourcefile == null
				? file
				: sourcefile;
	}

	/**
	 * @return the sourceline
	 */
	public Integer getSourceline() {
		return sourceline == null
				? line
				: sourceline;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the exported
	 */
	public Boolean isExported() {
		return exported;
	}

	/**
	 * @param certname
	 *            the certname to set
	 */
	public void setCertname(String certname) {
		this.certname = certname;
	}

	/**
	 * @param exported
	 *            the exported to set
	 */
	public void setExported(Boolean exported) {
		this.exported = exported;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.sourcefile = null;
		this.file = file;
	}

	/**
	 * @param line
	 *            the line to set
	 */
	public void setLine(Integer line) {
		this.sourceline = null;
		this.line = line;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
