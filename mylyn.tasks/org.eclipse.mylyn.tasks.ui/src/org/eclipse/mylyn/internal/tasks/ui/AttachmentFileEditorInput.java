/*******************************************************************************
 * Copyright (c) 2010 Peter Stibrany and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Peter Stibrany
 */
class AttachmentFileEditorInput extends PlatformObject implements IPathEditorInput, IStorageEditorInput {

	private final File file;

	private final String name;

	private final String tooltipText;

	AttachmentFileEditorInput(File file, String name, String tooltipText) {
		this.file = file;
		this.name = name;
		this.tooltipText = tooltipText;
	}

	public IPath getPath() {
		return Path.fromOSString(file.getAbsolutePath());
	}

	public boolean exists() {
		return file.exists();
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		// ignore
		return null;
	}

	public String getToolTipText() {
		return tooltipText;
	}

	public IStorage getStorage() throws CoreException {
		return new AttachmentFileStorage(file, name);
	}

	@Override
	public int hashCode() {
		return file.hashCode() ^ name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AttachmentFileEditorInput)) {
			return false;
		}

		AttachmentFileEditorInput other = (AttachmentFileEditorInput) obj;
		return name.equals(other.name) && file.equals(other.file);
	}
}
