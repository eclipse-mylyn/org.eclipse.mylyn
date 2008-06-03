/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextSelection;
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
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskAttachmentDropListener;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorNewCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineNode;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlinePage;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPlanningPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorRichTextPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Extend to provide a task editor page.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskEditorPage extends FormPage implements ISelectionProvider, ISelectionChangedListener {

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
					AbstractTaskEditorPart actionPart = getPart(ID_PART_ACTIONS);
					if (actionPart instanceof TaskEditorAttributePart) {
						parent = ((TaskEditorActionPart) actionPart).getCategory();
					}
					// TODO copy context and scheduling
					TasksUiInternal.getTaskList().addTask(newTask, parent);
					close();
					TasksUiInternal.getTaskList().deleteTask(getTask());
					TasksUiInternal.openTaskInBackground(newTask, false);
				}

				public void run() {
					try {
						if (job.getStatus() == null) {
							if (job.getTask().equals(getTask())) {
								refreshFormContent();
							} else {
								openNewTask(job.getTask());
							}
						} else {
							handleSubmitError(job);
						}
					} finally {
						showEditorBusy(false);
					}
				}
			});
		}

		@Override
		public void taskSubmitted(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
			if (attachContext) {
				AttachmentUtil.postContext(connector, getModel().getTaskRepository(), task, "", null, monitor);
			}
		}

		@Override
		public void taskSynchronized(SubmitJobEvent event, IProgressMonitor monitor) {
		}

	}

	private class TaskListChangeListener extends TaskListChangeAdapter {
		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			if (refreshDisabled) {
				return;
			}
			ITask taskToRefresh = null;
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (task.equals(taskContainerDelta.getElement())) {
					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)
							&& !taskContainerDelta.isTransient()) {
						taskToRefresh = (ITask) taskContainerDelta.getElement();
						break;
					}
				}
			}
			if (taskToRefresh != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!isDirty() && task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED) {
							// automatically refresh if the user has not made any changes and there is no chance of missing incomings
							refreshFormContent();
						} else {
							getTaskEditor().setMessage("Task has incoming changes", IMessageProvider.WARNING,
									new HyperlinkAdapter() {
										@Override
										public void linkActivated(HyperlinkEvent e) {
											refreshFormContent();
										}
									});
							setSubmitEnabled(false);
						}
					}
				});
			}
		}
	}

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	public static final String ID_PART_ACTIONS = "org.eclipse.mylyn.tasks.ui.editors.parts.actions";

	public static final String ID_PART_ATTACHMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.attachments";

	public static final String ID_PART_ATTRIBUTES = "org.eclipse.mylyn.tasks.ui.editors.parts.attributes";

	public static final String ID_PART_COMMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.comments";

	public static final String ID_PART_DESCRIPTION = "org.eclipse.mylyn.tasks.ui.editors.part.descriptions";

	public static final String ID_PART_NEW_COMMENT = "org.eclipse.mylyn.tasks.ui.editors.part.newComment";

	public static final String ID_PART_PEOPLE = "org.eclipse.mylyn.tasks.ui.editors.part.people";

	public static final String ID_PART_PLANNING = "org.eclipse.mylyn.tasks.ui.editors.part.planning";

	public static final String ID_PART_SUMMARY = "org.eclipse.mylyn.tasks.ui.editors.part.summary";

	public static final String PATH_ACTIONS = "actions";

	public static final String PATH_ATTACHMENTS = "attachments";

	public static final String PATH_ATTRIBUTES = "attributes";

	public static final String PATH_COMMENTS = "comments";

	public static final String PATH_HEADER = "header";

	public static final String PATH_PEOPLE = "people";

	public static final String PATH_PLANNING = "planning";

//	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.page";

	private AttributeEditorFactory attributeEditorFactory;

	private AttributeEditorToolkit attributeEditorToolkit;

	private Action clearOutgoingAction;

	private AbstractRepositoryConnector connector;

	private final String connectorKind;

	private StructuredSelection defaultSelection;

	private Composite editorComposite;

	private boolean expandAttributesSection;

	private ScrolledForm form;

	private boolean formBusy;

	private Action historyAction;

	protected Control lastFocusControl;

	private ISelection lastSelection;

	private TaskDataModel model;

	private boolean needsAddToCategory;

	private NewSubTaskAction newSubTaskAction;

	private Action openBrowserAction;

	private boolean reflow;

	private volatile boolean refreshDisabled;

	private final ListenerList selectionChangedListeners;

	private SynchronizeEditorAction synchronizeEditorAction;

	private ITask task;

	private TaskData taskData;

	private ITaskListChangeListener taskListChangeListener;

	private FormToolkit toolkit;

	private TaskEditorOutlinePage outlinePage;

	private TaskAttachmentDropListener defaultDropListener;

	public AbstractTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, "id", "label");
		Assert.isNotNull(connectorKind);
		this.connectorKind = connectorKind;
		this.reflow = true;
		this.selectionChangedListeners = new ListenerList();
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
		AbstractTaskEditorPart newCommentPart = getPart(ID_PART_NEW_COMMENT);
		if (newCommentPart instanceof TaskEditorRichTextPart) {
			((TaskEditorRichTextPart) newCommentPart).appendText(text);
			newCommentPart.setFocus();
		}
	}

	public boolean canPerformAction(String actionId) {
		return EditorUtil.canPerformAction(actionId, EditorUtil.getFocusControl(this));
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

	protected AttributeEditorFactory createAttributeEditorFactory() {
		return new AttributeEditorFactory(getModel(), getTaskRepository());
	}

	AttributeEditorToolkit createAttributeEditorToolkit() {
		IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
		return new AttributeEditorToolkit(handlerService);
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		registerDefaultDropListener(form);

		try {
			setReflow(false);

			editorComposite = form.getBody();
			GridLayout editorLayout = new GridLayout();
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

//			menuManager = new MenuManager();
//			menuManager.setRemoveAllWhenShown(true);
//			getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, this, true);
//			editorComposite.setMenu(menuManager.createContextMenu(editorComposite));
			editorComposite.setMenu(getTaskEditor().getMenu());

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
				createFormContentInternal();
			}
			updateHeaderMessage();
		} finally {
			setReflow(true);
		}
	}

	private void createFormContentInternal() {
		// end life-cycle of previous editor controls
		if (attributeEditorToolkit != null) {
			attributeEditorToolkit.dispose();
		}

		// start life-cycle of previous editor controls 
		if (attributeEditorFactory == null) {
			attributeEditorFactory = createAttributeEditorFactory();
			Assert.isNotNull(attributeEditorFactory);
		}
		attributeEditorToolkit = createAttributeEditorToolkit();
		Assert.isNotNull(attributeEditorToolkit);
		attributeEditorToolkit.setMenu(editorComposite.getMenu());
		attributeEditorToolkit.setSelectionChangedListener(this);

		createParts();

		FocusListener listener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lastFocusControl = (Control) e.widget;
			}
		};
		addFocusListener(editorComposite, listener);
		AbstractTaskEditorPart summaryPart = getPart(ID_PART_SUMMARY);
		if (summaryPart != null) {
			lastFocusControl = summaryPart.getControl();
		}
	}

	protected TaskDataModel createModel(TaskEditorInput input) throws CoreException {
		ITaskDataWorkingCopy taskDataState = TasksUi.getTaskDataManager().getWorkingCopy(task);
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(getConnectorKind(),
				taskDataState.getRepositoryUrl());
		return new TaskDataModel(taskRepository, input.getTask(), taskDataState);
	}

	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = new LinkedHashSet<TaskEditorPartDescriptor>();
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorSummaryPart();
			}
		}.setPath(PATH_HEADER));
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorAttributePart();
			}
		}.setPath(PATH_ATTRIBUTES));
		if (!taskData.isNew()) {
			descriptors.add(new TaskEditorPartDescriptor(ID_PART_ATTACHMENTS) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new TaskEditorAttachmentPart();
				}
			}.setPath(PATH_ATTACHMENTS));
		}
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_DESCRIPTION) {
			@Override
			public AbstractTaskEditorPart createPart() {
				TaskEditorDescriptionPart part = new TaskEditorDescriptionPart();
				if (getModel().getTaskData().isNew()) {
					part.setExpandVertically(true);
					part.setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
				}
				return part;
			}
		}.setPath(PATH_COMMENTS));
		if (!taskData.isNew()) {
			descriptors.add(new TaskEditorPartDescriptor(ID_PART_COMMENTS) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new TaskEditorCommentPart();
				}
			}.setPath(PATH_COMMENTS));
		}
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_NEW_COMMENT) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorNewCommentPart();
			}
		}.setPath(PATH_COMMENTS));
		if (taskData.isNew()) {
			descriptors.add(new TaskEditorPartDescriptor(ID_PART_PLANNING) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new TaskEditorPlanningPart();
				}
			}.setPath(PATH_PLANNING));
		}
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_ACTIONS) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorActionPart();
			}
		}.setPath(PATH_ACTIONS));
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_PEOPLE) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorPeoplePart();
			}
		}.setPath(PATH_PEOPLE));
		return descriptors;
	}

	private void createParts() {
		List<TaskEditorPartDescriptor> descriptors = new LinkedList<TaskEditorPartDescriptor>(createPartDescriptors());
		// single column
		createParts(PATH_HEADER, editorComposite, descriptors);
		createParts(PATH_ATTRIBUTES, editorComposite, descriptors);
		createParts(PATH_ATTACHMENTS, editorComposite, descriptors);
		createParts(PATH_COMMENTS, editorComposite, descriptors);
		createParts(PATH_PLANNING, editorComposite, descriptors);
		// two column
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);
		createParts(PATH_ACTIONS, bottomComposite, descriptors);
		createParts(PATH_PEOPLE, bottomComposite, descriptors);
		bottomComposite.pack(true);
	}

	private void createParts(String path, final Composite parent, Collection<TaskEditorPartDescriptor> descriptors) {
		for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();) {
			final TaskEditorPartDescriptor descriptor = it.next();
			if (path == null || path.equals(descriptor.getPath())) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Error creating task editor part: \"" + descriptor.getId() + "\"", e));
					}

					public void run() throws Exception {
						AbstractTaskEditorPart part = descriptor.createPart();
						part.setPartId(descriptor.getId());
						initializePart(parent, part);
					}
				});
				it.remove();
			}
		}
	}

	@Override
	public void dispose() {
		if (attributeEditorToolkit != null) {
			attributeEditorToolkit.dispose();
		}
		TasksUiInternal.getTaskList().removeChangeListener(taskListChangeListener);
		super.dispose();
	}

	public void doAction(String actionId) {
		EditorUtil.doAction(actionId, EditorUtil.getFocusControl(this));
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
				getModel().getTaskRepository(), task, getModel().getTaskData(), getModel().getChangedOldAttributes());
		submitJob.addSubmitJobListener(new SubmitTaskJobListener(getAttachContext()));
		submitJob.schedule();
	}

	/**
	 * Override for customizing the tool bar.
	 */
	public void fillToolBar(IToolBarManager toolBarManager) {
		final TaskRepository taskRepository = (model != null) ? getModel().getTaskRepository() : null;
		if (taskRepository != null) {
			ControlContribution repositoryLabelControl = new ControlContribution("Title") {
				@Override
				protected Control createControl(Composite parent) {
					FormToolkit toolkit = getTaskEditor().getHeaderForm().getToolkit();
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

		if (taskRepository != null && !taskData.isNew()) {
			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
			toolBarManager.add(synchronizeEditorAction);

			clearOutgoingAction = new ClearOutgoingAction(Collections.singletonList((IRepositoryElement) task));
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

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			updateOutlinePage();
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	private void updateOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new TaskEditorOutlinePage();
			outlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					ISelection selection = event.getSelection();
					if (selection instanceof StructuredSelection) {
						Object select = ((StructuredSelection) selection).getFirstElement();
						if (select instanceof TaskEditorOutlineNode) {
							TaskEditorOutlineNode node = (TaskEditorOutlineNode) select;
							if (node.getData() != null) {
								EditorUtil.reveal(form, node.getData().getId());
							} else {
								EditorUtil.reveal(form, node.getLabel());
							}
						}
					}
				}
			});
		}
		if (getModel() != null) {
			TaskEditorOutlineNode node = TaskEditorOutlineNode.parse(getModel().getTaskData());
			outlinePage.setInput(getTaskRepository(), node);
		} else {
			outlinePage.setInput(null, null);
		}
	}

	private boolean getAttachContext() {
		AbstractTaskEditorPart actionPart = getPart(ID_PART_ACTIONS);
		if (actionPart instanceof TaskEditorActionPart) {
			return ((TaskEditorActionPart) actionPart).getAttachContext();
		}
		return false;
	}

	public AttributeEditorFactory getAttributeEditorFactory() {
		return attributeEditorFactory;
	}

	public AttributeEditorToolkit getAttributeEditorToolkit() {
		return attributeEditorToolkit;
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

	public TaskDataModel getModel() {
		return model;
	}

	public AbstractTaskEditorPart getPart(String partId) {
		Assert.isNotNull(partId);
		for (IFormPart part : getManagedForm().getParts()) {
			if (part instanceof AbstractTaskEditorPart) {
				AbstractTaskEditorPart taskEditorPart = (AbstractTaskEditorPart) part;
				if (partId.equals(taskEditorPart.getPartId())) {
					return taskEditorPart;
				}
			}
		}
		return null;
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
			final IStatus status = job.getStatus();
			if (status.getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
				TasksUiInternal.displayStatus("Comment required", status);
				AbstractTaskEditorPart newCommentPart = getPart(ID_PART_NEW_COMMENT);
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
		this.defaultSelection = new StructuredSelection(task);
		this.lastSelection = defaultSelection;
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
		if (part.getControl() != null) {
			if (part.getExpandVertically()) {
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(part.getControl());
			} else {
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(part.getControl());
			}
			// for outline
			if (ID_PART_COMMENTS.equals(part.getPartId())) {
				EditorUtil.setMarker(part.getControl(), TaskEditorOutlineNode.LABEL_COMMENTS);
			}
		}
	}

	@Override
	public boolean isDirty() {
		return (getModel() != null && getModel().isDirty()) || (getManagedForm() != null && getManagedForm().isDirty());
	}

	public boolean isExpandAttributesSection() {
		return expandAttributesSection;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean needsAddToCategory() {
		return needsAddToCategory;
	}

	/**
	 * Force a re-layout of entire form.
	 */
	public void reflow() {
		if (reflow) {
			form.layout(true, true);
			form.reflow(true);
		}
	}

	/**
	 * Updates the editor contents in place.
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

					// clear old controls and parts
					for (Control control : editorComposite.getChildren()) {
						control.dispose();
					}
					lastFocusControl = null;
					lastSelection = null;
					for (IFormPart part : getManagedForm().getParts()) {
						part.dispose();
						getManagedForm().removePart(part);
					}

					// restore menu
					editorComposite.setMenu(menu);

					createFormContentInternal();

					getTaskEditor().setMessage(null, 0);
					getTaskEditor().setActivePage(getId());

					setSubmitEnabled(true);
				} finally {
					setReflow(true);
				}
			}

			updateOutlinePage();
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
			refreshDisabled = true;
			model.refresh(null);
		} catch (CoreException e) {
			getTaskEditor().setMessage("Failed to read task data: " + e.getMessage(), IMessageProvider.ERROR);
			taskData = null;
			return;
		} finally {
			refreshDisabled = false;
		}

		setTaskData(model.getTaskData());
	}

	public void registerDefaultDropListener(final Control control) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);
		if (defaultDropListener == null) {
			defaultDropListener = new TaskAttachmentDropListener(this);
		}
		target.addDropListener(defaultDropListener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	public void selectionChanged(Object element) {
		selectionChanged(new SelectionChangedEvent(this, new StructuredSelection(element)));
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof TextSelection) {
			// only update global actions
			((TaskEditorActionContributor) getEditorSite().getActionBarContributor()).updateSelectableActions(event.getSelection());
			return;
		}
		if (selection.isEmpty()) {
			// something was unselected, reset to default selection
			selection = defaultSelection;
		}
		if (!selection.equals(lastSelection)) {
			this.lastSelection = selection;
			fireSelectionChanged(lastSelection);
		}
	}

	public void setExpandAttributeSection(boolean expandAttributeSection) {
		this.expandAttributesSection = expandAttributeSection;
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

	public void setNeedsAddToCategory(boolean needsAddToCategory) {
		this.needsAddToCategory = needsAddToCategory;
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

	// TODO EDITOR this needs to be tracked somewhere else
	private void setSubmitEnabled(boolean enabled) {
		AbstractTaskEditorPart actionPart = getPart(ID_PART_ACTIONS);
		if (actionPart instanceof TaskEditorAttributePart) {
			((TaskEditorActionPart) actionPart).setSubmitEnabled(enabled);
		}
	}

	private void setTaskData(TaskData taskData) {
		this.taskData = taskData;
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
		refreshDisabled = busy;
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
