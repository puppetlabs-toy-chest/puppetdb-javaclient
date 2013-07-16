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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.puppetlabs.puppetdb.javaclient.HttpConnector;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.impl.GsonProvider;
import com.puppetlabs.puppetdb.javaclient.impl.PuppetDBClientImpl;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.model.Resource;

@SuppressWarnings("javadoc")
public class APIMockTest {
	private static Injector injector;

	@BeforeClass
	public static void beforeClass() {
		injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Gson.class).toProvider(GsonProvider.class);
				bind(HttpConnector.class).to(MockConnector.class);
				bind(PuppetDBClient.class).to(PuppetDBClientImpl.class);
			}
		});
	}

	private PuppetDBClient client;

	@Before
	public void before() {
		client = injector.getInstance(PuppetDBClient.class);
	}

	@Test
	public void getActiveNodes() throws Exception {
		List<Node> nodes = client.getActiveNodes(null);
		assertNotNull("should not return a null list", nodes);
		assertEquals("should return all nodes in mock", 2, nodes.size());
	}

	@Test
	public void getFactNames() throws Exception {
		List<String> names = client.getFactNames();
		assertNotNull("should not return a null list", names);
		assertEquals(
			"should return all names in mock", Arrays.asList(new String[] { "kernel", "operatingsystem", "osfamily", "uptime" }), names);
	}

	@Test
	public void getFacts() throws Exception {
		List<Fact> facts = client.getFacts(null);
		assertNotNull("should not return a null list", facts);
		assertEquals("should return all facts in mock", 12, facts.size());
	}

	@Test
	public void getNamedFacts() throws Exception {
		List<Fact> facts = client.getFacts(null, "kernel");
		assertNotNull("should not return a null list", facts);
		assertEquals("should return all facts named 'kernel' in mock", 3, facts.size());
	}

	@Test
	public void getNodeStatus() throws Exception {
		Node node = client.getNodeStatus("a.example.com");
		assertNotNull("should not return null", node);
	}

	@Test
	public void getUserFoo() throws Exception {
		List<Resource> users = client.getResources(null, "User", "foo");
		assertNotNull("should not return a null list", users);
		assertEquals("should return one user resource", 1, users.size());
		assertEquals("should have title foo", "foo", users.get(0).getTitle());
	}

	@Test
	public void getUsers() throws Exception {
		List<Resource> users = client.getResources(null, "User");
		assertNotNull("should not return a null list", users);
		assertEquals("should return two user resources", 2, users.size());
	}
}
