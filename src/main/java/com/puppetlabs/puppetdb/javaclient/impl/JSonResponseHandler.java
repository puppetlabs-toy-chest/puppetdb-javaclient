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

import static com.puppetlabs.puppetdb.javaclient.impl.HttpComponentsConnector.getStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.puppetlabs.puppetdb.javaclient.APIException;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;

/**
 * A response handler that expects JSON content.
 * 
 * @param <V>
 */
public class JSonResponseHandler<V> implements ResponseHandler<V> {
	protected static <T> T parseJson(Gson gson, InputStream stream, Type type) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HttpConnector.UTF_8), 2048);
		StringBuilder bld = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) {
			bld.append(line);
			bld.append('\n');
		}
		try {
			return gson.fromJson(bld.toString(), type);
		}
		catch(JsonSyntaxException jpe) {
			throw new APIException("Parse exception converting JSON to object", jpe); //$NON-NLS-1$
		}
		finally {
			try {
				reader.close();
			}
			catch(IOException ignored) {
				// Ignored
			}
		}
	}

	private final Gson gson;

	private final Type type;

	/**
	 * Creates a new handler that will attempt to use the given <code>gson</code> to deserialize a JSON response into an instance
	 * of the given <code>type</code>.
	 * 
	 * @param gson
	 *            The instance used for JSON deserialization
	 * @param type
	 *            The expected type of the response
	 */
	public JSonResponseHandler(Gson gson, Type type) {
		this.gson = gson;
		this.type = type;
	}

	/**
	 * Create exception from response
	 * 
	 * @param response
	 * @param code
	 * @param status
	 * @return non-null newly {@link IOException}
	 */
	protected HttpResponseException createException(InputStream response, int code, String status) {
		String message;
		if(status != null && status.length() > 0)
			message = status;
		else
			message = "Unknown error occurred";
		return new HttpResponseException(code, message);
	}

	@Override
	public V handleResponse(HttpResponse response) throws IOException {
		StatusLine statusLine = response.getStatusLine();
		int code = statusLine.getStatusCode();
		if(code >= 300) {
			String msg;
			try {
				msg = EntityUtils.toString(response.getEntity());
				if(msg == null)
					msg = statusLine.getReasonPhrase();
				else {
					msg = statusLine.getReasonPhrase() + ": " + msg;
				}
			}
			catch(Exception e) {
				// Just skip
				msg = statusLine.getReasonPhrase();
			}
			throw new HttpResponseException(statusLine.getStatusCode(), msg);
		}

		HttpEntity entity = response.getEntity();
		if(isOk(code)) {
			if(type == null)
				return null;
			return parseJson(getStream(entity), type);
		}
		throw createException(getStream(entity), code, statusLine.getReasonPhrase());
	}

	/**
	 * Does status code denote a non-error response?
	 * 
	 * @param code
	 * @return true if okay, false otherwise
	 */
	protected boolean isOk(final int code) {
		switch(code) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_CREATED:
			case HttpStatus.SC_ACCEPTED:
			case HttpStatus.SC_NO_CONTENT: // weird, but returned by DELETE calls
				return true;
			default:
				return false;
		}
	}

	/**
	 * Parse JSON to specified type
	 * 
	 * @param stream
	 * @param type
	 * @return parsed type
	 * @throws IOException
	 */
	protected V parseJson(InputStream stream, Type type) throws IOException {
		return parseJson(gson, stream, type);
	}
}
