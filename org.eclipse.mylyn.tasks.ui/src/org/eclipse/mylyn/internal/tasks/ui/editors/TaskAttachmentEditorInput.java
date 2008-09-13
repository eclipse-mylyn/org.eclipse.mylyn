/*******************************************************************************
 * Copyright (c) 2004, 2008 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class TaskAttachmentEditorInput extends PlatformObject implements IStorageEditorInput {

	private final ITaskAttachment attachment;

	public TaskAttachmentEditorInput(ITaskAttachment attachment) {
		this.attachment = attachment;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// ignore
		return null;
	}

	public String getName() {
		return attachment.getFileName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return attachment.getUrl();
	}

	public IStorage getStorage() throws CoreException {
		return TaskAttachmentStorage.create(attachment);
	}
}
