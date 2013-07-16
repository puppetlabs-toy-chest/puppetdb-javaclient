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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * A connector responsible for all HTTP interactions.
 */
public interface HttpConnector {
	/**
	 * The content type of posts and responses.
	 */
	String CONTENT_TYPE_JSON = "application/json"; //$NON-NLS-1$

	/**
	 * The content type of posts and responses.
	 */
	String CONTENT_TYPE_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"; //$NON-NLS-1$

	/**
	 * The string used when presenting us to the server
	 */
	String USER_AGENT = "PuppetDB Java Client/0.1.0"; //$NON-NLS-1$

	/**
	 * The encoding used by the API
	 */
	Charset UTF_8 = Charset.forName("UTF-8"); //$NON-NLS-1$

	/**
	 * Cleanly abort the currently executing request. This method does nothing if there is
	 * no executing request.
	 */
	void abortCurrentRequest();

	/**
	 * Send DELETE request to URI
	 * 
	 * @param urlStr
	 *            The relative URI end-point
	 * @throws IOException
	 */
	void delete(String urlStr) throws IOException;

	/**
	 * Performs a GET command and writes the response to <code>output</code>.
	 * 
	 * @param urlStr
	 *            The relative URI end-point
	 * @param params
	 * @param output
	 * @throws IOException
	 */
	void download(String urlStr, Map<String, String> params, OutputStream output) throws IOException;

	/**
	 * Executes a HTTP GET request. The http response is expected to be a JSON representation of
	 * an object of the specified <code>type</code>. The object is parsed and returned.
	 * 
	 * @param urlStr
	 *            The relative URI end-point
	 * @param params
	 *            Parameters to include in the URL
	 * @param type
	 *            The expected type of the result
	 * @return An object of the expected type
	 * @throws IOException
	 *             if the request could not be completed
	 */
	<V> V get(String urlStr, Map<String, String> params, Type type) throws IOException;

	/**
	 * Patch data to URI
	 * 
	 * @param urlStr
	 *            The relative URI end-point
	 * @param params
	 * @param class1
	 * @param type
	 *            The expected type of the result
	 * @return The response from the PATH request
	 * @throws IOException
	 */
	<V> V patch(String urlStr, Map<String, String> params, Class<V> type) throws IOException;

	/**
	 * Post data to URI
	 * 
	 * @param <V>
	 * @param urlStr
	 *            The relative URI end-point
	 * @param params
	 * @param type
	 * @return response
	 * @throws IOException
	 */
	<V> V post(String urlStr, Map<String, String> params, Class<V> type) throws IOException;

	/**
	 * Post using a MultiPart entity
	 * 
	 * @param urlStr
	 *            The relative URI end-point
	 * @param stringParts
	 *            Optional String parts to include in the post
	 * @param in
	 *            The stream from which data will be read
	 * @param mimeType
	 *            The mime type of the data
	 * @param fileName
	 *            The name of the data file
	 * @param fileSize
	 *            The size of the file in bytes
	 * @param type
	 *            The type of the expected return value
	 * @return The response from the POST request
	 * @throws IOException
	 */
	<V> V postUpload(String urlStr, Map<String, String> stringParts, InputStream in, String mimeType, String fileName, long fileSize,
			Class<V> type) throws IOException;

	/**
	 * Send data using a PUT request
	 * 
	 * @param <V>
	 * @param urlStr
	 *            The relative URI end-point
	 * @param params
	 *            The data to send
	 * @param type
	 *            The type of the response
	 * @return response The response of the PUT request
	 * @throws IOException
	 */
	<V> V put(String urlStr, Map<String, String> params, Class<V> type) throws IOException;

	/**
	 * Convert object to a JSON string
	 * 
	 * @param object
	 *            The object to convert
	 * @return JSON string
	 */
	String toJSON(Object object);
}
