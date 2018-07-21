/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import java.util.Collection;

import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Describes differences between two {@link TaskData} objects.
 * 
 * @author Steffen Pingel
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 3.5
 */
public interface ITaskDataDiff {

	public boolean hasChanged();

	public void setHasChanged(boolean hasChanged);

	public TaskRepository getRepository();

	public Collection<ITaskAttributeDiff> getChangedAttributes();

	public Collection<ITaskComment> getNewComments();

}
