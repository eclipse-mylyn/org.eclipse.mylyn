/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.internal.tasks.core.AttributeMap;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * TaskInitialzationData should be used as alternative to implementing {@link ITaskMapping} when passing initialization
 * data into {@link AbstractTaskDataHandler#initializeTaskData()}. It provides common accessors and mutators for a set
 * of fields used during task data initialization. Only attributes of type {@link String} are supported, other accessors
 * throw {@link UnsupportedOperationException} as documented for each method.
 *
 * @author Benjamin Muskalla
 * @since 3.10
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TaskInitializationData implements ITaskMapping {

	private final AttributeMap attributesById = new AttributeMap();

	@Nullable
	public String getAttribute(String key) {
		return attributesById.getAttribute(key);
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public List<String> getCc() {
		return null;
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public Date getCompletionDate() {
		return null;
	}

	@Nullable
	public String getComponent() {
		return attributesById.getAttribute(TaskAttribute.COMPONENT);
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public Date getCreationDate() {
		return null;
	}

	@Nullable
	public String getDescription() {
		return attributesById.getAttribute(TaskAttribute.DESCRIPTION);
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public Date getDueDate() {
		return null;
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public List<String> getKeywords() {
		return null;
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public Date getModificationDate() {
		return null;
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public String getOwner() {
		return null;

	}

	/**
	 * Returns <code>null</code>.
	 *
	 * @since 3.15
	 */
	@Nullable
	public String getOwnerId() {
		return null;

	}

	@Nullable
	public String getPriority() {
		return attributesById.getAttribute(TaskAttribute.PRIORITY);
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public PriorityLevel getPriorityLevel() {
		return null;
	}

	@Nullable
	public String getProduct() {
		return attributesById.getAttribute(TaskAttribute.PRODUCT);
	}

	@Nullable
	public String getReporter() {
		return attributesById.getAttribute(TaskAttribute.USER_REPORTER);
	}

	@Nullable
	public String getResolution() {
		return attributesById.getAttribute(TaskAttribute.RESOLUTION);
	}

	@Nullable
	public String getSeverity() {
		return attributesById.getAttribute(TaskAttribute.SEVERITY);
	}

	@Nullable
	public String getStatus() {
		return attributesById.getAttribute(TaskAttribute.STATUS);
	}

	@Nullable
	public String getSummary() {
		return attributesById.getAttribute(TaskAttribute.SUMMARY);
	}

	/**
	 * Returns <code>null</code>.
	 */
	@Nullable
	public TaskData getTaskData() {
		return null;
	}

	@Nullable
	public String getTaskKey() {
		return attributesById.getAttribute(TaskAttribute.TASK_KEY);
	}

	@Nullable
	public String getTaskKind() {
		return attributesById.getAttribute(TaskAttribute.TASK_KIND);
	}

	/**
	 * Does not map to a common attribute and hence Returns <code>null</code>.
	 */
	@Nullable
	public String getTaskStatus() {
		return null;
	}

	@Nullable
	public String getTaskUrl() {
		return attributesById.getAttribute(TaskAttribute.TASK_URL);
	}

	@Nullable
	public String getVersion() {
		return attributesById.getAttribute(TaskAttribute.VERSION);
	}

	/**
	 * Throws {@link UnsupportedOperationException}.
	 */
	public void merge(ITaskMapping source) {
		throw new UnsupportedOperationException();
	}

	public void setAttribute(@NonNull String key, @Nullable String value) {
		attributesById.setAttribute(key, value);
	}

	public void setComponent(@Nullable String newComponent) {
		attributesById.setAttribute(TaskAttribute.COMPONENT, newComponent);
	}

	public void setDescription(@Nullable String description) {
		attributesById.setAttribute(TaskAttribute.DESCRIPTION, description);
	}

	public void setPriority(@Nullable String priority) {
		attributesById.setAttribute(TaskAttribute.PRIORITY, priority);
	}

	public void setProduct(@Nullable String product) {
		attributesById.setAttribute(TaskAttribute.PRODUCT, product);
	}

	public void setResolution(@Nullable String resolution) {
		attributesById.setAttribute(TaskAttribute.RESOLUTION, resolution);
	}

	public void setSeverity(@Nullable String severity) {
		attributesById.setAttribute(TaskAttribute.SEVERITY, severity);
	}

	public void setStatus(@Nullable String status) {
		attributesById.setAttribute(TaskAttribute.STATUS, status);
	}

	public void setSummary(@Nullable String summary) {
		attributesById.setAttribute(TaskAttribute.SUMMARY, summary);
	}

	public void setTaskKey(@Nullable String taskKey) {
		attributesById.setAttribute(TaskAttribute.TASK_KEY, taskKey);
	}

	public void setTaskKind(@Nullable String taskKind) {
		attributesById.setAttribute(TaskAttribute.TASK_KIND, taskKind);
	}

	public void setTaskUrl(@Nullable String newKind) {
		attributesById.setAttribute(TaskAttribute.TASK_URL, newKind);
	}

	public void setVersion(@Nullable String version) {
		attributesById.setAttribute(TaskAttribute.VERSION, version);
	}

}