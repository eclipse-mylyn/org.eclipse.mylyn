/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPlanningPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorRichTextPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Extend to provide customized task editing.
 * 
 * NOTE: This class is work in progress
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
// TODO EDITOR selection service
// TODO EDITOR outline
public abstract class AbstractTaskEditorPage extends FormPage implements ISelectionProvider, ISelectionChangedListener {

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.page";

	private class SubmitTaskJobListener extends SubmitJobListener {

		private final boolean attachContext;

		public SubmitTaskJobListener(boolean attachContext) {
			this.attachContext = attachContext;
		}

		@Override
		public void done(SubmitJobEvent event) {
			final SubmitJob job = event.getJob();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				private void openNewTask(ITask newTask) {
					AbstractTaskContainer parent = null;
					if (actionPart != null) {
						parent = actionPart.getCategory();
					}
					// TODO copy context and scheduling
					TasksUiInternal.getTaskList().addTask(newTask, parent);
					close();
					TasksUiInternal.getTaskList().deleteTask(getTask());
					TasksUiUtil.openTaskInBackground(newTask, false);
				}

				public void run() {
					if (job.getError() == null) {
						if (job.getTask().equals(getTask())) {
							refreshFormContent();
						} else {
							openNewTask(job.getTask());
						}
					} else {
						handleSubmitError(job);
					}

					showEditorBusy(false);
				}
			});
		}

		@Override
		public void taskSubmitted(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
			// attach context if required
			if (attachContext) {
				AttachmentUtil.postContext(connector, getModel().getTaskRepository(), task, "", monitor);
			}
		}

		@Override
		public void taskSynchronized(SubmitJobEvent event, IProgressMonitor monitor) {
		}

	}

	private class TaskListChangeListener extends TaskListChangeAdapter {
		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			ITask taskToRefresh = null;
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (task.equals(taskContainerDelta.getContainer())) {
					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)) {
						taskToRefresh = (ITask) taskContainerDelta.getContainer();
						break;
					}
				}
			}
			if (taskToRefresh != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (task.getSynchronizationState() == SynchronizationState.INCOMING
								|| task.getSynchronizationState() == SynchronizationState.CONFLICT) {
							getTaskEditor().setMessage("Task has incoming changes", IMessageProvider.WARNING,
									new HyperlinkAdapter() {
										@Override
										public void linkActivated(HyperlinkEvent e) {
											refreshFormContent();
										}
									});

							// TODO EDITOR this needs to be tracked somewhere else
							if (actionPart != null) {
								actionPart.setSubmitEnabled(false);
							}
						} else {
							refreshFormContent();
						}
					}
				});
			}
		}
	};

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	private TaskEditorActionPart actionPart;

	private Action clearOutgoingAction;

	private TaskEditorCommentPart commentPart;

	private AbstractRepositoryConnector connector;

	private final String connectorKind;

	private TaskEditorDescriptionPart descriptionPart;

	private Composite editorComposite;

	private boolean expandedStateAttributes;

	private ScrolledForm form;

	private boolean formBusy;

	private Action historyAction;

	protected Control lastFocusControl;

	private ISelection lastSelection;

	private TaskDataModel model;

	private boolean needsAddToCategory;

	private boolean needsAttachments;

	private boolean needsComments;

	private boolean needsHeader;

	private boolean needsPlanning;

	private TaskEditorRichTextPart newCommentPart;

	private NewSubTaskAction newSubTaskAction;

	private Action openBrowserAction;

	private TaskEditorPlanningPart planningPart;

	private boolean reflow;

	private final ListenerList selectionChangedListeners = new ListenerList();

	private TaskEditorSummaryPart summaryPart;

	private SynchronizeEditorAction synchronizeEditorAction;

	private ITask task;

	private TaskData taskData;

	private ITaskListChangeListener taskListChangeListener;

	private FormToolkit toolkit;

	private MenuManager menuManager;

	public AbstractTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, "id", "label");
		Assert.isNotNull(connectorKind);
		this.connectorKind = connectorKind;
		this.reflow = true;
	}

	private void addFocusListener(Composite composite, FocusListener listener) {
		Control[] children = composite.getChildren();
		for (Control control : children) {
			if ((control instanceof Text) || (control instanceof Button) || (control instanceof Combo)
					|| (control instanceof CCombo) || (control instanceof Tree) || (control instanceof Table)
					|| (control instanceof Spinner) || (control instanceof Link) || (control instanceof List)
					|| (control instanceof TabFolder) || (control instanceof CTabFolder)
					|| (control instanceof Hyperlink) || (control instanceof FilteredTree)
					|| (control instanceof StyledText)) {
				control.addFocusListener(listener);
			}
			if (control instanceof Composite) {
				addFocusListener((Composite) control, listener);
			}
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	public void appendTextToNewComment(String text) {
		newCommentPart.appendText(text);
		newCommentPart.setFocus();
	}

	public void close() {
		Display activeDisplay = getSite().getShell().getDisplay();
		activeDisplay.asyncExec(new Runnable() {
			public void run() {
				if (getSite() != null && getSite().getPage() != null && !getManagedForm().getForm().isDisposed()) {
					if (getTaskEditor() != null) {
						getSite().getPage().closeEditor(getTaskEditor(), false);
					} else {
						getSite().getPage().closeEditor(AbstractTaskEditorPage.this, false);
					}
				}
			}
		});
	}

	/**
	 * Creates the button layout. This displays options and buttons at the bottom of the editor to allow actions to be
	 * performed on the bug.
	 */
	private void createActionsSection(Composite composite) {
		actionPart = new TaskEditorActionPart();
		actionPart.setNeedsAddToCategory(needsAddToCategory);
		initializePart(composite, actionPart);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(actionPart.getControl());
	}

	private void createAttachmentSection(Composite composite) {
		TaskEditorAttachmentPart attachmentPart = new TaskEditorAttachmentPart();
		initializePart(composite, attachmentPart);
	}

	private void createAttributeSection(Composite composite) {
		TaskEditorAttributePart attributePart = new TaskEditorAttributePart();
		attributePart.setExpandOnCreation(expandedStateAttributes);
		initializePart(composite, attributePart);
	}

	private void createCommentSection(Composite composite) {
		commentPart = new TaskEditorCommentPart();
		initializePart(composite, commentPart);
	}

	private void createDescriptionSection(Composite composite) {
		TaskAttribute attribute = getModel().getTaskData().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (attribute != null) {
			descriptionPart = new TaskEditorDescriptionPart(attribute);
			initializePart(composite, descriptionPart);
		}
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		registerDropListener(form);

		try {
			setReflow(false);

			editorComposite = form.getBody();
			GridLayout editorLayout = new GridLayout();
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(getConnectorKind());
			if (connectorUi == null) {
				getTaskEditor().setMessage("The editor may not be fully loaded", IMessageProvider.INFORMATION,
						new HyperlinkAdapter() {
							@Override
							public void linkActivated(HyperlinkEvent e) {
								refreshFormContent();
							}
						});
			}

			if (taskData != null) {
				createSections();
			}
			updateHeaderMessage();
		} finally {
			setReflow(true);
		}
		reflow();
	}

	protected TaskDataModel createModel(TaskEditorInput input) throws CoreException {
		ITaskDataWorkingCopy taskDataState = TasksUi.getTaskDataManager().getWorkingCopy(task, getConnectorKind());
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(getConnectorKind(),
				taskDataState.getRepositoryUrl());
		return new TaskDataModel(taskRepository, input.getTask(), taskDataState);
	}

	private void createNewCommentSection(Composite composite) {
		TaskAttribute attribute = getModel().getTaskData().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		if (attribute != null) {
			newCommentPart = new TaskEditorRichTextPart(attribute);
			newCommentPart.setPartName("New Comment");
			initializePart(composite, newCommentPart);
			newCommentPart.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}

	private void createPeopleSection(Composite composite) {
		TaskEditorPeoplePart peoplePart = new TaskEditorPeoplePart();
		initializePart(composite, peoplePart);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(peoplePart.getControl());
	}

	private void createPlanningSection(Composite composite) {
		planningPart = new TaskEditorPlanningPart();
		initializePart(composite, planningPart);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(planningPart.getControl());
	}

	private void createSections() {
		createSummarySection(editorComposite);

		createAttributeSection(editorComposite);

		if (needsAttachments()) {
			createAttachmentSection(editorComposite);
		}

		createDescriptionSection(editorComposite);

		if (needsComments()) {
			createCommentSection(editorComposite);
			createNewCommentSection(editorComposite);
		}

		if (needsPlanning()) {
			createPlanningSection(editorComposite);
		}

		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);

		createActionsSection(bottomComposite);
		createPeopleSection(bottomComposite);

		bottomComposite.pack(true);

		FocusListener listener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lastFocusControl = (Control) e.widget;
			}
		};
		addFocusListener(editorComposite, listener);
		if (summaryPart != null) {
			lastFocusControl = summaryPart.getControl();
		}

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, this, true);

		editorComposite.setMenu(menuManager.createContextMenu(editorComposite));
	}

	private void createSummarySection(Composite composite) {
		summaryPart = new TaskEditorSummaryPart();
		summaryPart.setNeedsHeader(needsHeader());
		initializePart(composite, summaryPart);
		summaryPart.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
		}
		TasksUiInternal.getTaskList().removeChangeListener(taskListChangeListener);
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!isDirty()) {
			return;
		}

		getManagedForm().commit(true);

		try {
			model.save(monitor);
		} catch (final CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error saving task", e));
			getTaskEditor().setMessage("Could not save task", IMessageProvider.ERROR, new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent event) {
					TasksUiInternal.displayStatus("Save failed", e.getStatus());
				}
			});
		}

		updateHeaderMessage();
		getManagedForm().dirtyStateChanged();
		getTaskEditor().updateHeaderToolBar();
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	public void doSubmit() {
		showEditorBusy(true);

		doSave(new NullProgressMonitor());

		SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector,
				getModel().getTaskRepository(), task, getModel().getTaskData(), getModel().getChangedAttributes());
		submitJob.addSubmitJobListener(new SubmitTaskJobListener(actionPart.getAttachContext()));
		submitJob.schedule();
	}

	/**
	 * Override for customizing the toolbar.
	 */
	public void fillToolBar(IToolBarManager toolBarManager) {
		final TaskRepository taskRepository = (model != null) ? getModel().getTaskRepository() : null;
		if (taskRepository != null) {
			ControlContribution repositoryLabelControl = new ControlContribution("Title") {
				@Override
				protected Control createControl(Composite parent) {
					Composite composite = toolkit.createComposite(parent);
					composite.setLayout(new RowLayout());
					composite.setBackground(null);
					String label = taskRepository.getRepositoryLabel();
					if (label.indexOf("//") != -1) {
						label = label.substring((taskRepository.getRepositoryUrl().indexOf("//") + 2));
					}

					Hyperlink link = new Hyperlink(composite, SWT.NONE);
					link.setText(label);
					link.setFont(JFaceResources.getBannerFont());
					link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
					link.addHyperlinkListener(new HyperlinkAdapter() {

						@Override
						public void linkActivated(HyperlinkEvent e) {
							TasksUiUtil.openEditRepositoryWizard(taskRepository);
						}
					});

					return composite;
				}
			};
			toolBarManager.add(repositoryLabelControl);
		}

		synchronizeEditorAction = new SynchronizeEditorAction();
		synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
		toolBarManager.add(synchronizeEditorAction);

		if (taskRepository != null && !taskData.isNew()) {
			clearOutgoingAction = new ClearOutgoingAction(Collections.singletonList((ITaskElement) task));
			if (clearOutgoingAction.isEnabled()) {
				toolBarManager.add(clearOutgoingAction);
			}

			newSubTaskAction = new NewSubTaskAction();
			newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(task));
			if (newSubTaskAction.isEnabled()) {
				toolBarManager.add(newSubTaskAction);
			}

			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskData.getConnectorKind());
			if (connectorUi != null) {
				final String historyUrl = connectorUi.getTaskHistoryUrl(taskRepository, task);
				if (historyUrl != null) {
					historyAction = new Action() {
						@Override
						public void run() {
							TasksUiUtil.openUrl(historyUrl);
						}
					};

					historyAction.setImageDescriptor(TasksUiImages.TASK_REPOSITORY_HISTORY);
					historyAction.setToolTipText("History");
					toolBarManager.add(historyAction);
				}
			}

			final String taskUrlToOpen = task.getUrl();
			if (taskUrlToOpen != null) {
				openBrowserAction = new Action() {
					@Override
					public void run() {
						TasksUiUtil.openUrl(taskUrlToOpen);
					}
				};

				openBrowserAction.setImageDescriptor(CommonImages.BROWSER_OPEN_TASK);
				openBrowserAction.setToolTipText("Open with Web Browser");
				toolBarManager.add(openBrowserAction);
			}
		}
	}

	protected void fireSelectionChanged(ISelection selection) {
		// create an event
		final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);

		// fire the event
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	public abstract AttributeEditorFactory getAttributeEditorFactory();

	public abstract AttributeEditorToolkit getAttributeEditorToolkit();

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	/**
	 * @return The composite for the whole editor.
	 */
	public Composite getEditorComposite() {
		return editorComposite;
	}

	public TaskDataModel getModel() {
		return model;
	}

	public ISelection getSelection() {
		return lastSelection;
	}

	public ITask getTask() {
		return task;
	}

	public TaskEditor getTaskEditor() {
		return (TaskEditor) getEditor();
	}

	public TaskRepository getTaskRepository() {
		return getModel().getTaskRepository();
	}

	private void handleSubmitError(SubmitJob job) {
		if (form != null && !form.isDisposed()) {
			final IStatus status = job.getError();
			if (status.getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
				TasksUiInternal.displayStatus("Comment required", status);
				if (newCommentPart != null) {
					newCommentPart.setFocus();
				}
			} else if (status.getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
				if (TasksUiUtil.openEditRepositoryWizard(getTaskRepository()) == Window.OK) {
					doSubmit();
				}
			} else {
				String message;
				if (status.getCode() == RepositoryStatus.ERROR_IO) {
					message = ERROR_NOCONNECTIVITY;
				} else if (status.getMessage().length() > 0) {
					message = "Submit failed: " + status.getMessage();
				} else {
					message = "Submit failed";
				}
				getTaskEditor().setMessage(message, IMessageProvider.ERROR, new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						TasksUiInternal.displayStatus("Submit failed", status);
					}
				});
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		site.setSelectionProvider(this);

		TaskEditorInput taskEditorInput = (TaskEditorInput) input;
		this.task = taskEditorInput.getTask();
		try {
			setModel(createModel(taskEditorInput));
		} catch (final CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error opening task", e));
			getTaskEditor().setStatus("Error opening task", "Open failed", e.getStatus());
		}

		taskListChangeListener = new TaskListChangeListener();
		TasksUiInternal.getTaskList().addChangeListener(taskListChangeListener);
	}

	private void initializePart(Composite parent, AbstractTaskEditorPart part) {
		getManagedForm().addPart(part);
		part.initialize(this);
		part.createControl(parent, toolkit);
		part.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public boolean isDirty() {
		return (getModel() != null && getModel().isDirty()) || (getManagedForm() != null && getManagedForm().isDirty());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean needsAddToCategory() {
		return needsAddToCategory;
	}

	public boolean needsAttachments() {
		return needsAttachments;
	}

	public boolean needsComments() {
		return needsComments;
	}

	public boolean needsHeader() {
		return needsHeader;
	}

	public boolean needsPlanning() {
		return needsPlanning;
	}

	/**
	 * force a re-layout of entire form
	 */
	public void reflow() {
		if (reflow) {
			form.layout(true, true);
			form.reflow(true);
		}
	}

	/**
	 * Update editor contents in place.
	 */
	public void refreshFormContent() {
		if (getManagedForm().getForm().isDisposed()) {
			// editor possibly closed as part of submit
			return;
		}

		try {
			showEditorBusy(true);

			doSave(new NullProgressMonitor());
			refreshInput();

			if (taskData != null) {
				try {
					setReflow(false);
					// save menu
					Menu menu = editorComposite.getMenu();
					setMenu(editorComposite, null);

					// clear old controls
					for (Control control : editorComposite.getChildren()) {
						control.dispose();
					}
					lastFocusControl = null;
					lastSelection = null;

					// restore menu
					editorComposite.setMenu(menu);

					createSections();

					getTaskEditor().setMessage(null, 0);
					getTaskEditor().setActivePage(getId());

					if (actionPart != null) {
						actionPart.setSubmitEnabled(true);
					}
				} finally {
					setReflow(true);
				}
			}

			updateHeaderMessage();
			getManagedForm().dirtyStateChanged();
			getTaskEditor().updateHeaderToolBar();
		} finally {
			showEditorBusy(false);
		}
		reflow();
	}

	private void refreshInput() {
		try {
			model.refresh(null);
		} catch (CoreException e) {
			getTaskEditor().setMessage("Failed to read task data: " + e.getMessage(), IMessageProvider.ERROR);
			taskData = null;
			return;
		}

		setTaskData(model.getTaskData());
	}

	private void registerDropListener(final Control control) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);

		// Adapted from eclipse.org DND Article by Veronika Irvine, IBM OTI Labs
		// http://www.eclipse.org/articles/Article-SWT-DND/DND-in-SWT.html#_dt10D
		// TODO EDITOR
		//target.addDropListener(new RepositoryTaskEditorDropListener(this, fileTransfer, textTransfer, control));
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		lastSelection = event.getSelection();
		fireSelectionChanged(lastSelection);
	}

	public void setExpandAttributeSection(boolean expandAttributeSection) {
		this.expandedStateAttributes = expandAttributeSection;
	}

	@Override
	public void setFocus() {
		if (lastFocusControl != null && !lastFocusControl.isDisposed()) {
			lastFocusControl.setFocus();
		}
	}

	/**
	 * Used to prevent form menu from being disposed when disposing elements on the form during refresh
	 */
	private void setMenu(Composite comp, Menu menu) {
		if (!comp.isDisposed()) {
			comp.setMenu(null);
			for (Control child : comp.getChildren()) {
				child.setMenu(null);
				if (child instanceof Composite) {
					setMenu((Composite) child, menu);
				}
			}
		}
	}

	private void setModel(TaskDataModel model) {
		Assert.isNotNull(model);
		this.model = model;
		this.connector = TasksUi.getRepositoryManager().getRepositoryConnector(getConnectorKind());
		setTaskData(model.getTaskData());
		model.addModelListener(new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				getManagedForm().dirtyStateChanged();
			}
		});
	}

	public void setReflow(boolean reflow) {
		this.reflow = reflow;
		form.setRedraw(reflow);
	}

	public void setSelection(ISelection selection) {
		IFormPart[] parts = getManagedForm().getParts();
		for (IFormPart formPart : parts) {
			if (formPart instanceof AbstractTaskEditorPart) {
				if (((AbstractTaskEditorPart) formPart).setSelection(selection)) {
					lastSelection = selection;
					return;
				}
			}
		}
	}

	private void setTaskData(TaskData taskData) {
		this.taskData = taskData;

		needsComments = !taskData.isNew();
		needsAttachments = !taskData.isNew();
		needsHeader = !taskData.isNew();
		needsPlanning = taskData.isNew();
		needsAddToCategory = taskData.isNew();
	}

	@Override
	public void showBusy(boolean busy) {
		if (!getManagedForm().getForm().isDisposed() && busy != formBusy) {
			// parentEditor.showBusy(busy);
//			if (synchronizeEditorAction != null) {
//				synchronizeEditorAction.setEnabled(!busy);
//			}
//
//			if (openBrowserAction != null) {
//				openBrowserAction.setEnabled(!busy);
//			}
//
//			if (historyAction != null) {
//				historyAction.setEnabled(!busy);
//			}
//
//			if (actionPart != null) {
//				actionPart.setSubmitEnabled(!busy);
//			}
//
//			if (newSubTaskAction != null) {
//				newSubTaskAction.setEnabled(!busy);
//			}
//
//			if (clearOutgoingAction != null) {
//				clearOutgoingAction.setEnabled(!busy);
//			}

			EditorUtil.setEnabledState(editorComposite, !busy);

			formBusy = busy;
		}
	}

	public void showEditorBusy(boolean busy) {
		getTaskEditor().showBusy(busy);
	}

	protected boolean supportsRefreshAttributes() {
		return true;
	}

	private void updateHeaderMessage() {
		if (taskData == null) {
			getTaskEditor().setMessage(
					"Task data not available. Press synchronize button (right) to retrieve latest data.",
					IMessageProvider.WARNING, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent e) {
							if (synchronizeEditorAction != null) {
								synchronizeEditorAction.run();
							}
						}
					});
		}
	}
}
