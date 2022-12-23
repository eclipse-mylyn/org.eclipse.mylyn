/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;

/**
 * Clients can contribute an implementation of this class to be notified of all task submissions for a particular
 * connector, or all connectors. Implementations can be contributed using the taskJobListeners extension point. See also
 * {@link SubmitJobListener} if you only need to be notified about specific {@link SubmitJob}s.
 * 
 * @author Sam Davis
 * @since 3.7
 */
public abstract class TaskJobListener {

	/**
	 * Called when a task is about to be submitted. Note that the submission may fail after this call due to network
	 * error, invalid credentials, etc.
	 */
	public abstract void aboutToSubmit(TaskJobEvent event);

	/**
	 * Called after a submission has completed successfully.
	 */
	public abstract void taskSubmitted(TaskJobEvent event);

}