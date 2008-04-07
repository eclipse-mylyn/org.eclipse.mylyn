/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
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

			String label1 = t1.getProperty(IRepositoryConstants.PROPERTY_LABEL);
			String label2 = t2.getProperty(IRepositoryConstants.PROPERTY_LABEL);

			if (LocalRepositoryConnector.REPOSITORY_LABEL.equals(label1)) {
				return -1;
			} else if (LocalRepositoryConnector.REPOSITORY_LABEL.equals(label2)) {
				return 1;
			}

			if (!t1.getConnectorKind().equals(t2.getConnectorKind())) {
				return (t1.getConnectorKind()).compareTo(t2.getConnectorKind());
			} else {
				if ((label1 == null || label1.equals("")) && label2 != null) {
					return 1;
				} else if (label1 != null && (label2 == null || label2.equals(""))) {
					return -1;
				} else if (label1 != null && label2 != null) {
					return label1.compareTo(label2);
				} else {
					return (t1.getRepositoryUrl()).compareTo(t2.getRepositoryUrl());
				}
			}
		} else {
			return super.compare(viewer, e1, e2);
		}
	}
}