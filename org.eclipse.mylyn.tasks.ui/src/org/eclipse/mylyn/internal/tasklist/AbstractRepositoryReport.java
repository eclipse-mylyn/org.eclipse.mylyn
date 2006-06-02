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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryReport extends AttributeContainer implements Serializable {

	private int reportID;

	private String repositoryURL;

	private List<Comment> comments = new ArrayList<Comment>();

	private List<ReportAttachment> attachments = new ArrayList<ReportAttachment>();

	private boolean hasChanges = false;

	public AbstractRepositoryReport(int id, String repositoryURL) {
		super();
		this.reportID = id;
		this.repositoryURL = repositoryURL;
	}

	public void addComment(Comment comment) {
		Comment preceding = null;
		if (comments.size() > 0) {
			// if there are some comments, get the last comment and set the next
			// value to be the new comment
			preceding = comments.get(comments.size() - 1);
			preceding.setNext(comment);
		}
		// set the comments previous value to the preceeding one
		comment.setPrevious(preceding);

		// comment.setText(decodeStringFromCharset(comment.getText()));
		// add the comment to the comment list
		comments.add(comment);
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void addAttachment(ReportAttachment attachment) {
		attachments.add(attachment);
	}

	public List<ReportAttachment> getAttachments() {
		return attachments;
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
		return hasChanges ;
	}

	public void setHasChanged(boolean b) {
		hasChanges = b;
	}
	
	/**
	 * @return <code>true</code> if this report was created locally, and does not
	 *         yet exist on a server.
	 */
	public abstract boolean isLocallyCreated();
	
	/**
	 * @return <code>true</code> if this report is saved offline.
	 */
	public abstract boolean isSavedOffline();

	/**
	 * Sets whether or not this report is saved offline.
	 * 
	 * @param newOfflineState
	 *            <code>true</code> if this bug is saved offline
	 */
	public abstract void setOfflineState(boolean newOfflineState);

//	public String getAttributeValue(Object key) {
//		AbstractRepositoryReportAttribute attribute = getAttribute(key);
//		if(attribute != null) {
//			return attribute.getValue();
//		}
//		return "";
//	}
	
	public List<String> getAttributeValues(Object key) {
		AbstractRepositoryReportAttribute attribute = getAttribute(key);
		if(attribute != null) {
			return attribute.getValues();
		}
		return new ArrayList<String>();
	}

	/** 
	 * sets a value on an attribute, if attribute doesn't exist,
	 * appropriate attribute is created
	 */
	public void setAttributeValue(Object key, String value) {
		AbstractRepositoryReportAttribute attrib = getAttribute(key);
		if(attrib == null) {
			attrib = getAttributeFactory().createAttribute(key);
			this.addAttribute(key, attrib);
		}
		attrib.setValue(value);		
	}
	
	public void addAttributeValue(Object key, String value) {
		AbstractRepositoryReportAttribute attrib = getAttribute(key);
		if (attrib != null) {
			attrib.addValue(value);
		} else {
			attrib = getAttributeFactory().createAttribute(key);
			attrib.addValue(value);
			this.addAttribute(key, attrib);
		}
	}
	
	public void removeAttributeValue(Object key, String value) {
		AbstractRepositoryReportAttribute attrib = getAttribute(key);
		if (attrib != null) {
			attrib.removeValue(value);
		} 
	}

	public abstract AbstractAttributeFactory getAttributeFactory();
}
