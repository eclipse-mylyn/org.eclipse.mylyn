/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Date formatter for multiple date formats present in the GitHub v2 API.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class DateFormatter implements JsonDeserializer<Date> {

	/**
	 * DATE_FORMAT1
	 */
	public static final String DATE_FORMAT1 = "yyyy/MM/dd HH:mm:ss Z";

	/**
	 * DATE_FORMAT2
	 */
	public static final String DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * DATE_FORMAT3
	 */
	public static final String DATE_FORMAT3 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * DATE_FORMAT4
	 */
	public static final String DATE_FORMAT4 = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String[] FORMATS = new String[] { DATE_FORMAT1,
			DATE_FORMAT2, DATE_FORMAT3, DATE_FORMAT4 };

	private List<DateFormat> formats;

	/**
	 * Create date formatter
	 */
	public DateFormatter() {
		this.formats = new LinkedList<DateFormat>();
		TimeZone timeZone = TimeZone.getTimeZone("Zulu");
		for (String format : FORMATS) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(timeZone);
			this.formats.add(dateFormat);
		}
	}

	/**
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 *      java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		String string = json.getAsString();
		Exception exception = null;
		for (DateFormat format : this.formats) {
			try {
				synchronized (format) {
					return format.parse(string);
				}
			} catch (ParseException e) {
				exception = e;
			}
		}
		throw new JsonParseException(exception);
	}

}
