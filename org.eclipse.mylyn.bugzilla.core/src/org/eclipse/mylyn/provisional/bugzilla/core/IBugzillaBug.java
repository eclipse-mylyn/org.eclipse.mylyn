/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.provisional.bugzilla.core;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface representing a Bugzilla bug report.
 */
public interface IBugzillaBug { // extends Serializable

	// /**
	// * @return bug's id.
	// */
	// public int getId();
	//
	// /**
	// * @return the server for this bug.
	// */
	// public String getRepositoryUrl();

	/**
	 * @return the title label for this bug.
	 */
	public String getLabel();

	/**
	 * @return bug's description.
	 */
	public String getDescription();

	/**
	 * Sets the bug's description.
	 * 
	 * @param newDescription
	 */
	public void setDescription(String newDescription);

	/**
	 * @return bug's summary.
	 */
	public String getSummary();

	/**
	 * Sets the bug's summary.
	 * 
	 * @param newSummary
	 */
	public void setSummary(String newSummary);

	// /**
	// * Get an attribute given its key
	// *
	// * @return The value of the attribute or <code>null</code> if not present
	// */
	// public AbstractRepositoryReportAttribute getAttribute(String key);
	//
	// /**
	// * @return the attributes for this bug.
	// */
	// public List<AbstractRepositoryReportAttribute> getAttributes();

	// /**
	// * @return <code>true</code> if this bug report is saved offline.
	// */
	// public boolean isSavedOffline();

	// /**
	// * @return <code>true</code> if this bug was created locally, and does not
	// * yet exist on a bugzilla server.
	// */
	// public boolean isLocallyCreated();

	// /**
	// * Sets whether or not this bug is saved offline.
	// *
	// * @param newOfflineState
	// * <code>true</code> if this bug is saved offline
	// */
	// public void setOfflineState(boolean newOfflineState);

	// public boolean hasChanges();

	public void addCC(String email);

	public void addOperation(Operation o);

	public String getAssignedTo();

	public List<String> getCC();

	public List<String> getKeywords();

	public String getNewComment();

	// public String getNewNewComment();
	public void setNewComment(String newComment);

	// public void setNewNewComment(String newNewComment);
	public void setOfflineState(boolean newOfflineState);

	//public Operation getOperation(String displayText);

	public List<Operation> getOperations();

	public String getReporter();

	public String getResolution();

	public void setSelectedOperation(Operation o);

	public Operation getSelectedOperation();

	public String getStatus();

	public boolean isResolved();

	public void removeCC(String email);

	// public void setCreated(Date created);
	public void setKeywords(List<String> keywords);

	// public void setLastModified(Date date);
	public String getProduct();

	/**
	 * Get the date that the bug was created
	 * 
	 * @return The bugs creation date
	 */
	public Date getCreated();

	/**
	 * Returns a Date based on the given time zone
	 * null is allowed and will return date interpreted
	 * as local default time zone.
	 */
	public Date getLastModified(TimeZone timeZone);
}
