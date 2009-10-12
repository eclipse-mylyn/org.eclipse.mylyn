/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Steffen Pingel
 */
public class TaskActivationExternalizationParticipant extends AbstractExternalizationParticipant implements
		ITaskActivationListener {

	private final ExternalizationManager externalizationManager;

	private boolean dirty;

	private final TaskActivationHistory activationHistory;

	private final File file;

	private final TaskList taskList;

	public TaskActivationExternalizationParticipant(ExternalizationManager externalizationManager, TaskList taskList,
			TaskActivationHistory history, File file) {
		this.externalizationManager = externalizationManager;
		this.taskList = taskList;
		this.activationHistory = history;
		this.file = file;
	}

	@Override
	public String getDescription() {
		return Messages.TaskActivationExternalizationParticipant_Task_Activation_History;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.ACTIVITY_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private void requestSave() {
		synchronized (TaskActivationExternalizationParticipant.this) {
			dirty = true;
		}
		externalizationManager.requestSave();
	}

	@Override
	public void load(File sourceFile, IProgressMonitor monitor) throws CoreException {
		try {
			activationHistory.clear();
			if (file.exists()) {
				FileReader reader = new FileReader(file);
				try {
					XMLMemento memento = XMLMemento.createReadRoot(reader);
					IMemento[] items = memento.getChildren("task"); //$NON-NLS-1$
					for (IMemento child : items) {
						String handle = child.getString("handle"); //$NON-NLS-1$
						if (handle != null) {
							AbstractTask task = taskList.getTask(handle);
							if (task != null) {
								activationHistory.addTaskInternal(task);
							}
						}
					}
				} finally {
					reader.close();
				}
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Failed to load task activation history", e)); //$NON-NLS-1$
		}
	}

	@Override
	public void save(File targetFile, IProgressMonitor monitor) throws CoreException {
		synchronized (TaskActivationExternalizationParticipant.this) {
			dirty = false;
		}

		XMLMemento memento = XMLMemento.createWriteRoot("taskActivationHistory"); //$NON-NLS-1$
		for (AbstractTask task : activationHistory.getPreviousTasks()) {
			IMemento child = memento.createChild("task"); //$NON-NLS-1$
			child.putString("handle", task.getHandleIdentifier()); //$NON-NLS-1$
		}

		try {
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			try {
				memento.save(writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Failed to save task activation history", e)); //$NON-NLS-1$
		}
	}

	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public File getFile(String rootPath) throws CoreException {
		return file;
	}

	public void preTaskActivated(ITask task) {
		// ignore
	}

	public void preTaskDeactivated(ITask task) {
		// ignore
	}

	public void taskActivated(ITask task) {
		activationHistory.addTask((AbstractTask) task);
		requestSave();
	}

	public void taskDeactivated(ITask task) {
		// ignore		
	}

}
