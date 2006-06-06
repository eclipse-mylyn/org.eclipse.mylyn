///*******************************************************************************
// * Copyright (c) 2003 - 2006 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     University Of British Columbia - initial API and implementation
// *******************************************************************************/
//package org.eclipse.mylar.internal.bugzilla.core;
//
//import java.io.Serializable;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.StringTokenizer;
//import java.util.TimeZone;
//
//import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTaskData;
//import org.eclipse.mylar.internal.tasklist.RepositoryOperation;
//import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
//import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttributeFactory;
//
///**
// * @author Mik Kersten
// * @author Rob Elves
// */
//public class BugzillaTaskData extends AbstractRepositoryTaskData implements Serializable {

//	private static final long serialVersionUID = 310066248657960823L;
//
//	public static final String VAL_STATUS_VERIFIED = "VERIFIED";
//
//	public static final String VAL_STATUS_CLOSED = "CLOSED";
//
//	public static final String VAL_STATUS_RESOLVED = "RESOLVED";
//
//	public static final String VAL_STATUS_NEW = "NEW";
//
//	/** Parser for dates in the report */
//	private static SimpleDateFormat delta_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	
//	private static SimpleDateFormat creation_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//
//	/** The bugs valid keywords */
//	protected List<String> validKeywords;
//
//	/** The repositoryOperations that can be done on the report */
//	protected List<RepositoryOperation> repositoryOperations = new ArrayList<RepositoryOperation>();
//
//	private static final RepositoryTaskAttributeFactory attributeFactory = new BugzillaAttributeFactory();
//
//	/** The operation that was selected to do to the bug */
//	protected RepositoryOperation selectedOperation = null;
//
//	/** Whether or not this bug report is saved offline. */
//	protected boolean savedOffline = false;
//
//	protected boolean hasChanges = false;
//
//	protected String charset = null;
//
//	public BugzillaTaskData(int id, String repositoryURL) {
//		super(id, repositoryURL);
//	}
//
//	/**
//	 * TODO: move?
//	 */
//	public static boolean isResolvedStatus(String status) {
//		if (status != null) {
//			return status.equals(VAL_STATUS_RESOLVED) || status.equals(VAL_STATUS_CLOSED)
//					|| status.equals(VAL_STATUS_VERIFIED);
//		} else {
//			return false;
//		}
//	}
//
//	public void addCC(String email) {
//		addAttributeValue(BugzillaReportElement.CC.getKeyString(), email);
//	}
//
//	/**
//	 * Add an operation to the bug
//	 * 
//	 * @param o
//	 *            The operation to add
//	 */
//	public void addOperation(RepositoryOperation o) {
//		repositoryOperations.add(o);
//	}
//
//	/**
//	 * Get the set of addresses in the CC list
//	 * 
//	 * @return A <code>Set</code> of addresses in the CC list
//	 */
//	public List<String> getCC() {
//		return getAttributeValues(BugzillaReportElement.CC.getKeyString());
//	}
//
//	/**
//	 * Get the date that the bug was created
//	 * 
//	 * @return The bugs creation date
//	 */
//	public Date getCreated() {
//		if (created == null) {
//			String dateString = getAttributeValue(BugzillaReportElement.CREATION_TS.getKeyString());
//			try {
//				created = creation_ts_format.parse(dateString);
//			} catch (ParseException e) {
//				// ignore
//			}
//		}
//		return created;
//	}
//
//	/**
//	 * Get the keywords for the bug
//	 * 
//	 * @return The keywords for the bug
//	 */
//	public List<String> getKeywords() {
//
//		// get the selected keywords for the bug
//		StringTokenizer st = new StringTokenizer(getAttributeValue(BugzillaReportElement.KEYWORDS.getKeyString()), ",", false);
//		List<String> keywords = new ArrayList<String>();
//		while (st.hasMoreTokens()) {
//			String s = st.nextToken().trim();
//			keywords.add(s);
//		}
//
//		return keywords;
//	}
//
//	public String getLabel() {
//		return getId() + ": " + getAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString());
//	}
//
//	public Date getLastModified(TimeZone timeZone) {
//		if (lastModified == null) {			
//			String dateString = getAttributeValue(BugzillaReportElement.DELTA_TS.getKeyString());
//			try {
//				if(timeZone != null) {
//					delta_ts_format.setTimeZone(timeZone);
//				} else {
//					delta_ts_format.setTimeZone(TimeZone.getDefault());
//				}
//				lastModified = delta_ts_format.parse(dateString);
//			} catch (ParseException e) {
//				// ignore
//			}
//		}
//		return lastModified;
//	}
//
//	/**
//	 * Get an operation from the bug based on its display name
//	 * 
//	 * @param displayText
//	 *            The display text for the operation
//	 * @return The operation that has the display text
//	 */
//	public RepositoryOperation getOperation(String displayText) {
//		Iterator<RepositoryOperation> itr = repositoryOperations.iterator();
//		while (itr.hasNext()) {
//			RepositoryOperation o = itr.next();
//			String opName = o.getOperationName();
//			opName = opName.replaceAll("</.*>", "");
//			opName = opName.replaceAll("<.*>", "");
//			if (opName.equals(displayText))
//				return o;
//		}
//		return null;
//	}
//
//	/**
//	 * Get all of the repositoryOperations that can be done to the bug
//	 * 
//	 * @return The repositoryOperations that can be done to the bug
//	 */
//	public List<RepositoryOperation> getOperations() {
//		return repositoryOperations;
//	}
//
//	/**
//	 * Get the person who reported the bug
//	 * 
//	 * @return The person who reported the bug
//	 */
//	public String getReporter() {
//		return getAttributeValue(BugzillaReportElement.REPORTER.getKeyString());
//	}
//
//	/**
//	 * Get the resolution of the bug
//	 * 
//	 * @return The resolution of the bug
//	 */
//	public String getResolution() {
//		return getAttributeValue(BugzillaReportElement.RESOLUTION.getKeyString());
//	}
//
//	/**
//	 * Get the status of the bug
//	 * 
//	 * @return The bugs status
//	 */
//	public String getStatus() {
//		return getAttributeValue(BugzillaReportElement.BUG_STATUS.getKeyString());
//	}
//
//	/**
//	 * Get the summary for the bug
//	 * 
//	 * @return The bugs summary
//	 */
//	public String getSummary() {
//		return getAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString());
//	}
//
//	public void setSummary(String summary) {
//		setAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString(), summary);
//	}
//
//	public String getProduct() {
//		return getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
//	}
//
//	public boolean isLocallyCreated() {
//		return false;
//	}
//
//	public boolean isResolved() {
//		RepositoryTaskAttribute status = getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString());
//		return status != null && isResolvedStatus(status.getValue());
//	}
//
//	public boolean isSavedOffline() {
//		return savedOffline;
//	}
//
//	/**
//	 * Remove an address from the bugs CC list
//	 * 
//	 * @param email
//	 *            the address to be removed from the CC list
//	 * @return <code>true</code> if the email is in the set and it was removed
//	 */
//	public void removeCC(String email) {
//		removeAttributeValue(BugzillaReportElement.CC.getKeyString(), email);
//	}
//	
//	public void setDescription(String description) {
//		// ignore, used by NewBugReport
//		// this.description = decodeStringFromCharset(description);
//	}
//
//	public void setKeywords(List<String> keywords) {
//		this.validKeywords = keywords;
//	}
//
//	public void setOfflineState(boolean newOfflineState) {
//		savedOffline = newOfflineState;
//	}
//
//	public void setSelectedOperation(RepositoryOperation o) {
//		selectedOperation = o;
//	}
//
//	public RepositoryOperation getSelectedOperation() {
//		return selectedOperation;
//	}
//
//	@Override
//	public RepositoryTaskAttributeFactory getAttributeFactory() {
//		return attributeFactory;
//	}
//}
