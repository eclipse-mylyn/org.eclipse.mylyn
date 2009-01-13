/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.trac.core.client.InvalidTicketException;

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
		CC("cc"), CHANGE_TIME("changetime"), COMPONENT("component"), DESCRIPTION("description"), ID("id"), KEYWORDS( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"keywords"), MILESTONE("milestone"), OWNER("owner"), PRIORITY("priority"), REPORTER("reporter"), RESOLUTION( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"resolution"), STATUS("status"), SEVERITY("severity"), SUMMARY("summary"), TIME("time"), TYPE("type"), VERSION( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				"version"); //$NON-NLS-1$

		public static Key fromKey(String name) {
			for (Key key : Key.values()) {
				if (key.getKey().equals(name)) {
					return key;
				}
			}
			return null;
		}

		private String key;

		Key(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return key;
		}

		public String getKey() {
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
	private final Map<Key, String> valueByKey = new HashMap<Key, String>();

	private List<TracComment> comments;

	private List<TracAttachment> attachments;

	private String[] actions;

	private String[] resolutions;

	public TracTicket() {
	}

	/**
	 * Constructs a Trac ticket.
	 * 
	 * @param id
	 *            the numeric Trac ticket id
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
		if (customValueByKey == null) {
			return null;
		}
		return customValueByKey.get(key);
	}

	public String getValue(Key key) {
		return valueByKey.get(key);
	}

	public Map<String, String> getValues() {
		Map<String, String> result = new HashMap<String, String>();
		for (Key key : valueByKey.keySet()) {
			result.put(key.getKey(), valueByKey.get(key));
		}
		if (customValueByKey != null) {
			result.putAll(customValueByKey);
		}
		return result;
	}

	public boolean isValid() {
		return getId() != TracTicket.INVALID_ID;
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
	public boolean putValue(String keyName, String value) throws InvalidTicketException {
		Key key = Key.fromKey(keyName);
		if (key != null) {
			if (key == Key.ID || key == Key.TIME || key == Key.CHANGE_TIME) {
				return false;
			}
			putBuiltinValue(key, value);
		} else if (value != null) {
			putCustomValue(keyName, value);
		} else {
			throw new InvalidTicketException("Expected string value for custom key '" + keyName + "', got '" + value //$NON-NLS-1$ //$NON-NLS-2$
					+ "'"); //$NON-NLS-1$
		}
		return true;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastChanged(Date lastChanged) {
		this.lastChanged = lastChanged;
	}

	public void addComment(TracComment comment) {
		if (comments == null) {
			comments = new ArrayList<TracComment>();
		}
		comments.add(comment);
	}

	public void addAttachment(TracAttachment attachment) {
		if (attachments == null) {
			attachments = new ArrayList<TracAttachment>();
		}
		attachments.add(attachment);
	}

	public TracComment[] getComments() {
		return (comments != null) ? comments.toArray(new TracComment[0]) : null;
	}

	public TracAttachment[] getAttachments() {
		return (attachments != null) ? attachments.toArray(new TracAttachment[0]) : null;
	}

	public void setActions(String[] actions) {
		this.actions = actions;
	}

	public String[] getActions() {
		return actions;
	}

	public void setResolutions(String[] resolutions) {
		this.resolutions = resolutions;
	}

	public String[] getResolutions() {
		return resolutions;
	}

}
