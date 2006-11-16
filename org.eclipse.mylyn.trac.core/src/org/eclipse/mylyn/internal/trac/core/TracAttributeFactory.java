/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;

/**
 * Provides a mapping from Mylar task keys to Trac ticket keys. 
 *
 * @author Steffen Pingel
 */
public class TracAttributeFactory extends AbstractAttributeFactory {

	private static final long serialVersionUID = 5333211422546115138L;

	private static Map<String, Attribute> attributeByTracKey = new HashMap<String, Attribute>();

	private static Map<String, String> tracKeyByTaskKey = new HashMap<String, String>();
	
	public enum Attribute {
		CC(Key.CC, "CC:", RepositoryTaskAttribute.USER_CC, true, false), 
		CHANGE_TIME(Key.CHANGE_TIME, "Last Modification:", RepositoryTaskAttribute.DATE_MODIFIED, true, true),
		COMPONENT(Key.COMPONENT, "Component:", null),
		DESCRIPTION(Key.DESCRIPTION, "Description:", RepositoryTaskAttribute.DESCRIPTION, true, false),
		ID(Key.ID, "<used by search engine>", null, true),
		KEYWORDS(Key.KEYWORDS, "Keywords:", RepositoryTaskAttribute.KEYWORDS),
		MILESTONE(Key.MILESTONE, "Milestone:", null),
		OWNER(Key.OWNER, "Assigned to:", RepositoryTaskAttribute.USER_ASSIGNED, true, true),
		PRIORITY(Key.PRIORITY, "Priority:", null),
		REPORTER(Key.REPORTER, "Reporter:", RepositoryTaskAttribute.USER_REPORTER, true, true),
		RESOLUTION(Key.RESOLUTION, "Resolution:", RepositoryTaskAttribute.RESOLUTION, false, true),
		SEVERITY(Key.SEVERITY, "Severity:", null),
		STATUS(Key.STATUS, "Status:", RepositoryTaskAttribute.STATUS, false, true),
		SUMMARY(Key.SUMMARY, "Summary:", RepositoryTaskAttribute.SUMMARY, true),
		TIME(Key.TIME, "Created:", RepositoryTaskAttribute.DATE_CREATION, true, true), 
		TYPE(Key.TYPE, "Type:", null), 
		VERSION(Key.VERSION, "Version:", null);		
		
		private final boolean isHidden;
		
		private final boolean isReadOnly;
		
		private final String tracKey;
		
		private final String prettyName;

		private final String taskKey;

		Attribute(Key key, String prettyName, String taskKey, boolean hidden, boolean readonly) {		
			this.tracKey = key.getKey();
			this.taskKey = taskKey;
			this.prettyName = prettyName;
			this.isHidden = hidden;
			this.isReadOnly = readonly;
			
			attributeByTracKey.put(tracKey, this);
			if (taskKey != null) {
				tracKeyByTaskKey.put(taskKey, tracKey);
			}
		}

		Attribute(Key key, String prettyName, String taskKey, boolean hidden) {		
			this(key, prettyName, taskKey, hidden, false);
		}
		
		Attribute(Key key, String prettyName, String taskKey) {		
			this(key, prettyName, taskKey, false, false);
		}

		public String getTaskKey() {
			return taskKey;
		}

		public String getTracKey() {
			return tracKey;
		}

		public boolean isHidden() {
			return isHidden;
		}	
		
		public boolean isReadOnly() {
			return isReadOnly;
		}
		
		public String toString() {
			return prettyName;
		}
	}

	static {
		// make sure hash maps get initialized when class is loaded
		Attribute.values();
	}

	@Override
	public boolean getIsHidden(String key) {
		Attribute attribute = attributeByTracKey.get(key);
		return (attribute != null) ? attribute.isHidden() : false;
	}

	@Override
	public String getName(String key) {
		Attribute attribute = attributeByTracKey.get(key);
		// TODO if attribute == null it is probably a custom field: need 
		// to query custom field information from repoository
		return (attribute != null) ? attribute.toString() : key;
	}

	@Override
	public boolean isReadOnly(String key) {
		Attribute attribute = attributeByTracKey.get(key);
		return (attribute != null) ? attribute.isReadOnly() : false;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		String tracKey = tracKeyByTaskKey.get(key);
		return (tracKey != null) ? tracKey : key;
	}

}
