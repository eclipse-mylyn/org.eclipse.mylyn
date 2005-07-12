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
package org.eclipse.mylar.bugzilla.core;

import java.io.Serializable;
import java.util.List;


/**
 * Interface representing a Bugzilla bug report.
 */
public interface IBugzillaBug extends Serializable {
	
	/**
	 * @return bug's id.
	 */
	public int getId();

	/**
	 * @return the server for this bug.
	 */
	public String getServer();
	
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
	 * @param newDescription
	 */
	public void setDescription(String newDescription);
	
	/**
	 * @return bug's summary.
	 */
	public String getSummary();
	
	/**
	 * Sets the bug's summary.
	 * @param newSummary
	 */
	public void setSummary(String newSummary);
	
	/**
	 * Get an attribute given its key 
	 * @return The value of the attribute or <code>null</code> if not present
	 */
	public Attribute getAttribute(String key);

	/**
	 * @return the attributes for this bug.
	 */
	public List<Attribute> getAttributes();
	
	/**
	 * @return <code>true</code> if this bug report is saved offline.
	 */
	public boolean isSavedOffline();
	
	/**
	 * @return <code>true</code> if this bug was created locally, and does not
	 *         yet exist on a bugzilla server.
	 */
	public boolean isLocallyCreated();
	
	/**
	 * Sets whether or not this bug is saved offline.
	 * @param newOfflineState <code>true</code> if this bug is saved offline
	 */
	public void setOfflineState(boolean newOfflineState);
}
