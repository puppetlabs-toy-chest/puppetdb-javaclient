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

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

import com.google.inject.Inject;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.model.Catalog;
import com.puppetlabs.puppetdb.javaclient.model.Entity;
import com.puppetlabs.puppetdb.javaclient.model.Event;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Facts;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.model.Report;
import com.puppetlabs.puppetdb.javaclient.model.Resource;
import com.puppetlabs.puppetdb.javaclient.query.Expression;

/**
 * Default implementation of the PuppetDBClient
 */
public class PuppetDBClientImpl implements PuppetDBClient {
	/**
	 * Builds a path from the given key and all qualifiers. The key is expected to start, but not end
	 * with a slash. The qualifiers must not start nor end with a slash. The final string will not
	 * end with a slash.
	 * 
	 * @param bld
	 *            The receiver of the path
	 * @param key
	 *            The key that will be first in the path
	 * @param qualifiers
	 *            The optional qualifiers to append to the path
	 * @return The string representation of the path
	 */
	protected static String buildPath(StringBuilder bld, String key, String[] qualifiers) {
		bld.append(key);
		for(String qualifier : qualifiers) {
			bld.append('/');
			bld.append(qualifier);
		}
		return bld.toString();
	}

	/**
	 * Converts the given query expression into JSON a string and returns singleton map
	 * where the key is &quot;query&quot; and the value is the JSON representation of the
	 * given query. This method returns <code>null</code> if the argument is <code>null</code>.
	 * 
	 * @param query
	 *            The query to convert or <code>null</code>
	 * @return The singleton map or <code>null</code>
	 */
	protected static Map<String, String> queryAsMap(Expression<?> query) {
		if(query == null)
			return null;

		StringBuilder bld = new StringBuilder();
		query.toJSON(bld);
		return Collections.singletonMap("query", bld.toString());
	}

	private final HttpConnector connector;

	/**
	 * <p>
	 * Creates a new PuppetDBClient instance. This constructor
	 * </p>
	 * <p>
	 * <b>For Guice injection only.</b> Don't use this constructor from code
	 * </p>
	 * 
	 * @param connector
	 *            The connector responsible for all HTTP requests
	 */
	@Inject
	public PuppetDBClientImpl(HttpConnector connector) {
		this.connector = connector;
	}

	@Override
	public UUID deactivateNode(String node) throws IOException {
		return postCommand("deactivate node", 1, node);
	}

	@Override
	public List<Node> getActiveNodes(Expression<Node> query) throws IOException {
		return getListResponse("/nodes", queryAsMap(query), Node.LIST);
	}

	@Override
	public List<Event> getEvents(Expression<Event> query) throws IOException {
		return getListResponse("../experimental/events", queryAsMap(query), Event.LIST);
	}

	@Override
	public List<String> getFactNames() throws IOException {
		return getListResponse("/fact-names", null, Entity.LIST_STRING);
	}

	@Override
	public List<Fact> getFacts(Expression<Fact> query, String... factQualifiers) throws IOException {
		StringBuilder bld = new StringBuilder();
		return getListResponse(buildPath(bld, "/facts", factQualifiers), queryAsMap(query), Fact.LIST);
	}

	/**
	 * Executes the request and converts the result into a list of the desired <code>type</code>. If the request results in a
	 * {@link HttpStatus#SC_NOT_FOUND}, then this method will
	 * return an empty list.
	 * 
	 * @param uriStr
	 *            The relative path to the endpoint
	 * @param params
	 *            Parameters to pass in the request
	 * @param type
	 *            The expected return type (must be a generic List declaration)
	 * @return The response in list form or an empty list in case no data was found
	 * @throws IOException
	 */
	protected <V> List<V> getListResponse(String uriStr, Map<String, String> params, Type type) throws IOException {
		try {
			return connector.get(uriStr, params, type);
		}
		catch(HttpResponseException e) {
			if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND)
				return Collections.emptyList();
			throw e;
		}
	}

	/**
	 * Executes the request and converts the result into a map of the desired <code>type</code>. If the request results in a
	 * {@link HttpStatus#SC_NOT_FOUND}, then this method will
	 * return an empty map.
	 * 
	 * @param uriStr
	 *            The relative path to the endpoint
	 * @param params
	 *            Parameters to pass in the request
	 * @param type
	 *            The expected return type (must be a generic List declaration)
	 * @return The response in list form or an empty list in case no data was found
	 * @throws IOException
	 */
	protected <K, V> Map<K, V> getMapResponse(String uriStr, Map<String, String> params, Type type) throws IOException {
		try {
			return connector.get(uriStr, params, type);
		}
		catch(HttpResponseException e) {
			if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND)
				return Collections.emptyMap();
			throw e;
		}
	}

	@Override
	public Map<String, Object> getMetric(String metricName) throws IOException {
		return getMapResponse("/metrics/mbean/" + URLEncoder.encode(metricName, HttpConnector.UTF_8.name()), null, Entity.MAP_STRING_OBJECT);
	}

	@Override
	public Map<String, String> getMetrics() throws IOException {
		return getMapResponse("/metrics/mbeans", null, Entity.MAP_STRING_STRING);
	}

	@Override
	public List<Fact> getNodeFacts(Expression<Node> query, String node, String... factQualifiers) throws IOException {
		StringBuilder bld = new StringBuilder("/nodes/");
		bld.append(URLEncoder.encode(node, HttpConnector.UTF_8.name()));
		return getListResponse(buildPath(bld, "/facts", factQualifiers), queryAsMap(query), Resource.LIST);
	}

	@Override
	public List<Resource> getNodeResources(Expression<Node> query, String node, String... resourceQualifiers) throws IOException {
		StringBuilder bld = new StringBuilder("/nodes/");
		bld.append(URLEncoder.encode(node, HttpConnector.UTF_8.name()));
		return getListResponse(buildPath(bld, "/resources", resourceQualifiers), queryAsMap(query), Resource.LIST);
	}

	@Override
	public Node getNodeStatus(String node) throws IOException {
		return getSingletonResponse("/nodes/" + URLEncoder.encode(node, HttpConnector.UTF_8.name()), null, Node.class);
	}

	@Override
	public List<Report> getReports(Expression<Report> query) throws IOException {
		return getListResponse("../experimental/reports", queryAsMap(query), Report.LIST);
	}

	@Override
	public List<Resource> getResources(Expression<Resource> query, String... resourceQualifiers) throws IOException {
		StringBuilder bld = new StringBuilder();
		return getListResponse(buildPath(bld, "/resources", resourceQualifiers), queryAsMap(query), Resource.LIST);
	}

	/**
	 * Executes the request and converts the result into an object of the desired <code>type</code>. If the request results in a
	 * {@link HttpStatus#SC_NOT_FOUND}, then this method will return <code>null</code>.
	 * 
	 * @param uriStr
	 *            The relative path to the endpoint
	 * @param params
	 *            Parameters to pass in the request
	 * @param type
	 *            The expected return type
	 * @return The response or <code>null</code> in case no data was found
	 * @throws IOException
	 */
	protected <V> V getSingletonResponse(String uriStr, Map<String, String> params, Type type) throws IOException {
		try {
			return connector.get(uriStr, params, type);
		}
		catch(HttpResponseException e) {
			if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND)
				return null;
			throw e;
		}
	}

	protected UUID postCommand(String command, int version, Object payload) throws IOException {
		CommandObject cmdObj = new CommandObject();
		cmdObj.setCommand(command);
		cmdObj.setVersion(version);
		cmdObj.setPayload(payload);

		String json = connector.toJSON(cmdObj);
		Map<String, String> params = new HashMap<String, String>();
		params.put("payload", json);
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			params.put("checksum", Hex.encodeHexString(md.digest(json.getBytes(HttpConnector.UTF_8))));
		}
		catch(NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}

		CommandResponse response = connector.post("/commands/", params, CommandResponse.class);
		return response == null
				? null
				: UUID.fromString(response.getUuid());
	}

	@Override
	public UUID replaceCatalog(Catalog catalog) throws IOException {
		return postCommand("replace catalog", 2, catalog);
	}

	@Override
	public UUID replaceFacts(Facts facts) throws IOException {
		// TODO: This is rather odd since we json encode something that will
		// be json encoded again.
		String jsonFacts = connector.toJSON(facts);
		return postCommand("replace facts", 1, jsonFacts);
	}

	@Override
	public UUID storeReport(Report report) throws IOException {
		return postCommand("store report", 1, report);
	}
}
