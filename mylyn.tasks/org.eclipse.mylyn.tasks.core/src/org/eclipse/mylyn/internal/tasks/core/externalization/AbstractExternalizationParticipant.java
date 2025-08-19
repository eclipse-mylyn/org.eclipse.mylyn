/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * File based externalization participant
 *
 * @author Rob Elves
 */
public abstract class AbstractExternalizationParticipant implements IExternalizationParticipant {

	public static final String SNAPSHOT_PREFIX = "."; //$NON-NLS-1$

	public abstract void load(File sourceFile, IProgressMonitor monitor) throws CoreException;

	public abstract void save(File targetFile, IProgressMonitor monitor) throws CoreException;

	@Override
	public abstract String getDescription();

	@Override
	public abstract ISchedulingRule getSchedulingRule();

	@Override
	public abstract boolean isDirty();

	public abstract String getFileName();

	public AbstractExternalizationParticipant() {
	}

	protected boolean takeSnapshot(File file) {
		if (file.length() > 0) {
			File originalFile = file.getAbsoluteFile();
			File backup = new File(file.getParentFile(), SNAPSHOT_PREFIX + file.getName());
			backup.delete();
			return originalFile.renameTo(backup);
		}
		return false;
	}

	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		monitor = Policy.monitorFor(monitor);
		final File dataFile = getFile(context.getRootPath());
		switch (context.getKind()) {
			case SAVE:
				if (dataFile != null) {
					takeSnapshot(dataFile);
				}
				save(dataFile, monitor);
				break;
			case LOAD:
				performLoad(dataFile, monitor);
				break;
			case SNAPSHOT:
				break;
		}

	}

	protected boolean performLoad(final File dataFile, IProgressMonitor monitor) throws CoreException {
		try {
			load(dataFile, monitor);
			return true;
		} catch (CoreException e) {
			if (dataFile != null) {
				File backup = new File(dataFile.getParentFile(), SNAPSHOT_PREFIX + dataFile.getName());
				if (backup.exists()) {
					StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Failed to load " //$NON-NLS-1$
							+ dataFile.getName() + ", restoring from snapshot", e)); //$NON-NLS-1$
					load(backup, monitor);
					return true;
				}
			}
		}
		return false;
	}

	public File getFile(String rootPath) throws CoreException {
		String fileName = getFileName();
		if (fileName != null) {
			String filePath = rootPath + File.separator + getFileName();
			return new File(filePath);
		}

		return null;
	}

	/**
	 * @param full
	 *            true when the workbench is shutting down and any time-based dirty states should be ignore
	 */
	@Override
	public boolean isDirty(boolean full) {
		return isDirty();
	}

}