/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.SubmitTaskDataJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.core.data.AttributeManager;
import org.eclipse.mylyn.tasks.core.data.IAttributeManagerListener;
import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
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
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 * @author Xiaoyang Guan (Wiki HTML preview)
 */
// TODO EDITOR selection service
// TODO EDITOR outline
public abstract class AbstractTaskEditorPage extends FormPage {

	private class SubmitTaskJobListener extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {
			final SubmitTaskDataJob job = (SubmitTaskDataJob) event.getJob();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (job.getError() == null) {
						AbstractTask task = job.getTask();
						if (task != null) {
							updateTask(task);
						}

						refreshFormContent();
					} else {
						handleSubmitError(job.getError());
					}

					showEditorBusy(false);
				}
			});
		}

		private void handleSubmitError(IStatus status) {
			if (form != null && !form.isDisposed()) {
				if (status.getCode() == RepositoryStatus.ERROR_IO) {
					getParentEditor().setMessage(ERROR_NOCONNECTIVITY, IMessageProvider.ERROR);
					StatusHandler.log(status);
				} else if (status.getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
					StatusHandler.displayStatus("Comment required", status);
					if (!getManagedForm().getForm().isDisposed() && newCommentPart != null) {
						newCommentPart.setFocus();
					}
				} else if (status.getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					if (TasksUiUtil.openEditRepositoryWizard(taskRepository) == Window.OK) {
						submitToRepository();
						return;
					}
				} else {
					StatusHandler.displayStatus("Submit failed", status);
				}
			}
		}

		private void updateTask(AbstractTask task) {
			TasksUi.getTaskListManager().getTaskList().addTask(task, actionPart.getCategory());

			AbstractTaskEditorPage.this.task = task;

			// FIXME refresh editor
		}

	}

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	private static final String LABEL_HISTORY = "History";

	private static final Font TITLE_FONT = JFaceResources.getBannerFont();

	private TaskEditorActionPart actionPart;

	private ToggleTaskActivationAction activateAction;

	private AttributeManager attributeManager;

	private Action clearOutgoingAction;

	private TaskEditorCommentPart commentPart;

	private AbstractRepositoryConnector connector;

	private final String connectorKind;

	private TaskEditorDescriptionPart descriptionPart;

	private Composite editorComposite;

	private boolean expandedStateAttributes;

	// once the following bug is fixed, this check for first focus is probably
	// not needed -> Bug# 172033: Restore editor focus
	private boolean firstFocus = true;

	private ScrolledForm form;

	private boolean formBusy;

	private Action historyAction;

	private boolean needsAttachments;

	private boolean needsComments;

	private boolean needsHeader;

	private boolean needsPlanning;

	private TaskEditorRichTextPart newCommentPart;

	private NewSubTaskAction newSubTaskAction;

	private Action openBrowserAction;

	private RepositoryTaskOutlinePage outlinePage;

	private TaskEditorPlanningPart planningPart;

	private boolean reflow = true;

	private TaskEditorSummaryPart summaryPart;

	private SynchronizeEditorAction synchronizeEditorAction;

	private AbstractTask task;

	private TaskData taskData;

	private final ITaskListChangeListener taskListChangeListener = new TaskListChangeAdapter() {
		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			AbstractTask taskToRefresh = null;
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (task != null && task.equals(taskContainerDelta.getContainer())) {
					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)) {
						taskToRefresh = (AbstractTask) taskContainerDelta.getContainer();
						break;
					}
				}
			}
			if (taskToRefresh != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
								|| task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
							getParentEditor().setMessage("Task has incoming changes", IMessageProvider.WARNING,
									new HyperlinkAdapter() {
										@Override
										public void linkActivated(HyperlinkEvent e) {
											refreshFormContent();
										}
									});

							// API EDITOR this needs to be tracked somewhere else
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

	private TaskRepository taskRepository;

	private FormToolkit toolkit;

	public AbstractTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.isNotNull(connectorKind);
		this.connectorKind = connectorKind;
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
					if (getParentEditor() != null) {
						getSite().getPage().closeEditor(getParentEditor(), false);
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
		TaskAttribute attribute = getAttributeManager().getTaskData().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (attribute != null) {
			descriptionPart = new TaskEditorDescriptionPart(attribute);
			initializePart(composite, descriptionPart);
		}
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		super.createFormContent(managedForm);

		try {
			reflow = false;

			form = managedForm.getForm();
			toolkit = managedForm.getToolkit();
			registerDropListener(form);

			try {
				form.setRedraw(false);
				editorComposite = form.getBody();
				GridLayout editorLayout = new GridLayout();
				editorComposite.setLayout(editorLayout);
				editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
				if (connectorUi == null) {
					getParentEditor().setMessage("The editor may not be fully loaded", IMessageProvider.INFORMATION,
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

				updateHeaderControls();
			} finally {
				form.setRedraw(true);
			}
		} finally {
			reflow = true;
		}

		form.reflow(true);
	}

	private void createNewCommentSection(Composite composite) {
		TaskAttribute attribute = getAttributeManager().getTaskData().getMappedAttribute(TaskAttribute.COMMENT_NEW);
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
	}

	private void createSummarySection(Composite composite) {
		summaryPart = new TaskEditorSummaryPart();
		summaryPart.setNeedsHeader(needsHeader());
		initializePart(composite, summaryPart);
		summaryPart.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void dispose() {
		TasksUi.getTaskListManager().getTaskList().removeChangeListener(taskListChangeListener);
		if (activateAction != null) {
			activateAction.dispose();
		}
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!isDirty()) {
			return;
		}

		getManagedForm().commit(true);

		try {
			attributeManager.save(monitor);
		} catch (CoreException e) {
			// FIXME
			e.printStackTrace();
		}

		if (task != null) {
			task.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
			TasksUi.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
		}

		getManagedForm().dirtyStateChanged();

		updateHeaderControls();
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Override for customizing the toolbar.
	 * 
	 * @since 2.1 (NOTE: likely to change for 3.0)
	 */
	protected void fillToolBar(IToolBarManager toolBarManager) {
		if ((taskData != null && !taskData.isNew()) || task != null) {
			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(this));
			toolBarManager.add(synchronizeEditorAction);
		}

		if (taskData != null && !taskData.isNew()) {
			if (task != null) {
				clearOutgoingAction = new ClearOutgoingAction(Collections.singletonList((AbstractTaskContainer) task));

				if (clearOutgoingAction.isEnabled()) {
					toolBarManager.add(clearOutgoingAction);
				}

				newSubTaskAction = new NewSubTaskAction();
				newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(task));
				if (newSubTaskAction.isEnabled()) {
					toolBarManager.add(newSubTaskAction);
				}
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
					historyAction.setToolTipText(LABEL_HISTORY);
					toolBarManager.add(historyAction);
				}
			}

			if (connector != null) {
				String taskUrl = connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskId());
				if (taskUrl == null && task != null && task.hasValidUrl()) {
					taskUrl = task.getUrl();
				}

				final String taskUrlToOpen = taskUrl;

				if (taskUrlToOpen != null) {
					openBrowserAction = new Action() {
						@Override
						public void run() {
							TasksUiUtil.openUrl(taskUrlToOpen);
						}
					};

					openBrowserAction.setImageDescriptor(TasksUiImages.BROWSER_OPEN_TASK);
					openBrowserAction.setToolTipText("Open with Web Browser");
					toolBarManager.add(openBrowserAction);
				}
			}
		}
	}

	protected abstract AttributeEditorFactory getAttributeEditorFactory();

	public abstract AttributeEditorToolkit getAttributeEditorToolkit();

	protected AttributeManager getAttributeManager() {
		return attributeManager;
	}

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

	public RepositoryTaskOutlinePage getOutline() {
		return outlinePage;
	}

	public TaskEditor getParentEditor() {
		return (TaskEditor) getEditor();
	}

	public AbstractTask getTask() {
		return task;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		TaskEditorInput taskEditorInput = (TaskEditorInput) input;
		task = taskEditorInput.getTask();

		try {
			ITaskDataState taskDataState = TasksUi.getTaskDataManager().createWorkingCopy(task, getConnectorKind());
			attributeManager = new AttributeManager(taskDataState);
		} catch (CoreException e) {
			// FIXME
			e.printStackTrace();
		}

		taskRepository = TasksUi.getRepositoryManager().getRepository(getConnectorKind(),
				attributeManager.getTaskData().getRepositoryUrl());
		connector = TasksUi.getRepositoryManager().getRepositoryConnector(getConnectorKind());

		attributeManager.addAttributeManagerListener(new IAttributeManagerListener() {
			public void attributeChanged(TaskAttribute attribute) {
				getManagedForm().dirtyStateChanged();
			}
		});

		refreshInput();

		TasksUi.getTaskListManager().getTaskList().addChangeListener(taskListChangeListener);
	}

	private void initializePart(Composite parent, AbstractTaskEditorPart part) {
		getManagedForm().addPart(part);
		part.initialize(this);
		part.createControl(parent, toolkit);
		part.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public boolean isDirty() {
		return (getAttributeManager() != null && getAttributeManager().isDirty())
				|| (getManagedForm() != null && getManagedForm().isDirty());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
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

			updateHeaderControls();

			if (taskData != null) {
				// save menu
				Menu menu = editorComposite.getMenu();
				setMenu(editorComposite, null);

				// clear old controls
				for (Control control : editorComposite.getChildren()) {
					control.dispose();
				}

				// restore menu
				editorComposite.setMenu(menu);

				createSections();

				getParentEditor().setMessage(null, 0);
				getParentEditor().setActivePage(getId());

				if (actionPart != null) {
					actionPart.setSubmitEnabled(true);
				}
			}

			getManagedForm().dirtyStateChanged();
		} finally {
			showEditorBusy(false);
		}
	}

	private void refreshInput() {
		try {
			attributeManager.refresh(null);
		} catch (CoreException e) {
			// FIXME
			e.printStackTrace();
		}

		taskData = attributeManager.getTaskData();

		needsComments = !taskData.isNew();
		needsAttachments = !taskData.isNew();
		needsHeader = !taskData.isNew();
		needsPlanning = taskData.isNew();
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

	/**
	 * force a re-layout of entire form
	 */
	protected void resetLayout() {
		if (reflow) {
			form.layout(true, true);
			form.reflow(true);
		}
	}

	public void setExpandAttributeSection(boolean expandAttributeSection) {
		this.expandedStateAttributes = expandAttributeSection;
	}

	@Override
	public void setFocus() {
		if (summaryPart != null) {
			if (firstFocus) {
				summaryPart.setFocus();
				firstFocus = false;
			}
		} else {
			form.setFocus();
		}
	}

	public void showEditorBusy(boolean busy) {
		getParentEditor().showBusy(busy);
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

	public void setReflow(boolean refreshEnabled) {
		this.reflow = refreshEnabled;
		form.setRedraw(reflow);
	}

	@Override
	public void showBusy(boolean busy) {
		if (!getManagedForm().getForm().isDisposed() && busy != formBusy) {
			// parentEditor.showBusy(busy);
			if (synchronizeEditorAction != null) {
				synchronizeEditorAction.setEnabled(!busy);
			}

			if (activateAction != null) {
				activateAction.setEnabled(!busy);
			}

			if (openBrowserAction != null) {
				openBrowserAction.setEnabled(!busy);
			}

			if (historyAction != null) {
				historyAction.setEnabled(!busy);
			}

			if (actionPart != null) {
				actionPart.setSubmitEnabled(!busy);
			}

			if (newSubTaskAction != null) {
				newSubTaskAction.setEnabled(!busy);
			}

			if (clearOutgoingAction != null) {
				clearOutgoingAction.setEnabled(!busy);
			}

			EditorUtil.setEnabledState(editorComposite, !busy);

			formBusy = busy;
		}
	}

	public void submitToRepository() {
		showEditorBusy(true);

		doSave(new NullProgressMonitor());

		SubmitTaskDataJob submitJob = new SubmitTaskDataJob(connector, taskRepository, taskData);
		submitJob.setAttachContext(actionPart.getAttachContext());
		submitJob.addJobChangeListener(new SubmitTaskJobListener());
		submitJob.schedule();
	}

	protected boolean supportsRefreshAttributes() {
		return true;
	}

	private void updateHeaderControls() {
		if (taskData == null) {
			getParentEditor().setMessage(
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

		ControlContribution repositoryLabelControl = new ControlContribution("Title") { //$NON-NLS-1$
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
				link.setFont(TITLE_FONT);
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

		if (getParentEditor().getTopForm() != null) {
			IToolBarManager toolBarManager = getParentEditor().getTopForm().getToolBarManager();

			// TODO: Remove? Added to debug bug#197355
			toolBarManager.removeAll();
			toolBarManager.update(true);

			toolBarManager.add(repositoryLabelControl);
			fillToolBar(getParentEditor().getTopForm().getToolBarManager());

			if (task != null && taskData != null && !taskData.isNew()) {
				activateAction = new ToggleTaskActivationAction(task, toolBarManager);
				toolBarManager.add(new Separator("activation"));
				toolBarManager.add(activateAction);
			}

			toolBarManager.update(true);
		}
	}

}
