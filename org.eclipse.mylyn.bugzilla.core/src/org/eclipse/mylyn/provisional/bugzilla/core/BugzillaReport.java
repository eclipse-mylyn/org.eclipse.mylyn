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

import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;

/**
 * A report entered in Bugzilla.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaReport extends AbstractRepositoryReport implements IBugzillaBug, Serializable {

	private static final long serialVersionUID = 310066248657960823L;

	public static final String VAL_STATUS_VERIFIED = "VERIFIED";

	public static final String VAL_STATUS_CLOSED = "CLOSED";

	public static final String VAL_STATUS_RESOLVED = "RESOLVED";

	public static final String VAL_STATUS_NEW = "NEW";

	/** Parser for dates in the report */
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

	// /** Bug attributes (status, resolution, etc.) */
	// protected HashMap<String, AbstractRepositoryReportAttribute> attributes =
	// new HashMap<String, AbstractRepositoryReportAttribute>();
	//
	//
	// /** The keys for the bug attributes */
	// protected ArrayList<String> attributeKeys = new ArrayList<String>();

	// /** A list of comments */
	// protected ArrayList<Comment> comments = new ArrayList<Comment>();

	/** The value for the new comment to add (text that is saved) */
	protected String newComment = "";

	// /** The new value for the new comment to add (text from submit editor) */
	// protected String newNewComment = "";

	/** The operation that was selected to do to the bug */
	protected Operation selectedOperation = null;

	/** Whether or not this bug report is saved offline. */
	protected boolean savedOffline = false;

	protected boolean hasChanges = false;

	protected String charset = null;

	// /**
	// * Get the bugs id
	// *
	// * @return The bugs id
	// */
	// public int getId() {
	// return id;
	// }
	//
	// public String getRepositoryUrl() {
	// return repositoryUrl;
	// }

	public BugzillaReport(int id, String repositoryURL) {
		super(id, repositoryURL);

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

	// public AbstractRepositoryReportAttribute
	// getAttributeForKnobName(BugzillaReportElement element) {
	// return super.getAttribute(element.getKeyString());
	// }

	// private String decodeStringFromCharset(String string) {
	// String decoded = string;
	// if (charset != null && string != null &&
	// Charset.availableCharsets().containsKey(charset)) {
	// try {
	// decoded = new String(string.getBytes(), charset);
	// } catch (UnsupportedEncodingException e) {
	// // ignore
	// }
	// }
	// return decoded;
	// }

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
				created = df.parse(dateString);
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

	// public AbstractRepositoryReportAttribute getAttribute(String key) {
	// return attributes.get(key);
	// }
	//
	// /**
	// * Get the list of attributes for this bug
	// *
	// * @return An <code>ArrayList</code> of the bugs attributes
	// */
	// public List<AbstractRepositoryReportAttribute> getAttributes() {
	// // create an array list to store the attributes in
	// ArrayList<AbstractRepositoryReportAttribute> attributeEntries = new
	// ArrayList<AbstractRepositoryReportAttribute>(attributeKeys.size());
	//
	// // go through each of the attribute keys
	// for (Iterator<String> it = attributeKeys.iterator(); it.hasNext();) {
	// // get the key for the attribute
	// String key = it.next();
	//
	// // get the attribute and add it to the list
	// AbstractRepositoryReportAttribute attribute = attributes.get(key);
	// attributeEntries.add(attribute);
	// }
	//
	// // return the list of attributes for the bug
	// return attributeEntries;
	// }

	// public AbstractRepositoryReportAttribute getAttributeForKnobName(String
	// knobName) {
	// for (Iterator<String> it = attributeKeys.iterator(); it.hasNext();) {
	// String key = it.next();
	//
	// AbstractRepositoryReportAttribute attribute = attributes.get(key);
	// if (attribute != null && attribute.getID() != null
	// && attribute.getID().compareTo(knobName) == 0) {
	// return attribute;
	// }
	// }
	//
	// return null;
	// }

	// /**
	// * @param attribute
	// * The attribute to add to the bug
	// */
	// public void addAttribute(AbstractRepositoryReportAttribute attribute) {
	// if (!attributes.containsKey(attribute.getName())) {
	// attributeKeys.add(attribute.getName());
	// }
	//
	// attribute.setValue(decodeStringFromCharset(attribute.getValue()));
	//
	// // put the value of the attribute into the map, using its name as the
	// // key
	// attributes.put(attribute.getName(), attribute);
	// }

	// /**
	// * Get the comments posted on the bug
	// *
	// * @return A list of comments for the bug
	// */
	// public ArrayList<Comment> getComments() {
	// return comments;
	// }

	// /**
	// * Add a comment to the bug
	// *
	// * @param comment
	// * The comment to add to the bug
	// */
	// public void addComment(Comment comment) {
	// Comment preceding = null;
	// if (comments.size() > 0) {
	// // if there are some comments, get the last comment and set the next
	// // value to be the new comment
	// preceding = comments.get(comments.size() - 1);
	// preceding.setNext(comment);
	// }
	// // set the comments previous value to the preceeding one
	// comment.setPrevious(preceding);
	//
	// comment.setText(decodeStringFromCharset(comment.getText()));
	// // add the comment to the comment list
	// comments.add(comment);
	// }

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
		//		
		// String[] keywords =
		// getAttributeValue(BugzillaReportElement.KEYWORDS).split(",");
		// return getAttributeValue(BugzillaReportElement.KEYWORDS)
		// BugzillaPlugin.getDefault().getProductConfiguration(repository).getProducts()
		//		
		// return
		// BugzillaRepositoryUtil.getValidKeywords(this.getRepositoryUrl());
	}

	public String getLabel() {
		return getId() + ": " + getAttributeValue(BugzillaReportElement.SHORT_DESC);
	}

	public Date getLastModified() {
		if (lastModified == null) {
			String dateString = getAttributeValue(BugzillaReportElement.DELTA_TS);
			try {
				lastModified = df.parse(dateString);
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

	// /**
	// * @return the new value of the new NewComment.
	// */
	// public String getNewNewComment() {
	// return newNewComment;
	// }

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

	// public void setSummary(String newSummary) {
	// setAttributeValue(BugzillaReportElement.SHORT_DESC, newSummary);
	// }

	public boolean isLocallyCreated() {
		return false;
	}

	public boolean isResolved() {
		AbstractRepositoryReportAttribute status = getAttribute(BugzillaReportElement.BUG_STATUS);
		return status != null && isResolvedStatus(status.getValue());
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

	// /**
	// * Set the bugs creation date
	// *
	// * @param created
	// * The date the the bug was created
	// */
	// public void setCreated(Date created) {
	// this.created = created;
	// }

	public void setDescription(String description) {
		// ignore, used by NewBugReport
		// this.description = decodeStringFromCharset(description);
	}

	public void setKeywords(List<String> keywords) {
		this.validKeywords = keywords;
	}

	// public void setLastModified(Date date) {
	// this.lastModified = date;
	// }

	/**
	 * Set the new comment that will be added to the bug
	 * 
	 * @param newComment
	 *            The new comment to add to the bug
	 */
	public void setNewComment(String newComment) {
		this.newComment = newComment;
		// newNewComment = newComment;
	}

	// /**
	// * Set the new value of the new NewComment
	// *
	// * @param newNewComment
	// * The new value of the new NewComment.
	// */
	// public void setNewNewComment(String newNewComment) {
	// this.newNewComment = newNewComment;
	// }

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

//	public AbstractRepositoryReportAttribute getAttribute(String test) {
//
//		MylarStatusHandler.fail(new Exception(), "BugReport: getAttribute called with string", false);
//		return new BugzillaReportAttribute(BugzillaReportElement.UNKNOWN);
//	}
}
