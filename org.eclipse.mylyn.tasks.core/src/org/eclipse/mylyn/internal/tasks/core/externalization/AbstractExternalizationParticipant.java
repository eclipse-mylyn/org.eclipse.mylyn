/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * File based externalization participant
 * 
 * @author Rob Elves
 */
public abstract class AbstractExternalizationParticipant implements IExternalizationParticipant {

	static final String SNAPSHOT_PREFIX = ".";

	public abstract void load(String rootPath, IProgressMonitor monitor) throws CoreException;

	public abstract void save(String rootPath, IProgressMonitor monitor) throws CoreException;

	public abstract String getDescription();

	public abstract ISchedulingRule getSchedulingRule();

	public abstract boolean isDirty();

	public abstract String getFileName();

	public AbstractExternalizationParticipant() {
		super();
	}

	protected boolean restoreSnapshot(File file) {
		File backup = new File(file.getParentFile(), SNAPSHOT_PREFIX + file.getName());
		File originalFile = file.getAbsoluteFile();
		if (originalFile.exists()) {
			SimpleDateFormat format = new SimpleDateFormat(ITasksCoreConstants.FILENAME_TIMESTAMP_FORMAT,
					Locale.ENGLISH);
			File failed = new File(file.getParentFile(), "failed-" + format.format(new Date()) + "-"
					+ originalFile.getName());
			originalFile.renameTo(failed);
		}
		return backup.renameTo(originalFile);
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

	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		monitor = Policy.monitorFor(monitor);
		final File dataFile = getFile(context.getRootPath());
		switch (context.getKind()) {
		case SAVE:
			if (dataFile != null) {
				takeSnapshot(dataFile);
			}
			save(context.getRootPath(), monitor);
			break;
		case LOAD:
			try {
				load(context.getRootPath(), monitor);
			} catch (CoreException e) {
				restoreSnapshot(dataFile);
				load(context.getRootPath(), monitor);
			}
			break;
		case SNAPSHOT:
			break;
		}

	}

	public File getFile(String rootPath) throws CoreException {
		String fileName = getFileName();
		if (fileName != null) {
			String filePath = rootPath + File.separator + getFileName();
			return new File(filePath);
		}

//		if (!file.exists()) {
//			try {
//				if (!file.createNewFile()) {
//					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, getDescription()
//							+ " file not found, error creating new file."));
//				}
//			} catch (IOException e) {
//				throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, getDescription()
//						+ " file not found, error creating new file.", e));
//			}
//		}

		return null;
	}

}