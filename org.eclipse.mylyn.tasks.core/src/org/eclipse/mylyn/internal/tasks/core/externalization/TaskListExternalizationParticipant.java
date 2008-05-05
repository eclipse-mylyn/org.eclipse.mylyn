/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;

/**
 * @author Rob Elves
 */
public class TaskListExternalizationParticipant implements IExternalizationParticipant, ITaskListChangeListener {

	private static final String SNAPSHOT_PREFIX = ".";

	private final ExternalizationManager manager;

	private final TaskListExternalizer taskListWriter;

	private final TaskList taskList;

	private boolean dirty;

	public TaskListExternalizationParticipant(TaskList taskList, TaskListExternalizer taskListExternalizer,
			ExternalizationManager manager) {
		this.manager = manager;
		this.taskList = taskList;
		this.taskListWriter = taskListExternalizer;
	}

	public ISchedulingRule getSchedulingRule() {
		return TaskList.getSchedulingRule();
	}

	public boolean isDirty() {
		return dirty;
	}

	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);

		final File taskListFile = getTaskListFile(context.getRootPath());

		if (!taskListFile.exists()) {
			try {
				taskListFile.createNewFile();
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Task List file not found, error creating new file.", e));
			}
		}

		switch (context.getKind()) {
		case SAVE:
			if (!takeSnapshot(taskListFile)) {
				StatusHandler.fail(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"Task List snapshot failed"));
			}

			ITaskListRunnable saveRunnable = new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					taskListWriter.writeTaskList(taskList, taskListFile);
					synchronized (TaskListExternalizationParticipant.this) {
						dirty = false;
					}
				}
			};

			taskList.run(saveRunnable, monitor);

			break;
		case LOAD:
			ITaskListRunnable loadRunnable = new ITaskListRunnable() {

				public void execute(IProgressMonitor monitor) throws CoreException {
					try {
						resetAndLoad();
					} catch (CoreException e) {
						if (recover()) {
							resetAndLoad();
						} else {
							//XXX taskList.reset();
//							throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
//								"Task List recovered from snapshot"))
							throw e;
//							StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
//									"Task List not found"));
						}
					}
				}

				private void resetAndLoad() throws CoreException {
					// XXX: taskList.reset();
					taskList.preTaskListRead();
					taskListWriter.readTaskList(taskList, taskListFile);
					taskList.postTaskListRead();
				}

				private boolean recover() {
					if (restoreSnapshot(taskListFile)) {
						StatusHandler.log(new Status(IStatus.INFO, ITasksCoreConstants.ID_PLUGIN,
								"Task List recovered from snapshot"));
						return true;
					} else {
						return false;
					}

				}

			};

			taskList.run(loadRunnable, monitor);

			break;
		case SNAPSHOT:
			break;
		}

	}

	private boolean restoreSnapshot(File file) {
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

	private boolean takeSnapshot(File file) {
		File originalFile = file.getAbsoluteFile();
		File backup = new File(file.getParentFile(), SNAPSHOT_PREFIX + file.getName());
		backup.delete();
		return originalFile.renameTo(backup);
	}

	public void containersChanged(Set<TaskContainerDelta> containers) {
		synchronized (TaskListExternalizationParticipant.this) {
			dirty = true;
		}
		manager.requestSave();
	}

	public void taskListRead() {
		// ignore
	}

	public String getDescription() {
		return "Task List";
	}

	public static File getTaskListFile(String rootPath) {
		return new File(rootPath + File.separator + ITasksCoreConstants.DEFAULT_TASK_LIST_FILE);
	}

}
