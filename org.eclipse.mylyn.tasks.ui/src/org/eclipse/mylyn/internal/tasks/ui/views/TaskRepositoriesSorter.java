/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof TaskRepository && e2 instanceof TaskRepository) {
			TaskRepository t1 = (TaskRepository) e1;
			TaskRepository t2 = (TaskRepository) e2;

			String label1 = t1.getRepositoryLabel();
			String label2 = t2.getRepositoryLabel();

			if (label1 == null) {
				return (label2 != null) ? 1 : 0;
			} else if (label2 == null) {
				return -1;
			}

			if (LocalRepositoryConnector.REPOSITORY_LABEL.equals(label1)) {
				return -1;
			} else if (LocalRepositoryConnector.REPOSITORY_LABEL.equals(label2)) {
				return 1;
			}

			return label1.compareToIgnoreCase(label2);
		}
		return super.compare(viewer, e1, e2);
	}
}