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

package org.eclipse.mylyn.internal.tasks.activity.ui.provider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;

/**
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class ActivityRecordContentProvider implements ITreeContentProvider {
	private static final String PLUGIN_ID = "org.eclipse.mylyn.tasks.activity.ui"; //$NON-NLS-1$

	private static final Object[] NO_ELEMENTS = {};

	private IActivityStream activityStream;

	@Override
	public void dispose() {
		activityStream = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof IActivityStream activityStream) {
			querryProvider(activityStream);
			this.activityStream = activityStream;
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return activityStream.getEvents().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return NO_ELEMENTS;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

	private void querryProvider(IActivityStream activityStream) {
		try {
			activityStream.query(null);
		} catch (CoreException e) {
			StatusHandler.log(
					new Status(IStatus.ERROR, PLUGIN_ID, "Problem occured when querry the TaskActivityProvider.", e)); //$NON-NLS-1$
		}
	}
}