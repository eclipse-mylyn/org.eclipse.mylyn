/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.mylar.internal.trac.core.ITracRepository;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;

/**
 * Represents a Trac ticket as it is retrieved from a Trac repository.
 * 
 * @author Steffen Pingel
 */
public class TracTicket {

	/**
	 * Represents the key of a string propertiy of a ticket.
	 * 
	 * @author Steffen Pingel
	 */
	public enum Key {
		COMPONENT("component"), DESCRIPTION("description"), ID("id"), KEYWORDS("keywords"), MILESTONE("milestone"), OWNER(
				"owner"), PRIORITY("priority"), REPORTER("reporter"), RESOLUTION("resolution"), STATUS("status"), SUMMARY(
				"summary"), TYPE("type"), VERSION("version");

		public static Key fromKey(String name) {
			for (Key key : Key.values()) {
				if (key.toString().equals(name)) {
					return key;
				}
			}
			return null;
		}

		private String key;

		Key(String key) {
			this.key = key;
		}

		public String toString() {
			return key;
		}
	}

	public static final int INVALID_ID = -1;

	private Date created;

	/**
	 * User defined custom ticket fields.
	 * 
	 * @see http://projects.edgewall.com/trac/wiki/TracTicketsCustomFields
	 */
	private Map<String, String> customValueByKey;

	private int id = INVALID_ID;

	private Date lastChanged;

	/** Trac's built-in ticket properties. */
	private Map<Key, String> valueByKey = new HashMap<Key, String>();

	public TracTicket() {
	}

	/**
	 * Constructs a Trac ticket.
	 * 
	 * @param id
	 *            the nummeric Trac ticket id
	 */
	public TracTicket(int id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public int getId() {
		return id;
	}

	public Date getLastChanged() {
		return lastChanged;
	}

	public String getCustomValue(String key) {
		if (customValueByKey == null){
			return null;
		}
		return customValueByKey.get(key);
	}
	
	public String getValue(Key key) {
		return valueByKey.get(key);
	}

	public boolean isValid() {
		return getId() != TracTicket.INVALID_ID;
	}

	private Date parseTracDate(int seconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone(ITracRepository.TIME_ZONE));
		c.setTimeInMillis(seconds * 1000l);
		return c.getTime();
	}

	public void putBuiltinValue(Key key, String value) throws InvalidTicketException {
		valueByKey.put(key, value);
	}

	public void putCustomValue(String key, String value) {
		if (customValueByKey == null) {
			customValueByKey = new HashMap<String, String>();
		}
		customValueByKey.put(key, value);
	}

	/**
	 * Stores a value as it is retrieved from the repository.
	 * 
	 * @throws InvalidTicketException
	 *             thrown if the type of <code>value</code> is not valid
	 */
	public void putTracValue(String keyName, String value) throws InvalidTicketException {
		Key key = Key.fromKey(keyName);
		if (key != null) {
			if (key == Key.ID) {
				throw new RuntimeException("The ID field must be accessed through setId()");
			}
			putBuiltinValue(key, value);
		} else if (value instanceof String) {
			putCustomValue(keyName, (String) value);
		} else {
			throw new InvalidTicketException("Expected string value for custom key '" + keyName + "', got '" + value
					+ "'");
		}
	}

	public void setCreated(int created) {
		this.created = parseTracDate(created);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastChanged(int lastChanged) {
		this.lastChanged = parseTracDate(lastChanged);
	}

}
