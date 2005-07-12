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
package org.eclipse.mylar.bugzilla.core.search;



/**
 * An item in the Bugzilla database matching the search criteria.
 */
public class BugzillaSearchHit 
{
	/** The server that the result was from */
	private String server;
	
	/** The bug id */
	private int id;
	
	/** The description of the bug */
	private String description;
	
	/** The severity of the bug */
	private String severity;
	
	/** The priority of the bug */
	private String priority;
	
	/** The platform that the bug was found in */
	private String platform;
	
	/** The state of the bug */
	private String state;
	
	/** The resolution of the bug */
	private String result;
	
	/** The owner of the bug */
	private String owner;
	
	/** The query that the bug was a result of */
	private String query;

	/**
	 * Constructor
	 * @param id The id of the bug
	 * @param description The description of the bug
	 * @param severity The severity of the bug
	 * @param priority The priority of the bug
	 * @param platform The platform the bug was found in
	 * @param state The state of the bug
	 * @param result The resolution of the bug
	 * @param owner The owner of the bug
	 * @param query the query that the bug was a result of
	 */
	public BugzillaSearchHit(int id, String description, String severity, String priority, String platform, String state, String result, String owner, String query, String server) 
	{
		this.id = id;
		this.description = description;
		this.severity = severity;
		this.priority = priority;
		this.platform = platform;
		this.state = state;
		this.result = result;
		this.owner = owner;
		this.query = query;
		this.server = server;
	}
	
	/**
	 * Get the bugs server
	 * @return The server the bug resides on
	 */
	public String getServer() 
	{
		return server;
	}
	
	/**
	 * Get the bugs id
	 * @return The bugs id
	 */
	public int getId() 
	{
		return id;
	}

	/**
	 * Get the bugs description
	 * @return The description of the bug
	 */
	public String getDescription() 
	{
		return description;
	}

	/**
	 * Get the bugs priority
	 * @return The priority of the bug
	 */
	public String getPriority() 
	{
		return priority;
	}

	/**
	 * Get the bugs severity
	 * @return The severity of the bug
	 */
	public String getSeverity() 
	{
		return severity;
	}

	/**
	 * Get the platform the bug occurred under
	 * @return The platform that the bug occured under
	 */
	public String getPlatform() 
	{
		return platform;
	}

	/**
	 * Get the bugs state
	 * @return The state of the bug
	 */
	public String getState() 
	{
		return state;
	}

	/**
	 * Get the bugs resolution
	 * @return The resolution of the bug
	 */
	public String getResult() 
	{
		return result;
	}

	/**
	 * Get the bugs owner
	 * @return The owner of the bug
	 */
	public String getOwner() 
	{
		return owner;
	}
	
	/**
	 * Get the query that the bug was a result of
	 * @return The query that the bug was a result of
	 */
	public String getQuery() 
	{
		return query;
	}
	
	@Override
	public String toString()
	{
		return id + " " + description + "\n";
	}
}
