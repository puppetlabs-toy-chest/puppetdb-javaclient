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

import static com.puppetlabs.puppetdb.javaclient.model.Resource.TAG;
import static com.puppetlabs.puppetdb.javaclient.model.Resource.TITLE;
import static com.puppetlabs.puppetdb.javaclient.model.Resource.TYPE;
import static com.puppetlabs.puppetdb.javaclient.query.Query.and;
import static com.puppetlabs.puppetdb.javaclient.query.Query.eq;
import static com.puppetlabs.puppetdb.javaclient.query.Query.match;
import static com.puppetlabs.puppetdb.javaclient.query.Query.not;
import static com.puppetlabs.puppetdb.javaclient.query.Query.or;
import static com.puppetlabs.puppetdb.javaclient.query.Query.parameter;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ExpressionTest {
	@Test
	public void testAnd() {
		assertEquals(
			"Should produce JSON for and", "[\"and\",[\"=\",\"type\",\"User\"],[\"~\",\"tag\",\"magical\"]]",
			and(eq(TYPE, "User"), match(TAG, "magical")).toString());
	}

	@Test
	public void testEqual() {
		assertEquals("Should produce JSON for equal", "[\"=\",\"type\",\"User\"]", eq(TYPE, "User").toString());
	}

	@Test
	public void testMatch() {
		assertEquals("Should produce JSON for match", "[\"~\",\"tag\",\"magical\"]", match(TAG, "magical").toString());
	}

	@Test
	public void testNot() {
		assertEquals("Should produce JSON for not", "[\"not\",[\"=\",\"title\",\"foo\"]]", not(eq(TITLE, "foo")).toString());
	}

	@Test
	public void testOr() {
		assertEquals(
			"Should produce JSON for or", "[\"or\",[\"not\",[\"=\",\"type\",\"User\"]],[\"~\",\"tag\",\"magical\"]]",
			or(not(eq(TYPE, "User")), match(TAG, "magical")).toString());
	}

	@Test
	public void testParameterEqual() {
		assertEquals(
			"Should produce JSON for parameterEqual", "[\"=\",[\"parameter\",\"ensure\"],\"enabled\"]",
			eq(parameter("ensure"), "enabled").toString());
	}

	@Test
	public void testParameterMatch() {
		assertEquals(
			"Should produce JSON for parameterMatch", "[\"~\",[\"parameter\",\"home\"],\"foobar\"]",
			match(parameter("home"), "foobar").toString());
	}
}
