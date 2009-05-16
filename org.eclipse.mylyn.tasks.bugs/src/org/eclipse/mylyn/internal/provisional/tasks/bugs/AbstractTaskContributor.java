/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.tasks.bugs;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskContributor {

	/**
	 * @since 3.2
	 */
	public void preProcess(ISupportRequest request) {
	}

	/**
	 * @deprecated use {@link #contribute(ISTatus, ITaskContribution)} instead
	 */
	@Deprecated
	public abstract Map<String, String> getAttributes(IStatus status);

	/**
	 * Returns the id of an editor that should be used to open the {@link TaskEditorInput} with the task.
	 * 
	 * @param status
	 *            the status
	 * @return id of editor
	 */
	@Deprecated
	public String getEditorId(IStatus status) {
		return null;
	}

	/**
	 * @since 3.2
	 */
	public void process(ITaskContribution contribution) {
	}

	/**
	 * @deprecated use {@link #postProcess(ISupportResponse)} instead
	 */
	@Deprecated
	public void postProcess(IStatus status, TaskData taskData) {
	}

	/**
	 * @since 3.2
	 */
	public void postProcess(ISupportResponse response) {
	}

}
