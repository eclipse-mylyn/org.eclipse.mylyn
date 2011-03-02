/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
