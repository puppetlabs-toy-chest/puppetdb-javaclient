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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.puppetlabs.puppetdb.javaclient.APIException;
import com.puppetlabs.puppetdb.javaclient.APIPreferences;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;
import com.puppetlabs.puppetdb.javaclient.query.Paging;

/**
 * Class responsible for all HTTP request and response processing. Based on the
 * Apache {@link HttpClient}.
 */
public class HttpComponentsConnector implements HttpConnector {

	static InputStream getStream(HttpEntity entity) throws IOException {
		if(entity == null)
			return null;

		return entity.getContent();
	}

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

	private final HttpClient httpClient;

	private final APIPreferences preferences;

	private HttpRequestBase currentRequest;

	/**
	 * <p>
	 * Creates a new HttpCommonsConnector.
	 * </p>
	 * <p>
	 * <b>For Guice injection only.</b> Don't use this constructor from code
	 * </p>
	 * 
	 * @param gson
	 *            The instance used when parsing or serializing JSON
	 * @param httpClient
	 *            The client to use for the connection
	 * @param preferences
	 *            API connection preferences
	 */
	@Inject
	public HttpComponentsConnector(Gson gson, HttpClient httpClient, APIPreferences preferences) {
		this.gson = gson;
		this.preferences = preferences;
		this.httpClient = httpClient;
	}

	@Override
	public synchronized void abortCurrentRequest() {
		if(currentRequest != null) {
			currentRequest.abort();
			currentRequest = null;
		}
	}

	protected void assignContent(HttpEntityEnclosingRequestBase request, Map<String, String> params) {
		if(params != null && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for(Map.Entry<String, String> param : params.entrySet())
				pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			try {
				StringEntity entity = new StringEntity(URLEncodedUtils.format(pairs, UTF_8.name()), UTF_8.name());
				entity.setContentType(CONTENT_TYPE_WWW_FORM_URLENCODED);
				request.setEntity(entity);
			}
			catch(UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	protected void configureRequest(final HttpRequestBase request) {
		request.addHeader(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON);
		request.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
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

	private HttpGet createGetRequest(String urlStr, Map<String, String> params) {
		StringBuilder bld = new StringBuilder(createURI(urlStr));
		if(params != null && !params.isEmpty()) {
			List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
			for(Map.Entry<String, String> param : params.entrySet())
				pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			bld.append('?');
			bld.append(URLEncodedUtils.format(pairs, UTF_8.name()));
		}
		return new HttpGet(URI.create(bld.toString()));
	}

	/**
	 * Create full URI from path
	 * 
	 * @param path
	 * @return uri
	 */
	protected String createURI(String path) {
		StringBuilder bld = new StringBuilder();
		if(preferences.getCertPEM() == null)
			bld.append("http://");
		else
			bld.append("https://");

		bld.append(preferences.getServiceHostname());
		bld.append(':');
		bld.append(preferences.getServicePort());
		bld.append('/');
		if(path.startsWith("../"))
			// Skip the 'v3' part (this is probably ../experimental/<something>
			bld.append(path, 3, path.length());
		else {
			bld.append("v3");
			bld.append(path);
		}
		return bld.toString();
	}

	@Override
	public void delete(final String uri) throws IOException {
		HttpDelete request = new HttpDelete(createURI(uri));
		configureRequest(request);
		executeRequest(request, null, null);
	}

	@Override
	public void download(String urlStr, Map<String, String> params, final OutputStream output) throws IOException {
		HttpGet request = createGetRequest(urlStr, params);
		configureRequest(request);
		httpClient.execute(request, new ResponseHandler<Void>() {
			@Override
			public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();
				int code = statusLine.getStatusCode();
				if(code != HttpStatus.SC_OK)
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());

				HttpEntity entity = response.getEntity();
				entity.writeTo(output);
				return null;
			}
		});
	}

	private synchronized void endRequest() {
		currentRequest = null;
	}

	protected <V> V executeRequest(final HttpRequestBase request, final Type type, int[] totalCount) throws IOException {
		startRequest(request);
		try {
			HttpResponse response = httpClient.execute(request);
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

				if(totalCount != null) {
					Header xrecs = response.getFirstHeader("X-Records");
					if(xrecs != null)
						try {
							totalCount[0] = Integer.parseInt(xrecs.getValue());
						}
						catch(NumberFormatException e) {
						}
				}
				return parseJson(gson, getStream(entity), type);
			}
			throw createException(getStream(entity), code, statusLine.getReasonPhrase());
		}
		finally {
			endRequest();
		}
	}

	@Override
	public <V> V get(String urlStr, Map<String, String> params, Type type) throws IOException {
		HttpGet request = createGetRequest(urlStr, params);
		configureRequest(request);
		return executeRequest(request, type, null);
	}

	@Override
	public <V, Q> V get(String urlStr, Paging<Q> params, Type type) throws IOException {
		Map<String, String> queryParams = null;
		int[] totalCount = null;
		if(params != null) {
			queryParams = new HashMap<String, String>();
			params.appendTo(queryParams);
			totalCount = new int[] { -1 };
		}
		HttpGet request = createGetRequest(urlStr, queryParams);
		configureRequest(request);
		V result = executeRequest(request, type, totalCount);
		if(params != null)
			params.setTotalCount(totalCount[0]);
		return result;
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

	@Override
	public <V> V patch(final String uri, final Map<String, String> params, final Class<V> type) throws IOException {
		// HttpPatch is introduced in 4.2. This code is compatible with 4.1 in order to
		// play nice with Eclipse Juno and Kepler
		HttpPost request = new HttpPost(createURI(uri)) {
			@Override
			public String getMethod() {
				return "PATCH";
			}
		};

		configureRequest(request);
		assignContent(request, params);
		return executeRequest(request, type, null);
	}

	@Override
	public <V> V post(final String uri, final Map<String, String> params, final Class<V> type) throws IOException {
		HttpPost request = new HttpPost(createURI(uri));
		configureRequest(request);
		assignContent(request, params);
		return executeRequest(request, type, null);
	}

	@Override
	public <V> V postUpload(String uri, Map<String, String> stringParts, InputStream in, String mimeType, String fileName,
			final long fileSize, Class<V> type) throws IOException {
		HttpPost request = new HttpPost(createURI(uri));
		configureRequest(request);

		MultipartEntity entity = new MultipartEntity();
		for(Map.Entry<String, String> entry : stringParts.entrySet())
			entity.addPart(entry.getKey(), StringBody.create(entry.getValue(), "text/plain", UTF_8));

		entity.addPart("file", new InputStreamBody(in, mimeType, fileName) {
			@Override
			public long getContentLength() {
				return fileSize;
			}
		});
		request.setEntity(entity);
		return executeRequest(request, type, null);
	}

	@Override
	public <V> V put(final String uri, final Map<String, String> params, final Class<V> type) throws IOException {
		HttpPut request = new HttpPut(createURI(uri));
		configureRequest(request);
		assignContent(request, params);
		return executeRequest(request, type, null);
	}

	private synchronized void startRequest(HttpRequestBase request) {
		if(currentRequest != null)
			currentRequest.abort();
		currentRequest = request;
	}

	@Override
	public String toJSON(Object object) {
		return gson.toJson(object);
	}
}
