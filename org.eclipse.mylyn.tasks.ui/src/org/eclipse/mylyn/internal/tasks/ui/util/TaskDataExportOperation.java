/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * Zips task data up to specified directly and filename.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves TODO: Move into internal.tasks.core
 */
@SuppressWarnings("restriction")
public class TaskDataExportOperation implements IRunnableWithProgress {

	private static final String EXPORT_JOB_LABEL = Messages.TaskDataExportOperation_exporting_task_data;

	private static final Pattern excludePattern = Pattern.compile("(?:^\\.|^monitor-log.xml\\z|^tasklist.xml.zip\\z|attachments\\z|backup\\z)"); //$NON-NLS-1$

	private final String destinationDirectory;

	private final String destinationFilename;

	public TaskDataExportOperation(String destinationDirectory, String destinationFilename) {
		this.destinationFilename = destinationFilename;
		this.destinationDirectory = destinationDirectory;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor = Policy.monitorFor(monitor);

		Set<File> filesToExport = new HashSet<File>();
		selectFiles(filesToExport);

		if (filesToExport.size() > 0 && Platform.isRunning()) {
			try {
				monitor.beginTask(EXPORT_JOB_LABEL, filesToExport.size() + 1);

				Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE,
						new SubProgressMonitor(monitor, 1));

				ZipFileUtil.createZipFile(getDestinationFile(), new ArrayList<File>(filesToExport),
						TasksUiPlugin.getDefault().getDataDirectory(), monitor);
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			} finally {
				Job.getJobManager().endRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
				monitor.done();
			}
		}
	}

	public File getDestinationFile() {
		return new File(destinationDirectory + File.separator + destinationFilename);
	}

	protected void selectFiles(Set<File> filesToExport) {
		Set<Pattern> exclusionPatterns = new HashSet<Pattern>();
		exclusionPatterns.add(excludePattern);
		String dataRoot = TasksUiPlugin.getDefault().getDataDirectory();
		File dataFolder = new File(dataRoot);
		for (File file : dataFolder.listFiles()) {
			boolean exclude = false;
			for (Pattern pattern : exclusionPatterns) {
				if (pattern.matcher(file.getName()).find()) {
					exclude = true;
					break;
				}
			}
			if (!exclude) {
				filesToExport.add(file);
			}
		}

	}

	protected File getSourceFolder() {
		return new File(TasksUiPlugin.getDefault().getDataDirectory());
	}
}
