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

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Provider;
import com.puppetlabs.puppetdb.javaclient.model.Event;

/**
 * A provider of {@link Gson} instances.
 */
public class GsonProvider implements Provider<Gson> {
	/**
	 * TODO: Deprecate this adapter when support for Java < 1.7 is dropped. The RFC-822 format is implemented from Java 1.7 and up
	 * A json adapter capable of serializing/deserializing a timestamp with RFC-822 style timezone
	 */
	public static class DateJsonAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
		/**
		 * Convert the given date into a RFC-822 style timestamp
		 * 
		 * @param date
		 *            The date to be converted
		 * @return The string form of the date
		 */
		public static String dateToString(Date date) {
			String target;
			synchronized(ISO_8601_TZ) {
				target = ISO_8601_TZ.format(date);
			}
			Matcher m = RFC_822_PTRN.matcher(target);
			if(m.matches()) {
				String tz = m.group(2);
				if("+0000".equals(tz))
					tz = "Z";
				else
					tz = tz.substring(0, 3) + ':' + tz.substring(3, 5);
				target = m.group(1) + tz;
			}
			return target;
		}

		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String source = json.getAsString();
			Matcher m = ISO_8601_PTRN.matcher(source);
			if(m.matches()) {
				String tz = m.group(2);
				if("Z".equals(tz))
					tz = "+0000";
				else
					tz = tz.substring(0, 3) + tz.substring(4, 6);
				source = m.group(1) + tz;
			}
			synchronized(ISO_8601_TZ) {
				try {
					return ISO_8601_TZ.parse(source);
				}
				catch(ParseException e) {
					throw new JsonParseException(e);
				}
			}
		}

		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(dateToString(src));
		}
	}

	private static final Pattern ISO_8601_PTRN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(?:\\.\\d+)?)(Z|(?:[+-]\\d\\d:\\d\\d))$");

	private static final Pattern RFC_822_PTRN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(?:\\.\\d+)?)([+-]\\d\\d\\d\\d)$");

	private static final SimpleDateFormat ISO_8601_TZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private static final GsonBuilder gsonBuilder;

	private static final Gson gson;

	static {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateJsonAdapter());
        gsonBuilder.registerTypeAdapter(Event.class, new EventJsonAdapter());
		gson = gsonBuilder.create();
	}

	/**
	 * Creates a JSON representation for the given object using an internal
	 * synchronized {@link Gson} instance.
	 * 
	 * @param object
	 *            The object to produce JSON for
	 * @return JSON representation of the given <code>object</code>
	 */
	public static String toJSON(Object object) {
		synchronized(gson) {
			return gson.toJson(object);
		}
	}

	@Override
	public Gson get() {
		return gsonBuilder.create();
	}
}
