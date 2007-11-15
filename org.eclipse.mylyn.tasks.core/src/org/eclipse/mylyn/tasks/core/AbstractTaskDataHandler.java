/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Responsible for retrieving and posting task data to a repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Frank Becker
 * @since 2.0
 */
public abstract class AbstractTaskDataHandler {

	/**
	 * Download copy of task data from repository
	 * 
	 * @throws CoreException
	 */
	public abstract RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Download task data for each id provided
	 * 
	 * Override getMultiTaskData() to return true and implement this method if connector supports download of multiple
	 * task data in one request.
	 */
	public Set<RepositoryTaskData> getMultiTaskData(TaskRepository repository, Set<String> taskIds,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a reference to the newly created report in the case of new task submission, null otherwise
	 */
	public abstract String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * @param repositoryUrl
	 * @param repositoryKind
	 * @param taskKind
	 *            AbstractTask.DEFAULT_KIND or connector specific task kind string
	 * @return
	 */
	public abstract AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind,
			String taskKind);

	/**
	 * Initialize a new task data object with default attributes and values
	 */
	public abstract boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data,
			IProgressMonitor monitor) throws CoreException;

	public abstract AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData);

	/**
	 * @return Task id for any sub tasks referenced by the provided task data
	 */
	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		return Collections.emptySet();
	}

	/**
	 * @return true if connector support downloading multiple task data in single request, false otherwise. If true,
	 *         override and implement getMultiTaskData
	 */
	public boolean canGetMultiTaskData() {
		return false;
	}

	/**
	 * Sets attribute values from <code>sourceTaskData</code> on <code>targetTaskData</code>. Sets the following
	 * attributes:
	 * <ul>
	 * <li>summary
	 * <li>description
	 * </ul>
	 * Other attribute values are only set if they exist on <code>sourceTaskData</code> and
	 * <code>targetTaskData</code>.
	 * 
	 * @param sourceTaskData
	 *            the source task data values are copied from, the connector kind of repository of
	 *            <code>sourceTaskData</code> can be different from <code>targetTaskData</code>
	 * @param targetTaskData
	 *            the target task data values are copied to, the connector kind matches the one of this task data
	 *            handler
	 * @since 2.2
	 */
	public void cloneTaskData(RepositoryTaskData sourceTaskData, RepositoryTaskData targetTaskData) {
		targetTaskData.setSummary(sourceTaskData.getSummary());
		targetTaskData.setDescription(sourceTaskData.getDescription());
		if (sourceTaskData.getRepositoryKind().equals(targetTaskData.getRepositoryKind())
				&& sourceTaskData.getTaskKind().equals(targetTaskData.getTaskKind())) {
			// task data objects are from the same connector, copy all attributes
			for (RepositoryTaskAttribute sourceAttribute : sourceTaskData.getAttributes()) {
				copyAttributeValue(sourceAttribute, targetTaskData.getAttribute(sourceAttribute.getId()));
			}
		} else {
			// map attributes from common schema
			String[] commonAttributeKeys = new String[] { RepositoryTaskAttribute.KEYWORDS,
					RepositoryTaskAttribute.PRIORITY, RepositoryTaskAttribute.PRODUCT,
					RepositoryTaskAttribute.RESOLUTION, RepositoryTaskAttribute.USER_ASSIGNED,
					RepositoryTaskAttribute.USER_CC, };
			for (String key : commonAttributeKeys) {
				RepositoryTaskAttribute sourceAttribute = sourceTaskData.getAttribute(key);
				if (sourceAttribute != null) {
					copyAttributeValue(sourceAttribute, targetTaskData.getAttribute(key));
				}
			}
		}
	}

	private void copyAttributeValue(RepositoryTaskAttribute sourceAttribute, RepositoryTaskAttribute targetAttribute) {
		if (targetAttribute == null) {
			return;
		}

		if (!sourceAttribute.isReadOnly() && !sourceAttribute.isHidden() && !targetAttribute.isHidden()
				&& !targetAttribute.isReadOnly()) {
			targetAttribute.clearValues();
			if (targetAttribute.getOptions().size() > 0) {
				List<String> values = sourceAttribute.getValues();
				for (String value : values) {
					if (targetAttribute.getOptions().contains(value)) {
						targetAttribute.addValue(value);
					}
				}
			} else {
				List<String> values = sourceAttribute.getValues();
				for (String value : values) {
					targetAttribute.addValue(value);
				}
			}
		}
	}

}
