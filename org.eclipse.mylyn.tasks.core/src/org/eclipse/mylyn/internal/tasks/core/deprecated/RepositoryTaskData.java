/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public final class RepositoryTaskData extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = 2304501248225237699L;

	private boolean isNew = false;

	private final String reportID;

	private String repositoryURL;

	private final String repositoryKind;

	private String taskKind;

	private final List<TaskComment> taskComments = new ArrayList<TaskComment>();

	private final List<RepositoryAttachment> attachments = new ArrayList<RepositoryAttachment>();

	/** The operation that was selected to do to the bug */
	private RepositoryOperation selectedOperation = null;

	/** The repositoryOperations that can be done on the report */
	private final List<RepositoryOperation> repositoryOperations = new ArrayList<RepositoryOperation>();

	private boolean partial;

	public RepositoryTaskData(AbstractAttributeFactory factory, String repositoryKind, String repositoryURL, String id) {
		this(factory, repositoryKind, repositoryURL, id, AbstractTask.DEFAULT_TASK_KIND);
	}

	public RepositoryTaskData(AbstractAttributeFactory factory, String repositoryKind, String repositoryURL, String id,
			String taskKind) {
		super(factory);
		this.reportID = id;
		this.repositoryKind = repositoryKind;
		this.repositoryURL = repositoryURL;
		this.taskKind = taskKind;
	}

	public String getLabel() {
		if (isNew()) {
			return "<unsubmitted> " + this.getRepositoryUrl();
		} else {
			return getSummary();
		}
	}

	/**
	 * Get the resolution of the bug
	 * 
	 * @return The resolution of the bug
	 */
	public String getResolution() {
		return getAttributeValue(RepositoryTaskAttribute.RESOLUTION);
	}

	/**
	 * Get the status of the bug
	 * 
	 * @return The bugs status
	 */
	public String getStatus() {
		return getAttributeValue(RepositoryTaskAttribute.STATUS);
	}

	public String getLastModified() {
		return getAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED);
	}

	public void setSelectedOperation(RepositoryOperation o) {
		selectedOperation = o;
	}

	public RepositoryOperation getSelectedOperation() {
		return selectedOperation;
	}

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
			if (opName.equals(displayText)) {
				return o;
			}
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
	}

	public void setSummary(String summary) {
		setAttributeValue(RepositoryTaskAttribute.SUMMARY, summary);
	}

	public String getProduct() {
		return getAttributeValue(RepositoryTaskAttribute.PRODUCT);
	}

	/**
	 * true if this is a new, unsubmitted task false otherwise
	 */
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Get the date that the bug was created
	 * 
	 * @return The bugs creation date
	 */
	public String getCreated() {
		return getAttributeValue(RepositoryTaskAttribute.DATE_CREATION);
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

	public List<String> getCc() {
		return getAttributeValues(RepositoryTaskAttribute.USER_CC);
	}

	public void removeCc(String email) {
		removeAttributeValue(RepositoryTaskAttribute.USER_CC, email);
	}

	public String getAssignedTo() {
		return getAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED);
	}

	/**
	 * Get the new comment that is to be added to the bug
	 */
	public String getNewComment() {
		RepositoryTaskAttribute attribute = getAttribute(RepositoryTaskAttribute.COMMENT_NEW);
		return (attribute != null) ? attribute.getValue() : "";
	}

	/**
	 * Set the new comment that will be added to the bug
	 */
	public void setNewComment(String newComment) {
		setAttributeValue(RepositoryTaskAttribute.COMMENT_NEW, newComment);
	}

	public void addComment(TaskComment taskComment) {
		taskComments.add(taskComment);
	}

	public List<TaskComment> getComments() {
		return taskComments;
	}

	public void setDescription(String description) {
		setAttributeValue(RepositoryTaskAttribute.DESCRIPTION, description);
	}

	public String getDescription() {
		RepositoryTaskAttribute attribute = getDescriptionAttribute();
		return (attribute != null) ? attribute.getValue() : "";
	}

	public RepositoryTaskAttribute getDescriptionAttribute() {
		RepositoryTaskAttribute attribute = getAttribute(RepositoryTaskAttribute.DESCRIPTION);
		// TODO: Remove the following after 1.0 release as we now just have a
		// summary attribute
		if (attribute == null) {
			List<TaskComment> coms = this.getComments();
			if (coms != null && coms.size() > 0) {
				return coms.get(0).getAttribute(RepositoryTaskAttribute.COMMENT_TEXT);
			}
		}
		return attribute;
	}

	public void addAttachment(RepositoryAttachment attachment) {
		attachments.add(attachment);
		attachment.setTaskData(this);
	}

	public List<RepositoryAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * @deprecated Use {@link #getTaskId()} instead
	 */
	@Deprecated
	public String getId() {
		return getTaskId();
	}

	/**
	 * @since 3.0
	 */
	public String getTaskId() {
		return reportID;
	}

	public String getTaskKey() {
		RepositoryTaskAttribute attr = getAttribute(RepositoryTaskAttribute.TASK_KEY);
		if (attr != null) {
			return attr.getValue();
		}
		return getTaskId();
	}

	public void setTaskKey(String key) {
		setAttributeValue(RepositoryTaskAttribute.TASK_KEY, key);
	}

	/**
	 * @return the server for this report
	 */
	public String getRepositoryUrl() {
		return repositoryURL;
	}

	@Override
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

	/**
	 * @deprecated Use {@link #getConnectorKind()} instead
	 */
	@Deprecated
	public String getRepositoryKind() {
		return getConnectorKind();
	}

	/**
	 * @since 3.0
	 */
	public String getConnectorKind() {
		return repositoryKind;
	}

	@Override
	public void setAttributeFactory(AbstractAttributeFactory factory) {
		super.setAttributeFactory(factory);
		for (TaskComment taskComment : taskComments) {
			taskComment.setAttributeFactory(factory);
		}
		for (RepositoryAttachment attachment : attachments) {
			attachment.setAttributeFactory(factory);
		}
	}

	public String getTaskKind() {
		return taskKind;
	}

	/**
	 * @since 3.0
	 */
	public void setTaskKind(String taskKind) {
		this.taskKind = taskKind;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
		for (RepositoryAttachment attachment : attachments) {
			attachment.setRepositoryUrl(repositoryURL);
		}
	}

	public final String getHandleIdentifier() {
		return RepositoryTaskHandleUtil.getHandle(getRepositoryUrl(), getTaskId());
	}

	@Override
	public void addAttribute(String key, RepositoryTaskAttribute attribute) {
		super.addAttribute(key, attribute);
		attribute.setTaskData(this);
	}

	public void refresh() {
		setTaskData(this);
		for (AttributeContainer container : taskComments) {
			container.setTaskData(this);
		}
		for (AttributeContainer container : attachments) {
			container.setTaskData(this);
		}
	}

	/**
	 * @since 3.0
	 */
	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	/**
	 * @since 3.0
	 */
	public boolean isPartial() {
		return partial;
	}

}
