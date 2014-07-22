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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.puppetlabs.puppetdb.javaclient.model.*;
import com.puppetlabs.puppetdb.javaclient.model.EventCount.CountBy;
import com.puppetlabs.puppetdb.javaclient.model.EventCount.SummarizeBy;
import com.puppetlabs.puppetdb.javaclient.query.Expression;
import com.puppetlabs.puppetdb.javaclient.query.OrderBy;
import com.puppetlabs.puppetdb.javaclient.query.Paging;
import com.puppetlabs.puppetdb.javaclient.query.Parameters;
import com.puppetlabs.puppetdb.javaclient.query.Query;

/**
 * <p>
 * The PuppetDBClient implements the PuppetDB API. It contains all methods needed to query for nodes, facts, resources, and reports.
 * </p>
 * <p>
 * Methods that returns lists can perform ordering and paging. This is done by wrapping the {@link Expression query terms} in an
 * {@link OrderBy} instance using the method {@link Query#orderBy(Expression, List)}. The <code>OrderBy</code> instance can then be passed
 * to the {@link Paging#Paging(OrderBy, int, int, boolean)} constructor. The OrderBy can be skipped instead using the constructor
 * {@link Paging#Paging(Expression, int, int, boolean)} and the expression can be omitted by just passing it as <code>null</code> (this
 * applies to both OrderBy and Paging).
 *
 * @see OrderBy
 * @see Paging
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
	List<Node> getActiveNodes(Parameters<Node> query) throws IOException;

	/**
	 * This will return count information about all of the resource events matching the given query. For a given object type (resource,
	 * containing-class, or node), you can retrieve counts of the number of events on objects of that type that had a status of success,
	 * failure, noop, or skip.
	 *
	 * @param query
	 *            The query used to filter the returned set.
	 * @return The list of matching events. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */

	/**
	 * This will return aggregated count information about all of the resource events matching the given query.
	 *
	 * @param params
	 *            Pagination, OrderBy or query Expression that will be applied to the final result
	 * @param eventQuery
	 *            Query that filters the events to consider
	 * @param summarizeBy
	 *            Specifies which type of object you’d like to see counts for. Defaults to {@link SummarizeBy#CERTNAME}
	 * @param countBy
	 *            A string specifying what type of object is counted when building up the counts of successes, failures, noops,
	 *            and skips. Defaults to {@link CountBy#CERTNAME}
	 * @return The list of event counts
	 * @throws IOException
	 */
	AggregatedEventCount getAggregatedEventCounts(Expression<EventCount> eventCountQuery, Expression<Event> eventQuery,
			SummarizeBy summarizeBy, CountBy countBy) throws IOException;

	/**
	 * This will return count information about all of the resource events matching the given query. For a given object type (resource,
	 * containing-class, or node), you can retrieve counts of the number of events on objects of that type that had a status of success,
	 * failure, noop, or skip.
	 *
	 * @param params
	 *            Pagination, OrderBy or query Expression that will be applied to the final result
	 * @param eventQuery
	 *            Query that filters the events to consider
	 * @param summarizeBy
	 *            Specifies which type of object you’d like to see counts for. Defaults to {@link SummarizeBy#CERTNAME}
	 * @param countBy
	 *            A string specifying what type of object is counted when building up the counts of successes, failures, noops,
	 *            and skips. Defaults to {@link CountBy#CERTNAME}
	 * @return The list of event counts
	 * @throws IOException
	 */
	List<EventCount> getEventCounts(Parameters<EventCount> params, Expression<Event> eventQuery, SummarizeBy summarizeBy, CountBy countBy)
			throws IOException;

	/**
	 * Queries the database for events.
	 *
	 * @param query
	 *            The query used to filter the returned set.
	 * @return The list of matching events. Can be empty but never <code>null</code>.
	 * @throws IOException
	 */
	List<Event> getEvents(Parameters<Event> query) throws IOException;

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
	List<Fact> getFacts(Parameters<Fact> query, String... factQualifiers) throws IOException;

	/**
	 * Returns a map attributes for the metric identified by the given <code>metricName</code>.
	 *
	 * @param metricName
	 *            The name of a valid MBean</li>
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
	List<Fact> getNodeFacts(Parameters<Fact> query, String node, String... factQualifiers) throws IOException;

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
	List<Resource> getNodeResources(Parameters<Resource> query, String node, String... resourceQualifiers) throws IOException;

	/**
	 * Queries the database for the status of a specific node.
	 *
	 * @param node
	 *            The node for which to obtain status.
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
	List<Report> getReports(Parameters<Report> query) throws IOException;

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
	List<Resource> getResources(Parameters<Resource> query, String... resourceQualifiers) throws IOException;

	/**
	 * Returns the current time from the clock on the PuppetDB server
	 *
	 * @return The current time on the PuppetDB server
	 */
	Date getServerTime() throws IOException;

	/**
	 * Returns the version of the running PuppetDB server
	 *
	 * @return The version of the server
	 */
	String getVersion() throws IOException;

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
