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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.puppetlabs.puppetdb.javaclient.model.Catalog;
import com.puppetlabs.puppetdb.javaclient.model.Event;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Facts;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.model.Report;
import com.puppetlabs.puppetdb.javaclient.model.Resource;
import com.puppetlabs.puppetdb.javaclient.query.Expression;

/**
 * The PuppetDBClient implements the PuppetDB API. It contains all methods needed to
 * query for nodes, facts, resources, and reports.
 */
public interface PuppetDBClient {

	/**
	 * @param node
	 *            The name of a node which will be deactivated effective as of the time the command is <i>processed</i>.
	 * @return a UUID corresponding to the submitted command
	 * @throws IOException
	 */
	UUID deactivateNode(String node) throws IOException;

	/**
	 * Queries the database for active nodes.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @return The list of matching nodes. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Node> getActiveNodes(Expression<Node> query) throws IOException;

	/**
	 * Queries the database for events.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @return The list of matching events. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Event> getEvents(Expression<Event> query) throws IOException;

	/**
	 * Queries the database for an alphabetical list of all known fact names <i>including</i> those
	 * which are known only for deactivated nodes.
	 * 
	 * @return The alphabetical list of names. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<String> getFactNames() throws IOException;

	/**
	 * Queries the database for facts. The facts can be qualified by
	 * name and value as additional qualifiers.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @param factQualifiers
	 *            The optional fact qualifiers. Either none, just a name, or a name followed by a value.
	 * @return The list of matching facts. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Fact> getFacts(Expression<Fact> query, String... factQualifiers) throws IOException;

	/**
	 * Returns a map attributes for the metric identified by the given <code>metricName</code>.
	 * 
	 * @param metricName
	 *            The name of a valid MBean</li>
	 * 
	 * @return A map of metric attributes where the key is the name of the attribute and the
	 *         value is the attribute value (a String, Number, or Boolean)
	 * @throws IOException
	 */
	Map<String, Object> getMetric(String metricName) throws IOException;

	/**
	 * Returns a map of all available metrics:
	 * <ul>
	 * <li>The key is the name of a valid MBean</li>
	 * <li>The value is a string representing a URI to use for requesting that MBean's attributes</li>
	 * </ul>
	 * 
	 * @return A map with mbean => URI mappings
	 * @throws IOException
	 */
	Map<String, String> getMetrics() throws IOException;

	/**
	 * Queries the database for facts that belongs to a specific node. The facts can be qualified by
	 * specifying fact name and fact value as additional qualifiers.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @param node
	 *            The node for which to obtain facts.
	 * @param factQualifiers
	 *            The optional fact qualifiers. Either none, just a name, or a name followed by a value.
	 * @return The list of matching facts. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Fact> getNodeFacts(Expression<Node> query, String node, String... factQualifiers) throws IOException;

	/**
	 * Queries the database for resources that belongs to a specific node. The resources can be qualified by
	 * specifying resource type and resource title as additional qualifiers.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @param node
	 *            The node for which to obtain resources.
	 * @param resourceQualifiers
	 *            The optional resource qualifiers. Either none, just a type, or a type followed by a title.
	 * @return The list of matching resources. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Resource> getNodeResources(Expression<Node> query, String node, String... resourceQualifiers) throws IOException;

	/**
	 * Queries the database for the status of a specific node.
	 * 
	 * @param node
	 *            The node for which to obtain status.
	 * 
	 * @return The node status or <code>null</code> if it the node could not be found.
	 * @throws IOException
	 */
	Node getNodeStatus(String node) throws IOException;

	/**
	 * Queries the database for reports.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @return The list of matching reports. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Report> getReports(Expression<Report> query) throws IOException;

	/**
	 * Queries the database for resources. The resources can be qualified by
	 * specifying resource type and resource title as additional qualifiers.
	 * 
	 * @param query
	 *            The query used to filter the returned set.
	 * @param resourceQualifiers
	 *            The optional resource qualifiers. Either none, just a type, or a type followed by a title.
	 * @return The list of matching resources. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Resource> getResources(Expression<Resource> query, String... resourceQualifiers) throws IOException;

	/**
	 * @param catalog
	 *            The catalog to replace
	 * @return a UUID corresponding to the submitted command
	 * @throws IOException
	 */
	UUID replaceCatalog(Catalog catalog) throws IOException;

	/**
	 * @param facts
	 *            Facts to replace
	 * @return a UUID corresponding to the submitted command
	 * @throws IOException
	 */
	UUID replaceFacts(Facts facts) throws IOException;

	/**
	 * @param report
	 *            A report containing events that occured on Puppet resources
	 * @return a UUID corresponding to the submitted command
	 * @throws IOException
	 */
	UUID storeReport(Report report) throws IOException;
}
