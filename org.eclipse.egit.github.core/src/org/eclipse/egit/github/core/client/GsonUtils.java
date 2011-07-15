/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Gson utilities.
 */
public abstract class GsonUtils {

	private static final Gson GSON = createGson();

	/**
	 * Create the standard {@link Gson} configuration
	 * 
	 * @return created gson, never null
	 */
	public static final Gson createGson() {
		return new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateFormatter())
				.setFieldNamingPolicy(
						FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.serializeNulls().create();
	}

	/**
	 * Convert object to json
	 * 
	 * @param object
	 * @return json string
	 */
	public static final String toJson(Object object) {
		return GSON.toJson(object);
	}

	/**
	 * Convert string to given type
	 * 
	 * @param json
	 * @param type
	 * @return instance of type
	 */
	public static final <V> V fromJson(String json, Class<V> type) {
		return GSON.fromJson(json, type);
	}

	/**
	 * Convert string to given type
	 * 
	 * @param json
	 * @param type
	 * @return instance of type
	 */
	public static final <V> V fromJson(String json, Type type) {
		return GSON.fromJson(json, type);
	}

	/**
	 * Convert content of reader to given type
	 * 
	 * @param reader
	 * @param type
	 * @return instance of type
	 */
	public static final <V> V fromJson(Reader reader, Class<V> type) {
		return GSON.fromJson(reader, type);
	}

	/**
	 * Convert content of reader to given type
	 * 
	 * @param reader
	 * @param type
	 * @return instance of type
	 */
	public static final <V> V fromJson(Reader reader, Type type) {
		return GSON.fromJson(reader, type);
	}
}
