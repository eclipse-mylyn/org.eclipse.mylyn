/*******************************************************************************
 * Copyright (c) 2010, 2011 Peter Stibrany and others.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

/**
 * @author Peter Stibrany
 */
public class AttachmentFileStorage extends PlatformObject implements IStorage {

	private final File file;

	private final String name;

	public AttachmentFileStorage(File file, String name) {
		this.file = file;
		this.name = name;
	}

	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.FileStorage_unableToReadAttachmentFile, e));
		}
	}

	public IPath getFullPath() {
		return Path.fromOSString(file.getAbsolutePath());
	}

	public String getName() {
		return name;
	}

	public boolean isReadOnly() {
		return true;
	}

}
