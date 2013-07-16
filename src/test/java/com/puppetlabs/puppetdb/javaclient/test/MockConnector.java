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
package com.puppetlabs.puppetdb.javaclient.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;
import com.puppetlabs.puppetdb.javaclient.impl.GsonProvider;

@SuppressWarnings("javadoc")
public class MockConnector implements HttpConnector {

	private static boolean isCollectionType(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			Type rawType = ptype.getRawType();
			return rawType instanceof Class && Collection.class.isAssignableFrom((Class<?>) rawType);
		}
		return false;
	}

	private final Gson gson;

	@Inject
	MockConnector(Gson gson) {
		this.gson = gson;
	}

	@Override
	public void abortCurrentRequest() {
	}

	@Override
	public void delete(String urlStr) throws IOException {
	}

	@Override
	public void download(String urlStr, Map<String, String> params, OutputStream output) throws IOException {
	}

	private void flattenMaps(Map<?, ?> map, List<Object> result) {
		for(Object v : map.values()) {
			if(v instanceof Map)
				flattenMaps((Map<?, ?>) v, result);
			else if(v instanceof Collection)
				result.addAll((Collection<?>) v);
			else
				result.add(v);
		}
	}

	@Override
	public <V> V get(String urlStr, Map<String, String> params, Type type) throws IOException {
		InputStream mockResponses = getClass().getResourceAsStream("/mock_responses.json");
		assertNotNull("Unable to open 'mock_responses.json'", mockResponses);
		Object mocks;
		try {
			mocks = gson.fromJson(new InputStreamReader(mockResponses, UTF_8), Object.class);
		}
		finally {
			mockResponses.close();
		}
		assertTrue("mock_responses did not produce a Map", mocks instanceof Map);

		Object mock = mocks;
		int dash = urlStr.indexOf('/');
		if(dash != 0)
			// The URL must start with a dash
			throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, urlStr);

		int start = 1;
		dash = urlStr.indexOf('/', start);
		if(dash < 0)
			dash = urlStr.length();

		while(dash > 0) {
			String segment = urlStr.substring(start, dash);
			mock = ((Map<?, ?>) mocks).get(segment);
			if(mock == null)
				throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, urlStr);
			if(dash == urlStr.length())
				break;
			if(!(mock instanceof Map<?, ?>))
				throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, urlStr);
			mocks = mock;
			start = dash + 1;
			dash = urlStr.indexOf('/', start);
			if(dash < 0)
				dash = urlStr.length();
		}

		// Check if we are less qualified than the nested map depth in which case we must concatenate
		// the values of the maps below into a list
		if(isCollectionType(type)) {
			if(mock instanceof Map) {
				List<Object> result = new ArrayList<Object>();
				flattenMaps((Map<?, ?>) mock, result);
				mock = result;
			}
		}
		else if(mock instanceof List)
			mock = ((List<?>) mock).get(0);

		// Convert to expected type
		return gson.fromJson(gson.toJson(mock), type);
	}

	@Override
	public <V> V patch(String urlStr, Map<String, String> params, Class<V> type) throws IOException {
		return null;
	}

	@Override
	public <V> V post(String urlStr, Map<String, String> params, Class<V> type) throws IOException {
		return null;
	}

	@Override
	public <V> V postUpload(String urlStr, Map<String, String> stringParts, InputStream in, String mimeType, String fileName,
			long fileSize, Class<V> type) throws IOException {
		return null;
	}

	@Override
	public <V> V put(String urlStr, Map<String, String> params, Class<V> type) throws IOException {
		return null;
	}

	@Override
	public String toJSON(Object object) {
		return GsonProvider.toJSON(object);
	}
}
