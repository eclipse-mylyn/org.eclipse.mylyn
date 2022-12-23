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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class CommonStore {

	/**
	 * Delays writing of mementos to avoid blocking UI thread.
	 */
	private class FlushJob extends Job {

		public FlushJob() {
			super("Flush context mementos"); //$NON-NLS-1$
			setSystem(true);
			setPriority(Job.SHORT);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			flushPending();
			return Status.OK_STATUS;
		}

	}

	private static final long FLUSH_DELAY = 500;

	private boolean scheduled;

	private FlushJob flushJob;

	private final Map<File, CommonStorable> storableByLocation;

	private File location;

	public CommonStore(File location) {
		Assert.isNotNull(location);
		this.storableByLocation = new HashMap<File, CommonStorable>();
		this.location = location;
	}

	public synchronized ICommonStorable get(IPath path) {
		File file = getFile(path);
		CommonStorable storable = storableByLocation.get(file);
		if (storable == null) {
			storable = new CommonStorable(this, file);
			storableByLocation.put(file, storable);
		}
		return storable;
	}

	public File getLocation() {
		return location;
	}

	public void setLocation(File location) {
		Assert.isNotNull(location);
		this.location = location;
	}

	public void stop() {
		synchronized (this) {
			if (flushJob != null) {
				flushJob.cancel();
				flushJob = null;
			}
		}
		flushPending();
	}

	private File getFile(IPath path) {
		return getFile(path, true);
	}

	private File getFile(IPath path, boolean create) {
		File file = new File(location, path.toOSString());
		if (create && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

	synchronized void schedule() {
		if (!scheduled) {
			if (flushJob == null) {
				flushJob = new FlushJob();
			}
			flushJob.schedule(FLUSH_DELAY);
		}
	}

	synchronized void flushPending() {
		MultiStatus status = new MultiStatus(CommonsCorePlugin.ID_PLUGIN, 0, "Failed to save storable", null); //$NON-NLS-1$
		for (CommonStorable memento : storableByLocation.values()) {
			if (memento.isDirty()) {
				IStatus result = memento.flush();
				status.add(result);
			}
		}
		if (!status.isOK()) {
			StatusHandler.log(status);
		}
	}

	synchronized void release(CommonStorable storable) {
		storableByLocation.remove(storable.getPath());
	}

	public void move(IPath oldPath, IPath newPath) throws CoreException {
		File oldFile = getFile(oldPath, false);
		// TODO lock hierarchy and throw an exception if oldFile is in use 
		if (oldFile.exists()) {
			File newFile = getFile(newPath, false);
			newFile.getParentFile().mkdirs();
			if (!oldFile.renameTo(newFile)) {
				throw new CoreException(new Status(IStatus.ERROR, CommonsCorePlugin.ID_PLUGIN, NLS.bind(
						"The target path ''{0}'' already exists", newPath))); //$NON-NLS-1$
			}
		}
	}

}
