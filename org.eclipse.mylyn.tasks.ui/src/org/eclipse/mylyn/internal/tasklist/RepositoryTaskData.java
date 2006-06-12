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

package org.eclipse.mylar.internal.tasklist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositoryTaskData extends AttributeContainer implements Serializable {

	private int reportID;

	private String repositoryURL;

	protected String newComment = "";
		
	/** The full path to the attachment to submit with this report, empty if none */
	protected LocalAttachment newAttachment = null;
	
	private List<Comment> comments = new ArrayList<Comment>();

	private List<RepositoryAttachment> attachments = new ArrayList<RepositoryAttachment>();

	private boolean hasChanges = false;

	private static final long serialVersionUID = 310066248657960823L;

	public static final String VAL_STATUS_VERIFIED = "VERIFIED";

	public static final String VAL_STATUS_CLOSED = "CLOSED";

	public static final String VAL_STATUS_RESOLVED = "RESOLVED";

	public static final String VAL_STATUS_NEW = "NEW";

	/** The operation that was selected to do to the bug */
	protected RepositoryOperation selectedOperation = null;

	/** Whether or not this bug report is saved offline. */
	protected boolean savedOffline = false;

	protected String charset = null;

	/** The repositoryOperations that can be done on the report */
	protected List<RepositoryOperation> repositoryOperations = new ArrayList<RepositoryOperation>();

	// private static final RepositoryTaskAttributeFactory attributeFactory =
	// new BugzillaAttributeFactory();

	/** Parser for dates in the report */
	private static SimpleDateFormat delta_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat creation_ts_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/** The bugs valid keywords */
	protected List<String> validKeywords;

	/** Description of the bug */
	protected String description;

	/** Creation timestamp */
	protected Date created;

	/** Modification timestamp */
	protected Date lastModified = null;

	protected String repositoryKind;

	public RepositoryTaskData(AbstractAttributeFactory factory, String repositoryKind, String repositoryURL, int id) {
		super(factory);
		this.reportID = id;
		this.repositoryKind = repositoryKind;
		this.repositoryURL = repositoryURL;
	}

	/**
	 * TODO: move?
	 */
	public static boolean isResolvedStatus(String status) {
		if (status != null) {
			return status.equals(VAL_STATUS_RESOLVED) || status.equals(VAL_STATUS_CLOSED)
					|| status.equals(VAL_STATUS_VERIFIED);
		} else {
			return false;
		}
	}

	// public void addCC(String email) {
	// addAttributeValue(RepositoryTaskAttribute.KEY_CC, email);
	// addAttributeValue(BugzillaReportElement.CC.getKeyString(), email);
	// }

	public String getLabel() {
		return getSummary();
		// return getId() + ": " +
		// getAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString());
	}

	/**
	 * Get the resolution of the bug
	 * 
	 * @return The resolution of the bug
	 */
	public String getResolution() {
		return getAttributeValue(RepositoryTaskAttribute.RESOLUTION);
		// return
		// getAttributeValue(BugzillaReportElement.RESOLUTION.getKeyString());
	}

	/**
	 * Get the status of the bug
	 * 
	 * @return The bugs status
	 */
	public String getStatus() {
		return getAttributeValue(RepositoryTaskAttribute.STATUS);
		// return
		// getAttributeValue(BugzillaReportElement.BUG_STATUS.getKeyString());
	}

	// XXX: fix to not parse
	public Date getLastModified(String timeZoneID) {		
		if (lastModified == null) {
			String dateString = getAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED);
			// String dateString =
			// getAttributeValue(BugzillaReportElement.DELTA_TS.getKeyString());
			try {
				TimeZone timeZone  = TimeZone.getTimeZone(timeZoneID);
				if (timeZone != null) {
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

	public void setDescription(String description) {
		// ignore, used by NewBugReport
		// this.description = decodeStringFromCharset(description);
	}

	// public void setKeywords(List<String> keywords) {
	// this.validKeywords = keywords;
	// }

	// public void setOfflineState(boolean newOfflineState) {
	// savedOffline = newOfflineState;
	// }

	public void setSelectedOperation(RepositoryOperation o) {
		selectedOperation = o;
	}

	public RepositoryOperation getSelectedOperation() {
		return selectedOperation;
	}

	// @Override
	// public RepositoryTaskAttributeFactory getAttributeFactory() {
	// return attributeFactory;
	// }

	/**
	 * Get all of the repositoryOperations that can be done to the bug
	 * 
	 * @return The repositoryOperations that can be done to the bug
	 */
	public List<RepositoryOperation> getOperations() {
		return repositoryOperations;
	}

	/**
	 * Get the person who reported the bug
	 * 
	 * @return The person who reported the bug
	 */
	public String getReporter() {
		return getAttributeValue(RepositoryTaskAttribute.USER_REPORTER);
		// return
		// getAttributeValue(BugzillaReportElement.REPORTER.getKeyString());
	}

	/**
	 * Get an operation from the bug based on its display name
	 * 
	 * @param displayText
	 *            The display text for the operation
	 * @return The operation that has the display text
	 */
	public RepositoryOperation getOperation(String displayText) {
		Iterator<RepositoryOperation> itr = repositoryOperations.iterator();
		while (itr.hasNext()) {
			RepositoryOperation o = itr.next();
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			if (opName.equals(displayText))
				return o;
		}
		return null;
	}

	/**
	 * Get the summary for the bug
	 * 
	 * @return The bugs summary
	 */
	public String getSummary() {
		return getAttributeValue(RepositoryTaskAttribute.SUMMARY);
		// return
		// getAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString());
	}

	public void setSummary(String summary) {
		throw new NullPointerException("not impelmented");
		// setAttributeValue(RepositoryTaskAttribute.SHORT_DESC, summary);
		// setAttributeValue(BugzillaReportElement.SHORT_DESC.getKeyString(),
		// summary);
	}

	public String getProduct() {
		return getAttributeValue(RepositoryTaskAttribute.PRODUCT);
		// return
		// getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
	}

	public boolean isLocallyCreated() {
		return false;
	}

	public boolean isResolved() {
		// RepositoryTaskAttribute status =
		// getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString());
		return isResolvedStatus(getStatus());
	}

	// public boolean isSavedOffline() {
	// return savedOffline;
	// }

	/**
	 * Get the date that the bug was created
	 * 
	 * @return The bugs creation date
	 */
	public Date getCreated() {
		if (created == null) {			
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(this.getRepositoryKind(), this.getRepositoryUrl());
			TimeZone timeZone = null;
			if(repository != null) {
				timeZone = TimeZone.getTimeZone(repository.getTimeZoneId());
			} else {
				timeZone = TimeZone.getDefault();
			}
			String dateString = getAttributeValue(RepositoryTaskAttribute.DATE_CREATION);
			// String dateString =
			// getAttributeValue(BugzillaReportElement.CREATION_TS.getKeyString());
			try {
				creation_ts_format.setTimeZone(timeZone);
				created = creation_ts_format.parse(dateString);
			} catch (ParseException e) {
				// ignore
			}
		}
		return created;
	}

	/**
	 * Get the keywords for the bug
	 * 
	 * @return The keywords for the bug
	 */
	public List<String> getKeywords() {

		// get the selected keywords for the bug
		StringTokenizer st = new StringTokenizer(getAttributeValue(RepositoryTaskAttribute.KEYWORDS), ",", false);
		List<String> keywords = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			keywords.add(s);
		}

		return keywords;
	}

	/**
	 * Add an operation to the bug
	 * 
	 * @param o
	 *            The operation to add
	 */
	public void addOperation(RepositoryOperation o) {
		repositoryOperations.add(o);
	}

	public List<String> getCC() {
		return getAttributeValues(RepositoryTaskAttribute.USER_CC);
		// return getAttributeValues(BugzillaReportElement.CC.getKeyString());
	}

	public void removeCC(String email) {
		removeAttributeValue(RepositoryTaskAttribute.USER_CC, email);
		// removeAttributeValue(BugzillaReportElement.CC.getKeyString(), email);
	}

	public String getAssignedTo() {
		return getAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED);
		// return getAttributeValue(BugzillaReportElement.ASSIGNED_TO);
	}

	/**
	 * Get the new comment that is to be added to the bug
	 */
	public String getNewComment() {
		return newComment;
	}

	/**
	 * Set the new comment that will be added to the bug
	 */
	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public void addComment(Comment comment) {
		Comment preceding = null;
		if (comments.size() > 0) {
			// if there are some comments, get the last comment and set the next
			// value to be the new comment
			preceding = comments.get(comments.size() - 1);
			preceding.setNext(comment);
		}
		comment.setPrevious(preceding);
		comments.add(comment);
	}

	public List<Comment> getComments() {
		return comments;
	}

	public String getDescription() {
		List<Comment> coms = this.getComments();
		if (coms != null && coms.size() > 0) {
			return coms.get(0).getText();
		} else {
			return "";
		}
	}

	public void addAttachment(RepositoryAttachment attachment) {
		attachments.add(attachment);
	}

	public List<RepositoryAttachment> getAttachments() {
		return attachments;
	}

	public LocalAttachment getNewAttachment() {
		return newAttachment;
	}

	public void setNewAttachment(LocalAttachment newAttachment) {
		this.newAttachment = newAttachment;
	}
	
	public int getId() {
		return reportID;
	}

	/**
	 * @return the server for this report
	 */
	public String getRepositoryUrl() {
		return repositoryURL;
	}

	public boolean hasChanges() {
		return hasChanges;
	}

	public void setHasChanged(boolean b) {
		hasChanges = b;
	}

	public List<String> getAttributeValues(String key) {
		RepositoryTaskAttribute attribute = getAttribute(key);
		if (attribute != null) {
			return attribute.getValues();
		}
		return new ArrayList<String>();
	}

	public void removeAttributeValue(String key, String value) {
		RepositoryTaskAttribute attrib = getAttribute(key);
		if (attrib != null) {
			attrib.removeValue(value);
		}
	}

	public String getRepositoryKind() {
		return repositoryKind;
	}

	@Override
	public void setAttributeFactory(AbstractAttributeFactory factory) {
		super.setAttributeFactory(factory);
		for (Comment comment : comments) {
			comment.setAttributeFactory(factory);
		}
		for (RepositoryAttachment attachment : attachments) {
			attachment.setAttributeFactory(factory);
		}
	}

	// public abstract RepositoryTaskAttributeFactory getAttributeFactory();
}
