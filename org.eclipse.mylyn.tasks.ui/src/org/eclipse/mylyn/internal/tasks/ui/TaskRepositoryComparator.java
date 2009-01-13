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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Comparator;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryComparator implements Comparator<TaskRepository> {
	public int compare(TaskRepository t1, TaskRepository t2) {
		if (t1.getRepositoryLabel() != null && t2.getRepositoryLabel() != null) {
			return t1.getRepositoryLabel().compareTo(t2.getRepositoryLabel());
		} else if (t1.getRepositoryLabel() == null && t2.getRepositoryLabel() == null) {
			return t1.getRepositoryUrl().compareTo(t2.getRepositoryUrl());
		} else if (t1.getRepositoryLabel() == null) {
			return -1;
		} else if (t2.getRepositoryLabel() == null) {
			return 1;
		}
		return 1;
	}
}