/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten 
 */
public class TaskRepositoriesViewSorter extends ViewerSorter {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof TaskRepository && e2 instanceof TaskRepository) {
			TaskRepository t1 = (TaskRepository) e1;
			TaskRepository t2 = (TaskRepository) e2;
			return (t1.getKind() + t1.getUrl()).compareTo(t2.getKind() + t2.getUrl());
		} else {
			return super.compare(viewer, e1, e2);
		}
	}
}