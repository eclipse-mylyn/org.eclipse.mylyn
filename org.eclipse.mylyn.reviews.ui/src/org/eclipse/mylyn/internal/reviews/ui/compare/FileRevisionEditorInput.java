/*******************************************************************************
 * Copyright (c) 2013, 2014, Ericsson AB and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastien Dubois (Ericsson) - Adapted to use with Mylyn Reviews
 *******************************************************************************/
package org.eclipse.mylyn.internal.reviews.ui.compare;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An Editor input for file revisions
 * 
 * @author Sebastien Dubois
 */
public class FileRevisionEditorInput extends PlatformObject implements IWorkbenchAdapter, IStorageEditorInput {

	private final IFileRevision fileRevision;

	private final IProgressMonitor runningMonitor;

	public FileRevisionEditorInput(IFileRevision fileRevision, IProgressMonitor monitor) {
		this.fileRevision = fileRevision;
		this.runningMonitor = monitor;
	}

	public IStorage getStorage() {
		try {
			return fileRevision.getStorage(runningMonitor);
		} catch (CoreException e) {
			//There was a problem getting contents of the file revision in the repository history
			//This should not happen at this point, because we already checked that the contents was valid.
			//In any case, this can be safely ignored as we will fall back to using the Review contents instead.
			StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID,
					"Cannot retrieve file revision contents from local repository, defaulting to review contents instead")); //$NON-NLS-1$
		}
		return null;
	}

	public boolean exists() {
		return fileRevision.exists();
	}

	public String getName() {
		return fileRevision.getName();
	}

	public IPersistableElement getPersistable() {
		return null; // can't persist
	}

	public String getToolTipText() {
		return fileRevision.getURI().getPath();
	}

	@Override
	public boolean equals(Object aObject) {
		if (aObject == this) {
			return true;
		}
		if (aObject instanceof FileRevisionEditorInput) {
			final FileRevisionEditorInput other = (FileRevisionEditorInput) aObject;
			return other.fileRevision.equals(fileRevision);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fileRevision.hashCode();
	}

	public IFileRevision getFileRevision() {
		return fileRevision;
	}

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		return fileRevision.getName();
	}

	public Object getParent(Object o) {
		// ignore
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		// ignore
		return null;
	}
}
