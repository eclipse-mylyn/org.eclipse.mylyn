/*******************************************************************************
 * Copyright (c) 2012 Timur Achmetow and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Timur Achmetow - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.ui;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;

/**
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class ActivityRecordContentProvider implements ITreeContentProvider {
	private static final Object[] NO_ELEMENTS = new Object[0];

	private List<ActivityEvent> eventList;

	public void dispose() {
		eventList = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof IActivityStream) {
			eventList = ((IActivityStream) newInput).getEvents();
		}
	}

	public Object[] getElements(Object inputElement) {
		return eventList.toArray();
	}

	public Object[] getChildren(Object parentElement) {
//		if (parentElement instanceof ActivityCommitEvent) {
//			return ((ActivityCommitEvent) parentElement).getChanges().toArray();
//		}
		return NO_ELEMENTS;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
//		if (element instanceof ActivityCommitEvent) {
//			return true;
//		}
		return false;
	}
}