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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * @author Rob Elves
 */
public abstract class AbstractExternalizationParticipant implements IExternalizationParticipant {

	static final String SNAPSHOT_PREFIX = ".";

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
		File originalFile = file.getAbsoluteFile();
		File backup = new File(file.getParentFile(), SNAPSHOT_PREFIX + file.getName());
		backup.delete();
		return originalFile.renameTo(backup);
	}

	public abstract void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException;

	public abstract String getDescription();

	public abstract ISchedulingRule getSchedulingRule();

	public abstract boolean isDirty();

}