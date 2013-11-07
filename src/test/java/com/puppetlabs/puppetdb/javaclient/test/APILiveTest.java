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

import static com.puppetlabs.puppetdb.javaclient.query.Query.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.puppetlabs.puppetdb.javaclient.BasicAPIPreferences;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClientFactory;
import com.puppetlabs.puppetdb.javaclient.model.*;
import com.puppetlabs.puppetdb.javaclient.model.Event.Status;
import com.puppetlabs.puppetdb.javaclient.model.EventCount.SummarizeBy;
import com.puppetlabs.puppetdb.javaclient.query.Paging;

@SuppressWarnings("javadoc")
public class APILiveTest {
	private PuppetDBClient client;

	private static String PUPPETDB_HOST;

	private static int PUPPETDB_PORT;

	private static String NODE_THAT_IS_KNOWN_TO_EXIST;

	private static File SSL_DIR;

	private static String PEM_NAME;

	@BeforeClass
	public static void beforeClass() {
		PUPPETDB_HOST = getRequiredProperty("puppetdb.hostname");
		PUPPETDB_PORT = Integer.parseInt(getRequiredProperty("puppetdb.port"));
		NODE_THAT_IS_KNOWN_TO_EXIST = PUPPETDB_HOST;
		SSL_DIR = new File(getRequiredProperty("ssldir"));
		PEM_NAME = PUPPETDB_HOST + ".pem";
	}

	private static String getRequiredProperty(String propertyName) {
		String prop = System.getProperty(propertyName);
		assertNotNull("Missing property '" + propertyName + '\'', prop);
		return prop;
	}

	@Before
	public void before() {
		BasicAPIPreferences prefs = new BasicAPIPreferences();
		prefs.setServiceHostname(PUPPETDB_HOST);
		prefs.setServiceSSLPort(PUPPETDB_PORT);
		prefs.setAllowAllHosts(false);
		File caCertPem = new File(new File(SSL_DIR, "ca"), "ca_crt.pem");
		if(caCertPem.canRead())
			prefs.setCaCertPEM(caCertPem);
		prefs.setCertPEM(new File(new File(SSL_DIR, "certs"), PEM_NAME));
		prefs.setPrivateKeyPEM(new File(new File(SSL_DIR, "private_keys"), PEM_NAME));
		client = PuppetDBClientFactory.newClient(prefs);
	}

	@Test
	public void getActiveNodes() throws Exception {
		List<Node> nodes = client.getActiveNodes(null);
		assertNotNull("should not return a null list", nodes);
		assertEquals("should return all nodes", 1, nodes.size());
	}

	@Test
	public void getAggregatedEventCounts() throws Exception {
		AggregatedEventCount aec = client.getAggregatedEventCounts(
			null, eq(Event.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST), SummarizeBy.certname, null);
		assertNotNull("should not return a null instance", aec);
		assertTrue("should have a successful count > 0", aec.getSuccesses() > 0);
		assertTrue("should have a total count > 0", aec.getTotal() > 0);
	}

	@Test
	public void getClassesWithQuery() throws Exception {
		List<Resource> classes = client.getResources(or(eq(Resource.TITLE, "main"), eq(Resource.TITLE, "Settings")), "Class");
		assertNotNull("should not return a null list", classes);
		assertEquals("should return two class resources", 2, classes.size());
	}

	@Test
	public void getClassMain() throws Exception {
		List<Resource> classes = client.getResources(null, "Class", "main");
		assertNotNull("should not return a null list", classes);
		assertEquals("should return one class resource", 1, classes.size());
		assertEquals("should have title main", "main", classes.get(0).getTitle());
	}

	@Test
	public void getEventCounts() throws Exception {
		List<EventCount> eventCounts = client.getEventCounts(
			orderBy(null, orderByField(EventCount.FAILURES, false)), eq(Event.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST), SummarizeBy.certname,
			null);
		assertNotNull("should not return a null list", eventCounts);
		assertTrue("should return events", eventCounts.size() > 0);
		assertTrue("should have a successful count > 0", eventCounts.get(0).getSuccesses() > 0);
	}

	@Test
	public void getEvents() throws Exception {
		Paging<Event> paging = new Paging<Event>(eq(Event.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST), 0, 10, true);
		List<Event> events = client.getEvents(paging);
		assertNotNull("should not return a null list", events);
		assertTrue("should return events", events.size() > 0);
		assertTrue("should return total count", paging.getTotalCount() > 0);
	}

	@Test
	public void getFactNames() throws Exception {
		List<String> names = client.getFactNames();
		assertNotNull("should not return a null list", names);
		assertTrue("should return at least 1 name", names.size() >= 1);
	}

	@Test
	public void getFactsWithClass() throws Exception {
		List<Fact> facts = client.getFacts(and(
			eq(Fact.NAME, "hostname"),
			inResources(Fact.CERTNAME, Resource.CERTNAME, and(eq(Resource.TYPE, "Class"), eq(Resource.TITLE, "main")))));
		assertNotNull("should not return a null list", facts);
		assertEquals("should return 1 fact", 1, facts.size());
	}

	@Test
	public void getHostnameFact() throws Exception {
		List<Fact> facts = client.getFacts(eq(Fact.NAME, "hostname"));
		assertNotNull("should not return a null list", facts);
		assertEquals("should return all facts", 1, facts.size());
	}

	@Test
	public void getMetrics() throws Exception {
		Map<String, String> metrics = client.getMetrics();
		assertNotNull("should not return a null map", metrics);
		assertTrue("should return the at least 30 metrics", metrics.size() >= 30);
	}

	@Test
	public void getNodeStatus() throws Exception {
		Node node = client.getNodeStatus(NODE_THAT_IS_KNOWN_TO_EXIST);
		assertNotNull("should not return null", node);
	}

	@Test
	public void getNodesWithClass() throws Exception {
		List<Node> node = client.getActiveNodes(inResources(
			Node.NAME, Resource.CERTNAME, and(eq(Resource.TYPE, "Class"), eq(Resource.TITLE, "Settings"))));
		assertNotNull("should not return a null list", node);
		assertEquals("should return one node", 1, node.size());
	}

	@Test
	public void getNodesWithFacts() throws Exception {
		List<Node> node = client.getActiveNodes(inFacts(
			Node.NAME, Fact.CERTNAME, and(eq(Fact.NAME, "puppetversion"), match(Fact.VALUE, "^3\\.3.*"))));
		assertNotNull("should not return a null list", node);
		assertEquals("should return one node", 1, node.size());
	}

	@Test
	public void getNumberOfNodesMetric() throws Exception {
		Map<String, Object> numberOfNodesMetric = client.getMetric("com.puppetlabs.puppetdb.query.population:type=default,name=num-nodes");
		assertNotNull("should not return a null map", numberOfNodesMetric);
		assertEquals("should return 1 metric", 1, numberOfNodesMetric.size());
		Object value = numberOfNodesMetric.get("Value");
		assertNotNull("should return an attribute named 'Value'", value);
		assertTrue("should return an numeric value", value instanceof Number);
	}

	@Test
	public void getReports() throws Exception {
		List<Report> reports = client.getReports(eq(Report.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST));
		assertNotNull("should not return a null list", reports);
		assertTrue("should return reports", reports.size() > 0);
		assertNotNull("should have a transaction UUID", reports.get(0).getTransactionUUID());
	}

	@Test
	public void getServerTime() throws Exception {
		Date serverTime = client.getServerTime();
		assertNotNull("should not return a null date", serverTime);
		assertTrue("should return a reasonable timestamp", Math.abs(System.currentTimeMillis() - serverTime.getTime()) < 1000);
	}

	@Test
	public void getVersion() throws Exception {
		String version = client.getVersion();
		assertNotNull("should not return a null version", version);
		assertTrue("should return a version", version.matches("[1-9]+\\.[0-9]+\\.[0-9]"));
	}

	@Test
	public void replaceFacts() throws Exception {
		// Retrieve the current facts
		List<String> names = client.getFactNames();
		assertNotNull("should not return a null list", names);
		Map<String, String> values = new HashMap<String, String>(names.size());
		for(String factName : names) {
			List<Fact> facts = client.getFacts(eq(Fact.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST), factName);
			if(facts.size() > 0)
				values.put(factName, facts.get(0).getValue());
		}

		String origHostName = values.put("hostname", "www.example.com");

		Facts newFacts = new Facts();
		newFacts.setCertname(NODE_THAT_IS_KNOWN_TO_EXIST);
		newFacts.setValues(values);
		UUID uuid = client.replaceFacts(newFacts);
		assertNotNull("should not return a null command uuid", uuid);

		// Command is asynchronous. Give it some time
		Thread.sleep(2000);
		List<Fact> facts = client.getFacts(eq(Fact.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST), "hostname");
		assertEquals("should have replaced the 'hostname' fact", "www.example.com", facts.get(0).getValue());

		// Restore the old value
		values.put("hostname", origHostName);
		assertNotNull("should not return a null command uuid", client.replaceFacts(newFacts));
	}

	@Test
	public void storeReport() throws Exception {
		List<Report> reports = client.getReports(eq(Report.CERTNAME, NODE_THAT_IS_KNOWN_TO_EXIST));
		assertNotNull("should not return a null list", reports);
		assertTrue("should return reports", reports.size() > 0);
		Report template = reports.get(0);
		Report report = new Report();
		report.setCertname(template.getCertname());
		report.setPuppetVersion(template.getPuppetVersion());
		report.setConfigurationVersion(template.getConfigurationVersion());
		report.setReportFormat(template.getReportFormat());
		Date now = new Date();
		report.setStartTime(new Date(now.getTime() - 1000));
		report.setEndTime(now);

		List<Event> events = new ArrayList<Event>();
		Event event = new Event();
		event.setMessage("Test event 1");
		event.setStatus(Status.success);
		event.setTimestamp(new Date(now.getTime() - 500));
		event.setResourceType("Test");
		event.setResourceTitle("dummy");
		events.add(event);

		event = new Event();
		event.setMessage("Test event 2");
		event.setStatus(Status.success);
		event.setTimestamp(new Date(now.getTime() - 400));
		event.setResourceType("Test");
		event.setResourceTitle("dummy");
		events.add(event);

		report.setResourceEvents(events);
		UUID cmdId = client.storeReport(report);
		assertNotNull("should not return a null command uuid", cmdId);
	}

}
