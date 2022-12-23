/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
class CommonStorable implements ICommonStorable {

	private final File path;

	private final CommonStore store;

	public CommonStorable(CommonStore store, File path) {
		this.store = store;
		this.path = path;
	}

	public void delete(String item) throws CoreException {
		getFile(item).delete();
	}

	public void deleteAll() throws CoreException {
		File[] children = path.listFiles();
		if (children != null) {
			// validate
			for (File child : children) {
				if (child.isDirectory()) {
					throw new CoreException(new Status(IStatus.ERROR, CommonsCorePlugin.ID_PLUGIN, NLS.bind(
							"The storage location ''{0}'' contains sub directories", path))); //$NON-NLS-1$
				}
			}

			// delete all files
			for (File child : children) {
				child.delete();
			}
		}

		if (path.exists()) {
			path.delete();
		}
	}

	public boolean exists(String handle) {
		if (!path.exists()) {
			return false;
		}
		return getFile(handle).exists();
	}

	public IStatus flush() {
		return Status.OK_STATUS;
	}

	public File getPath() {
		return path;
	}

	public boolean isDirty() {
		return false;
	}

	public InputStream read(String item, IProgressMonitor monitor) throws IOException {
		File file = getFile(item);
		return new FileInputStream(file);
	}

	public void release() {
		store.release(this);
	}

	public OutputStream write(String item, IProgressMonitor monitor) throws IOException {
		File file = getFile(item);
		return new FileOutputStream(file);
	}

	private File getFile(String item) {
		File file = new File(path, item);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

}
