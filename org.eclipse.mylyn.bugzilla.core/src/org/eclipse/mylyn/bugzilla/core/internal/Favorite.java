/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchResultCollector;


/**
 * Class representing an item in the favorites view
 */
public class Favorite implements Serializable {

	/** Automatically generated serialVersionUID */
	private static final long serialVersionUID = 3258129158977632310L;

	/** Bug id */
	private int id;
	/** Bug description */
	private String description;
	/** Query that created the match */
	private String query;
	/** Bug's attributes (severity, priority, etc.) */
	private Map<String, Object> attributes;
	/** Date when the favorite was recommended. */
	private Date date;
    
    private String server;
	
	/**
	 * Constructor.
	 * 
	 * @param bug
	 *            The bug this favorite represents.
	 */
	public Favorite(BugReport bug) {
		this(bug.getRepository(), bug.getId(), bug.getSummary(), "", BugzillaSearchResultCollector.getAttributeMap(bug));
	}
	
	/**
	 * Constructor.
	 */
	public Favorite(String server, int id, String description, String query, Map<String, Object> attributes) {
		this.server = server;
        this.id = id;
		this.description = description;
		this.query = query;
		this.attributes = attributes;
		date = new Date();
	}

    /**
     * returns the server for the bug
     */
    public String getServer(){
        return server;
    }
    
	/**
	 * Returns bug's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns bug's description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns bug attributes.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * Returns the query that created the match.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Returns date when the favorite was added to the view.
	 */
	public Date getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return id + " - " + description;
	}
}
