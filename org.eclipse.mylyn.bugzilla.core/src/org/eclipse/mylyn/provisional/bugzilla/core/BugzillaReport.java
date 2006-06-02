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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;


import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;

/**
 * A report entered in Bugzilla.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaReport extends AbstractRepositoryReport implements Serializable {

	private static final long serialVersionUID = -382103999768379139L;

	public static final String VAL_STATUS_VERIFIED = "VERIFIED";

	public static final String VAL_STATUS_CLOSED = "CLOSED";

	public static final String VAL_STATUS_RESOLVED = "RESOLVED";

	public static final String VAL_STATUS_NEW = "NEW";

	/** Parser for dates in the report */
	private static SimpleDateFormat delta_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static SimpleDateFormat creation_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/** Description of the bug */
	protected String description;

	/** Creation timestamp */
	protected Date created;

	/** Modification timestamp */
	protected Date lastModified = null;

	/** The bugs valid keywords */
	protected List<String> validKeywords;

	/** The operations that can be done on the report */
	protected List<Operation> operations = new ArrayList<Operation>();

	private static final AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	/** The value for the new comment to add (text that is saved) */
	protected String newComment = "";

	/** The operation that was selected to do to the bug */
	protected Operation selectedOperation = null;

	/** Whether or not this bug report is saved offline. */
	protected boolean savedOffline = false;

	protected boolean hasChanges = false;

	protected String charset = null;

	public BugzillaReport(int id, String repositoryURL) {
		super(id, repositoryURL);
	}

	public void addCC(String email) {
		addAttributeValue(BugzillaReportElement.CC, email);
	}

	/**
	 * Add an operation to the bug
	 * 
	 * @param o
	 *            The operation to add
	 */
	public void addOperation(Operation o) {
		operations.add(o);
	}

	/**
	 * Get the person to whom this bug is assigned
	 * 
	 * @return The person who is assigned to this bug
	 */
	public String getAssignedTo() {
		return getAttributeValue(BugzillaReportElement.ASSIGNED_TO);
	}

	/**
	 * Get the set of addresses in the CC list
	 * 
	 * @return A <code>Set</code> of addresses in the CC list
	 */
	public List<String> getCC() {
		return getAttributeValues(BugzillaReportElement.CC);
	}

	/**
	 * Get the date that the bug was created
	 * 
	 * @return The bugs creation date
	 */
	public Date getCreated() {
		if (created == null) {
			String dateString = getAttributeValue(BugzillaReportElement.CREATION_TS);
			try {
				created = creation_ts_format.parse(dateString);
			} catch (ParseException e) {
				// ignore
			}
		}
		return created;
	}

	/**
	 * Get the bugs description
	 * 
	 * @return The description of the bug
	 */
	public String getDescription() {
		List<Comment> coms = this.getComments();
		if (coms != null && coms.size() > 0) {
			return coms.get(0).getText();
		} else {
			return "";
		}

	}
	
	/**
	 * Get the keywords for the bug
	 * 
	 * @return The keywords for the bug
	 */
	public List<String> getKeywords() {

		// get the selected keywords for the bug
		StringTokenizer st = new StringTokenizer(getAttributeValue(BugzillaReportElement.KEYWORDS), ",", false);
		List<String> keywords = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			keywords.add(s);
		}

		return keywords;
	}

	public String getLabel() {
		return getId() + ": " + getAttributeValue(BugzillaReportElement.SHORT_DESC);
	}

	public Date getLastModified(TimeZone timeZone) {
		if (lastModified == null) {			
			String dateString = getAttributeValue(BugzillaReportElement.DELTA_TS);
			try {
				if(timeZone != null) {
					delta_ts_format.setTimeZone(timeZone);
				} else {
					delta_ts_format.setTimeZone(TimeZone.getDefault());
				}
				lastModified = delta_ts_format.parse(dateString);
			} catch (ParseException e) {
				// ignore
			}
		}
		return lastModified;
	}

	/**
	 * Get the new comment that is to be added to the bug
	 * 
	 * @return The new comment
	 */
	public String getNewComment() {
		return newComment;
	}

	/**
	 * Get an operation from the bug based on its display name
	 * 
	 * @param displayText
	 *            The display text for the operation
	 * @return The operation that has the display text
	 */
	public Operation getOperation(String displayText) {
		Iterator<Operation> itr = operations.iterator();
		while (itr.hasNext()) {
			Operation o = itr.next();
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			if (opName.equals(displayText))
				return o;
		}
		return null;
	}

	/**
	 * Get all of the operations that can be done to the bug
	 * 
	 * @return The operations that can be done to the bug
	 */
	public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * Get the person who reported the bug
	 * 
	 * @return The person who reported the bug
	 */
	public String getReporter() {
		return getAttributeValue(BugzillaReportElement.REPORTER);
	}

	/**
	 * Get the resolution of the bug
	 * 
	 * @return The resolution of the bug
	 */
	public String getResolution() {
		return getAttributeValue(BugzillaReportElement.RESOLUTION);
	}

	/**
	 * Get the status of the bug
	 * 
	 * @return The bugs status
	 */
	public String getStatus() {
		return getAttributeValue(BugzillaReportElement.BUG_STATUS);
	}

	/**
	 * Get the summary for the bug
	 * 
	 * @return The bugs summary
	 */
	public String getSummary() {
		return getAttributeValue(BugzillaReportElement.SHORT_DESC);
	}

	public void setSummary(String summary) {
		setAttributeValue(BugzillaReportElement.SHORT_DESC, summary);
	}

	public String getProduct() {
		return getAttributeValue(BugzillaReportElement.PRODUCT);
	}
	
	public boolean isLocallyCreated() {
		return false;
	}

	public boolean isResolved() {
		AbstractRepositoryReportAttribute status = getAttribute(BugzillaReportElement.BUG_STATUS);
		return status != null && isResolvedStatus(status.getValue());
	}

	// TODO: move
	private boolean isResolvedStatus(String status) {
		if (status != null) {
			return status.equals(VAL_STATUS_RESOLVED) || status.equals(VAL_STATUS_CLOSED)
					|| status.equals(VAL_STATUS_VERIFIED);
		} else {
			return false;
		}
	}

	public boolean isSavedOffline() {
		return savedOffline;
	}

	/**
	 * Remove an address from the bugs CC list
	 * 
	 * @param email
	 *            the address to be removed from the CC list
	 * @return <code>true</code> if the email is in the set and it was removed
	 */
	public void removeCC(String email) {
		removeAttributeValue(BugzillaReportElement.CC, email);
	}

	public void setDescription(String description) {
		// ignore, used by NewBugReport
		// this.description = decodeStringFromCharset(description);
	}

	public void setKeywords(List<String> keywords) {
		this.validKeywords = keywords;
	}

	/**
	 * Set the new comment that will be added to the bug
	 * 
	 * @param newComment
	 *            The new comment to add to the bug
	 */
	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public void setOfflineState(boolean newOfflineState) {
		savedOffline = newOfflineState;
	}

	public void setSelectedOperation(Operation o) {
		selectedOperation = o;
	}

	public Operation getSelectedOperation() {
		return selectedOperation;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}
}
