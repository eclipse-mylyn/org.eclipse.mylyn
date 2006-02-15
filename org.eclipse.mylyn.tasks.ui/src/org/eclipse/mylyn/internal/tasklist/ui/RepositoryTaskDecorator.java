/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.internal.tasklist.AbstractQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery)element;
			String repositoryUrl = query.getRepositoryUrl();
			if (repositoryUrl != null) {
				try {
					URL url = new URL(repositoryUrl);
					decoration.addSuffix("    [" + url.getHost() + "]");
				} catch (MalformedURLException e) {
					decoration.addSuffix("    [ <unknown host> ]");
				}
			} 
		} else if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask)element;			 
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
				decoration.addOverlay(TaskListImages.OVERLAY_OUTGOING, IDecoration.TOP_RIGHT);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				decoration.addOverlay(TaskListImages.OVERLAY_INCOMMING, IDecoration.TOP_RIGHT);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				decoration.addOverlay(TaskListImages.OVERLAY_CONFLICT, IDecoration.TOP_RIGHT);
			}
		} else if (element instanceof AbstractQueryHit) {
			decorate(((AbstractQueryHit)element).getCorrespondingTask(), decoration);
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore

	}

	public void dispose() {
		// ignore

	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore

	}

}
