/*******************************************************************************
 * Copyright (c) 2004, 2011 Willian Mitsuda and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Tomasz Zarna, IBM Corporation - improvements for bug 261648
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.ITaskCommandIds;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Willian Mitsuda
 */
public class RemoteTaskSelectionDialog extends SelectionStatusDialog {

	public RemoteTaskSelectionDialog(Shell parent) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		setStatusLineAboveButtons(true);
	}

	private Text idText;

	private TableViewer tasksViewer;

	private ComboViewer repositoriesViewer;

	private Button addToTaskListCheck;

	private ComboViewer categoryViewer;

	// TODO: copy'n pasted code; make API?
	private List<TaskRepository> getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<>();
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			Set<TaskRepository> connectorRepositories = repositoryManager.getRepositories(connector.getConnectorKind());
			for (TaskRepository repository : connectorRepositories) {
				if (ITaskRepositoryFilter.CAN_CREATE_TASK_FROM_KEY.accept(repository, connector)) {
					repositories.add(repository);
				}
			}
		}
		return repositories;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Label idLabel = new Label(area, SWT.NULL);
		idLabel.setText(Messages.RemoteTaskSelectionDialog_Enter_Key_ID__use_comma_for_multiple_);
		idText = new Text(area, SWT.BORDER);
		idText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label matchingTasksLabel = new Label(area, SWT.NONE);
		matchingTasksLabel.setText(Messages.RemoteTaskSelectionDialog_Matching_tasks);
		tasksViewer = new TableViewer(area, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tasksViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(400, 400).create());
		tasksViewer.setLabelProvider(new DecoratingLabelProvider(new TaskElementLabelProvider(true),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		tasksViewer.setContentProvider(ArrayContentProvider.getInstance());
		tasksViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				// Only shows exact task matches
				if ((selectedIds == null) || !(element instanceof ITask task)) {
					return false;
				}
				String taskId = task.getTaskKey();
				for (String id : selectedIds) {
					if (id.equals(taskId)) {
						return true;
					}
				}
				return false;
			}

		});
		tasksViewer.setInput(TasksUiPlugin.getTaskList().getAllTasks());
		idText.addModifyListener(e -> {
			computeIds();
			validate();
			tasksViewer.refresh(false);
		});
		tasksViewer.addSelectionChangedListener(event -> validate());
		tasksViewer.addOpenListener(event -> {
			if (getOkButton().getEnabled()) {
				okPressed();
			}
		});
		Table table = tasksViewer.getTable();
		table.showSelection();

		Composite repositoriesComposite = new Composite(area, SWT.NONE);
		repositoriesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		repositoriesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		Label repositoriesLabel = new Label(repositoriesComposite, SWT.NONE);
		repositoriesLabel.setText(Messages.RemoteTaskSelectionDialog_Select_a_task_repository);

		repositoriesViewer = new ComboViewer(repositoriesComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		repositoriesViewer.setLabelProvider(new TaskRepositoryLabelProvider());
		repositoriesViewer.setContentProvider(ArrayContentProvider.getInstance());
		List<TaskRepository> taskRepositories = getTaskRepositories();
		repositoriesViewer.setInput(taskRepositories);
		if (taskRepositories.size() == 1) {
			repositoriesViewer.setSelection(new StructuredSelection(taskRepositories.get(0)));
		}
		TaskRepository currentRepository = TasksUiUtil.getSelectedRepository(null);
		if (currentRepository != null) {
			repositoriesViewer.setSelection(new StructuredSelection(currentRepository), true);
		}
		repositoriesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		repositoriesViewer.addSelectionChangedListener(event -> {
			tasksViewer.setSelection(StructuredSelection.EMPTY);
			validate();
		});

		Button addRepositoryButton = new Button(repositoriesComposite, SWT.NONE);
		addRepositoryButton.setText(Messages.RemoteTaskSelectionDialog_Add_);
		addRepositoryButton.setEnabled(TasksUiPlugin.getRepositoryManager().hasUserManagedRepositoryConnectors());
		addRepositoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IHandlerService hndSvc = PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					hndSvc.executeCommand(ITaskCommandIds.ADD_TASK_REPOSITORY, null);
					repositoriesViewer.setInput(getTaskRepositories());
				} catch (CommandException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
				}
			}
		});

		Composite addToTaskListComposite = new Composite(area, SWT.NONE);
		addToTaskListComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		addToTaskListCheck = new Button(addToTaskListComposite, SWT.CHECK);
		addToTaskListCheck.setText(Messages.RemoteTaskSelectionDialog_Add_to_Task_List_category);

		categoryViewer = new ComboViewer(addToTaskListComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		categoryViewer.setContentProvider(ArrayContentProvider.getInstance());
		TaskList taskList = TasksUiPlugin.getTaskList();
		LinkedList<AbstractTaskContainer> categories = new LinkedList<>(taskList.getCategories());
		categories.addFirst(taskList.getDefaultCategory());
		categoryViewer.setInput(categories);
		categoryViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IRepositoryElement) {
					return ((IRepositoryElement) element).getSummary();
				}
				return super.getText(element);
			}

		});
		categoryViewer.setSelection(new StructuredSelection(taskList.getDefaultCategory()));

		categoryViewer.getControl().setEnabled(addToTaskListCheck.getSelection());
		addToTaskListCheck.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				categoryViewer.getControl().setEnabled(addToTaskListCheck.getSelection());
			}

		});

		idText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					tasksViewer.getControl().setFocus();
				}
			}

		});

		validate();

		Dialog.applyDialogFont(area);
		return area;
	}

	private void validate() {
		if (idText.getText().trim().equals("")) { //$NON-NLS-1$
			updateStatus(new Status(IStatus.INFO, TasksUiPlugin.ID_PLUGIN, 0,
					Messages.RemoteTaskSelectionDialog_Enter_a_valid_task_ID, null));
			return;
		}
		if (tasksViewer.getSelection().isEmpty() && repositoriesViewer.getSelection().isEmpty()) {
			updateStatus(new Status(IStatus.INFO, TasksUiPlugin.ID_PLUGIN, 0,
					Messages.RemoteTaskSelectionDialog_Select_a_task_or_repository, null));
			return;
		}
		updateStatus(new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, 0, "", null)); //$NON-NLS-1$
	}

	@Override
	protected void updateStatus(IStatus status) {
		super.updateStatus(status);

		// support disabling button for non-error statuses
		Button okButton = getOkButton();
		if (okButton != null && !okButton.isDisposed()) {
			okButton.setEnabled(status.isOK());
		}
	}

	private String[] selectedIds;

	private TaskRepository selectedRepository;

	private AbstractTask selectedTask;

	private boolean shouldAddToTaskList;

	private AbstractTaskCategory selectedCategory;

	public String[] getSelectedIds() {
		return selectedIds;
	}

	public TaskRepository getSelectedTaskRepository() {
		return selectedRepository;
	}

	public AbstractTask getSelectedTask() {
		return selectedTask;
	}

	public boolean shouldAddToTaskList() {
		return shouldAddToTaskList;
	}

	public AbstractTaskCategory getSelectedCategory() {
		return selectedCategory;
	}

	@Override
	protected void computeResult() {
		computeIds();

		ISelection taskSelection = tasksViewer.getSelection();
		if (!taskSelection.isEmpty()) {
			selectedTask = (AbstractTask) ((IStructuredSelection) taskSelection).getFirstElement();
		} else {
			selectedRepository = (TaskRepository) ((IStructuredSelection) repositoriesViewer.getSelection())
					.getFirstElement();
		}
		shouldAddToTaskList = addToTaskListCheck.getSelection();
		if (shouldAddToTaskList) {
			selectedCategory = (AbstractTaskCategory) ((IStructuredSelection) categoryViewer.getSelection())
					.getFirstElement();
		}
	}

	private void computeIds() {
		selectedIds = idText.getText().split(","); //$NON-NLS-1$
		for (String id : selectedIds) {
			id = id.trim();
		}
	}

}
