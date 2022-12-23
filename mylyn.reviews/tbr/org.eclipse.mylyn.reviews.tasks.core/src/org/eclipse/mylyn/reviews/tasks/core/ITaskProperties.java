/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author mattk
 *
 */
public interface ITaskProperties {

	String getDescription();

	String getAssignedTo();

	List<TaskComment> getComments();

	List<Attachment> getAttachments();

	ITaskProperties loadFor(String taskId) throws CoreException;

	String getTaskId();

	String getNewCommentText();

	void setNewCommentText(String comment);

	void setDescription(String description);

	void setSummary(String summary);

	void setAssignedTo(String assignee);

	String getRepositoryUrl();

	String getReporter();

}

