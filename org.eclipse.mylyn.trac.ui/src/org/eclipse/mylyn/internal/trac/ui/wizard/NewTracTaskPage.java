/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTaskDataHandler;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for creating new Trac tickets through a rich editor.
 * 
 * @author Steffen Pingel
 */
public class NewTracTaskPage extends WizardPage {

	// private boolean firstTime;

	private final TaskRepository taskRepository;

	private RepositoryTaskData taskData;

	public NewTracTaskPage(TaskRepository taskRepository) {
		super("New Task");

		setTitle("Create via Rich Editor");
		setDescription("This will open an editor that can be used to create a new task.");

		this.taskRepository = taskRepository;
	}

	public void createControl(Composite parent) {
		Text text = new Text(parent, SWT.WRAP);
		text.setEditable(false);
		setControl(text);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		// if (visible && firstTime) {
		// firstTime = false;
		// if (!hasAttributes()) {
		// // delay the execution so the dialog's progress bar is visible
		// // when the attributes are updated
		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// if (getControl() != null && !getControl().isDisposed()) {
		// updateAttributesFromRepository();
		// }
		// }
		// });
		// }
		// }
		updateAttributesFromRepository();
	}

	// private boolean hasAttributes() {
	// TracRepositoryConnector connector = (TracRepositoryConnector)
	// TasksUiPlugin.getRepositoryManager()
	// .getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);
	// try {
	// ITracClient client =
	// connector.getClientManager().getRepository(taskRepository);
	// return client.hasAttributes();
	// } catch (MalformedURLException e) {
	// return false;
	// }
	// }

	@Override
	public boolean isPageComplete() {
		return taskData != null;
	}

	private void updateAttributesFromRepository() {
		TracRepositoryConnector connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);
		final ITracClient client = connector.getClientManager().getRepository(taskRepository);

		if (!client.hasAttributes()) {
			try {
				IRunnableWithProgress runnable = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							client.updateAttributes(monitor, true);
						} catch (TracException e) {
							throw new InvocationTargetException(e);
						}
					}
				};

				getContainer().run(true, true, runnable);
			} catch (InvocationTargetException e) {
				StatusHandler.displayStatus("Error updating attributes", TracCorePlugin.toStatus(e.getCause(),
						taskRepository));
				return;
			} catch (InterruptedException e) {
				return;
			}
		}

		TracTaskDataHandler offlineHandler = (TracTaskDataHandler) connector.getTaskDataHandler();
		AbstractAttributeFactory attributeFactory = offlineHandler.getAttributeFactory(taskRepository.getRepositoryUrl(),
				taskRepository.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);
		this.taskData = new RepositoryTaskData(attributeFactory, TracCorePlugin.REPOSITORY_KIND,
				taskRepository.getRepositoryUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
		this.taskData.setNew(true);
		TracTaskDataHandler.createDefaultAttributes(taskData.getAttributeFactory(), taskData, client, false);
	}

	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

}
