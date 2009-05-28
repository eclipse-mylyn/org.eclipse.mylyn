/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - initial prototype
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class TaskPlanningEditor extends TaskFormPage {

	private Composite editorComposite;

	private TaskRepository repository;

	private AbstractTask task;

	private Button saveButton;

	private final ITaskListChangeListener TASK_LIST_LISTENER = new TaskListChangeAdapter() {
		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getElement() instanceof ITask) {
					final AbstractTask updateTask = (AbstractTask) taskContainerDelta.getElement();
					if (updateTask != null && task != null
							&& updateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
						if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									refresh();
								}
							});
						}
						break;
					}
				}
			}
		}

	};

	private CommonTextSupport textSupport;

	private ITaskActivityListener timingListener;

	private FormToolkit toolkit;

	public TaskPlanningEditor(TaskEditor editor) {
		super(editor, ITasksUiConstants.ID_PAGE_PLANNING, Messages.TaskPlanningEditor_Planning);
		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
	}

	private void createContributions(final Composite editorComposite) {
		Collection<AbstractLocalEditorPart> localEditorContributions = TaskEditorContributionExtensionReader.getLocalEditorContributions();
		for (final AbstractLocalEditorPart part : localEditorContributions) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Error creating task editor contribution: \"" + part.getSectionName() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
				}

				public void run() throws Exception {
					initializePart(editorComposite, part);
				}

			});
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		TaskEditorInput taskEditorInput = (TaskEditorInput) getEditorInput();
		task = (AbstractTask) taskEditorInput.getTask();
		repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl());

		toolkit = managedForm.getToolkit();

		editorComposite = managedForm.getForm().getBody();
		GridLayout editorLayout = new GridLayout();
		editorLayout.verticalSpacing = 3;
		editorComposite.setLayout(editorLayout);

		if (task instanceof LocalTask) {
			SummaryPart part = new SummaryPart();
			part.setTextSupport(textSupport);
			initializePart(editorComposite, part);
			initializePart(editorComposite, new AttributePart());
			// currently there is only one location for extensions
			createContributions(editorComposite);
		}

		PlanningPart planningPart = new PlanningPart(SWT.NONE, true);
		// disable due date picker if it's a repository due date
		boolean needsDueDate = true;
		if (task != null) {
			try {
				TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
				if (taskData != null) {
					AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(taskData.getConnectorKind());
					TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(
							taskData.getConnectorKind(), taskData.getRepositoryUrl());
					if (connector != null && taskRepository != null
							&& connector.hasRepositoryDueDate(taskRepository, task, taskData)) {
						needsDueDate = false;
					}
				}
			} catch (CoreException e) {
				// ignore
			}
		}
		planningPart.initialize(getManagedForm(), repository, task, needsDueDate, this, textSupport);
		planningPart.createControl(editorComposite, toolkit);
		planningPart.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(planningPart);
	}

	@Override
	public void dispose() {
		if (timingListener != null) {
			TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		}
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		// update task title
		TasksUiInternal.getTaskList().notifyElementChanged(task);
	}

	@Override
	public void doSaveAs() {
		// don't support saving as
	}

	/**
	 * Override for customizing the tool bar.
	 */
	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		TaskEditorInput taskEditorInput = (TaskEditorInput) getEditorInput();
		if (taskEditorInput.getTask() instanceof LocalTask) {
			DeleteTaskEditorAction deleteAction = new DeleteTaskEditorAction(taskEditorInput.getTask());
			toolBarManager.add(deleteAction);

			NewSubTaskAction newSubTaskAction = new NewSubTaskAction();
			newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(taskEditorInput.getTask()));
			if (newSubTaskAction.isEnabled()) {
				toolBarManager.add(newSubTaskAction);
			}
		}
	}

	@Override
	public TaskEditor getEditor() {
		return (TaskEditor) super.getEditor();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		this.textSupport = new CommonTextSupport((IHandlerService) getSite().getService(IHandlerService.class));
		this.textSupport.setSelectionChangedListener((TaskEditorActionContributor) getEditorSite().getActionBarContributor());
	}

	private void initializePart(final Composite editorComposite, final AbstractLocalEditorPart part) {
		part.initialize(getManagedForm(), repository, task);
		Control control = part.createControl(editorComposite, toolkit);
		part.setControl(control);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
		getManagedForm().addPart(part);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void refresh() {
		getEditor().updateHeaderToolBar();
		IFormPart[] parts = getManagedForm().getParts();
		// refresh will not be invoked unless parts are stale
		for (IFormPart part : parts) {
			if (part instanceof AbstractLocalEditorPart) {
				((AbstractLocalEditorPart) part).markStale();
			}
		}
		getManagedForm().refresh();
	}

	public void fillLeftHeaderToolBar(IToolBarManager toolBarManager) {
		ControlContribution submitButtonContribution = new ControlContribution("org.eclipse.mylyn.tasks.toolbars.save") { //$NON-NLS-1$
			@Override
			protected Control createControl(Composite parent) {
				saveButton = new Button(parent, SWT.FLAT);
				saveButton.setText(Messages.TaskPlanningEditor_Save);
				saveButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
				saveButton.setBackground(null);
				saveButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						doSave(new NullProgressMonitor());
					}
				});
				return saveButton;
			}
		};
		toolBarManager.add(submitButtonContribution);
	}

}
