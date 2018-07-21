/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Note: This is provisional API that is used by connectors.
 * <p>
 * DO NOT CHANGE.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractSearchHandler {

	public abstract String getConnectorKind();

	public abstract boolean queryForText(TaskRepository taskRepository, IRepositoryQuery query, TaskData taskData,
			String searchString);

}
