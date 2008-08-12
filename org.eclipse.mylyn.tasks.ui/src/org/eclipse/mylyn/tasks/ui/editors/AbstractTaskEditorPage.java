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
import java.util.Date;
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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskEditorAction;
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
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
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
					TasksUiInternal.getTaskList().addTask(newTask, parent);
					ITask oldTask = getTask();
					if (oldTask instanceof AbstractTask && newTask instanceof AbstractTask) {
						((AbstractTask) newTask).setNotes(((AbstractTask) oldTask).getNotes());
						DateRange scheduledDate = ((AbstractTask) oldTask).getScheduledForDate();
						TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) newTask, scheduledDate);
						Date dueDate = ((AbstractTask) oldTask).getDueDate();
						TasksUiPlugin.getTaskActivityManager().setDueDate(newTask, dueDate);
						((AbstractTask) newTask).setEstimatedTimeHours(((AbstractTask) oldTask).getEstimatedTimeHours());
					}
					ContextCorePlugin.getContextStore().saveActiveContext();
					ContextCore.getContextStore().cloneContext(oldTask.getHandleIdentifier(),
							newTask.getHandleIdentifier());
					close();
					TasksUiInternal.getTaskList().deleteTask(oldTask);
					ContextCore.getContextManager().deleteContext(oldTask.getHandleIdentifier());
					try {
						TasksUiPlugin.getTaskDataManager().deleteTaskData(oldTask);
					} catch (CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Failed to delete task data", e));
					}
					TasksUiInternal.openTaskInBackground(newTask, false);
				}

				public void run() {
					try {
						if (job.getStatus() == null) {
							TasksUiInternal.synchronizeRepository(getTaskRepository(), false);
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

//	private class TaskListChangeListener extends TaskListChangeAdapter {
//		@Override
//		public void containersChanged(Set<TaskContainerDelta> containers) {
//			if (refreshDisabled) {
//				return;
//			}
//			ITask taskToRefresh = null;
//			for (TaskContainerDelta taskContainerDelta : containers) {
//				if (task.equals(taskContainerDelta.getElement())) {
//					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)
//							&& !taskContainerDelta.isTransient()) {
//						taskToRefresh = (ITask) taskContainerDelta.getElement();
//						break;
//					}
//				}
//			}
//			if (taskToRefresh != null) {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						if (!isDirty() && task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED) {
//							// automatically refresh if the user has not made any changes and there is no chance of missing incomings
//							refreshFormContent();
//						} else {
//							getTaskEditor().setMessage("Task has incoming changes", IMessageProvider.WARNING,
//									new HyperlinkAdapter() {
//										@Override
//										public void linkActivated(HyperlinkEvent e) {
//											refreshFormContent();
//										}
//									});
//							setSubmitEnabled(false);
//						}
//					}
//				});
//			}
//		}
//	}

	private final ITaskDataManagerListener TASK_DATA_LISTENER = new ITaskDataManagerListener() {

		public void taskDataUpdated(final TaskDataManagerEvent event) {
			ITask task = event.getTask();
			if (task.equals(AbstractTaskEditorPage.this.getTask()) && event.getTaskDataUpdated()) {
				refresh(task);
			}
		}

		private void refresh(final ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (refreshDisabled) {
						return;
					}

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

		public void editsDiscarded(TaskDataManagerEvent event) {
			if (event.getTask().equals(AbstractTaskEditorPage.this.getTask())) {
				refresh(event.getTask());
			}
		}
	};

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	public static final String ID_PART_ACTIONS = "org.eclipse.mylyn.tasks.ui.editors.parts.actions";

	public static final String ID_PART_ATTACHMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.attachments";

	public static final String ID_PART_ATTRIBUTES = "org.eclipse.mylyn.tasks.ui.editors.parts.attributes";

	public static final String ID_PART_COMMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.comments";

	public static final String ID_PART_DESCRIPTION = "org.eclipse.mylyn.tasks.ui.editors.parts.descriptions";

	public static final String ID_PART_NEW_COMMENT = "org.eclipse.mylyn.tasks.ui.editors.parts.newComment";

	public static final String ID_PART_PEOPLE = "org.eclipse.mylyn.tasks.ui.editors.parts.people";

	public static final String ID_PART_PLANNING = "org.eclipse.mylyn.tasks.ui.editors.parts.planning";

	public static final String ID_PART_SUMMARY = "org.eclipse.mylyn.tasks.ui.editors.parts.summary";

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

	private ScrolledForm form;

	private boolean formBusy;

	private Action historyAction;

	private Control lastFocusControl;

	private ISelection lastSelection;

	private TaskDataModel model;

	private boolean needsAddToCategory;

	private NewSubTaskAction newSubTaskAction;

	private Action openBrowserAction;

	private boolean reflow;

	private volatile boolean refreshDisabled;

	private final ListenerList selectionChangedListeners;

	private SynchronizeEditorAction synchronizeEditorAction;

	private DeleteTaskEditorAction deleteAction;

	private ITask task;

	private TaskData taskData;

//	private ITaskListChangeListener taskListChangeListener;

	private FormToolkit toolkit;

	private TaskEditorOutlinePage outlinePage;

	private TaskAttachmentDropListener defaultDropListener;

	// TODO 3.1 define constructor for setting id and label
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
		EditorUtil.disableScrollingOnFocus(form);

		try {
			setReflow(false);

			editorComposite = form.getBody();
			GridLayout editorLayout = new GridLayout();
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			//form.setData("focusScrolling", Boolean.FALSE);

//			menuManager = new MenuManager();
//			menuManager.setRemoveAllWhenShown(true);
//			getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, this, true);
//			editorComposite.setMenu(menuManager.createContextMenu(editorComposite));
			editorComposite.setMenu(getTaskEditor().getMenu());

			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(getConnectorKind());
			if (connectorUi == null) {
				getTaskEditor().setMessage("Synchronize to update editor contents", IMessageProvider.INFORMATION,
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
		ITaskDataWorkingCopy taskDataState;
		try {
			taskDataState = TasksUi.getTaskDataManager().getWorkingCopy(task);
		} catch (OperationCanceledException e) {
			// XXX retry once to work around bug 235479
			taskDataState = TasksUi.getTaskDataManager().getWorkingCopy(task);
		}
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(taskDataState.getConnectorKind(),
				taskDataState.getRepositoryUrl());
		return new TaskDataModel(taskRepository, input.getTask(), taskDataState);
	}

	/**
	 * To suppress a section, just remove its descriptor from the list. To add your own section in a specific order on
	 * the page, use the path value for where you want it to appear (your descriptor will appear after previously added
	 * descriptors with the same path), and add it to the descriptors list in your override of this method.
	 */
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

	protected void createParts() {
		List<TaskEditorPartDescriptor> descriptors = new LinkedList<TaskEditorPartDescriptor>(createPartDescriptors());
		// single column
		createParts(PATH_HEADER, editorComposite, descriptors);
		createParts(PATH_ATTRIBUTES, editorComposite, descriptors);
		createParts(PATH_ATTACHMENTS, editorComposite, descriptors);
		createParts(PATH_COMMENTS, editorComposite, descriptors);
		createParts(PATH_PLANNING, editorComposite, descriptors);
		// two column
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
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
		TasksUiPlugin.getTaskDataManager().removeListener(TASK_DATA_LISTENER);
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

		// update the summary of unsubmitted repository tasks
		if (getTask().getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
			final String summary = connector.getTaskMapping(model.getTaskData()).getSummary();
			try {
				TasksUiPlugin.getTaskList().run(new ITaskListRunnable() {
					public void execute(IProgressMonitor monitor) throws CoreException {
						task.setSummary(summary);
					}
				});
				TasksUiPlugin.getTaskList().notifyElementChanged(task);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Failed to set summary for task \"" + task + "\"", e));
			}
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

		if (taskData != null && taskData.isNew()) {
			deleteAction = new DeleteTaskEditorAction();
			toolBarManager.add(deleteAction);
		}

		if (taskData == null) {
			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
			toolBarManager.add(synchronizeEditorAction);
		} else if (taskRepository != null && !taskData.isNew()) {
			clearOutgoingAction = new ClearOutgoingAction(Collections.singletonList((IRepositoryElement) task));
			((ClearOutgoingAction) clearOutgoingAction).setTaskEditorPage(this);
			if (clearOutgoingAction.isEnabled()) {
				toolBarManager.add(clearOutgoingAction);
			}

			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
			toolBarManager.add(synchronizeEditorAction);

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
							TaskAttribute attribute = node.getData();
							if (attribute != null) {
								if (TaskAttribute.TYPE_COMMENT.equals(attribute.getMetaData().getType())) {
									AbstractTaskEditorPart actionPart = getPart(ID_PART_COMMENTS);
									if (actionPart != null && actionPart.getControl() instanceof ExpandableComposite) {
										EditorUtil.toggleExpandableComposite(true,
												(ExpandableComposite) actionPart.getControl());
									}
								}
								EditorUtil.reveal(form, attribute.getId());
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
		// FIXME model can be null
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

		initModel(taskEditorInput);

		TasksUiPlugin.getTaskDataManager().addListener(TASK_DATA_LISTENER);
	}

	private void initModel(TaskEditorInput input) {
		Assert.isTrue(model == null);
		try {
			this.model = createModel(input);
			this.connector = TasksUi.getRepositoryManager().getRepositoryConnector(getConnectorKind());
			setTaskData(model.getTaskData());
			model.addModelListener(new TaskDataModelListener() {
				@Override
				public void attributeChanged(TaskDataModelEvent event) {
					getManagedForm().dirtyStateChanged();
				}
			});

		} catch (final CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error opening task", e));
			getTaskEditor().setStatus("Error opening task", "Open failed", e.getStatus());
		}
	}

	private void initializePart(Composite parent, AbstractTaskEditorPart part) {
		getManagedForm().addPart(part);
		part.initialize(this);
		part.createControl(parent, toolkit);
		if (part.getControl() != null) {
			if (ID_PART_ACTIONS.equals(part.getPartId())) {
				// do not expand horizontally
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).applyTo(part.getControl());
			} else {
				if (part.getExpandVertically()) {
					GridDataFactory.fillDefaults()
							.align(SWT.FILL, SWT.FILL)
							.grab(true, true)
							.applyTo(part.getControl());
				} else {
					GridDataFactory.fillDefaults()
							.align(SWT.FILL, SWT.TOP)
							.grab(true, false)
							.applyTo(part.getControl());
				}
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

			if (model != null) {
				doSave(new NullProgressMonitor());
				refreshInput();
			} else {
				initModel(getTaskEditor().getTaskEditorInput());
			}

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

	/**
	 * Registers a drop listener for <code>control</code>. The default implementation registers a listener for attaching
	 * files. Does nothing if the editor is showing a new task.
	 * <p>
	 * Clients may override.
	 * </p>
	 * 
	 * @param control
	 *            the control to register the listener for
	 */
	public void registerDefaultDropListener(final Control control) {
		if (getModel() == null || getModel().getTaskData().isNew()) {
			return;
		}

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
			// XXX a styled text widget has lost focus, re-enable all edit actions
			((TaskEditorActionContributor) getEditorSite().getActionBarContributor()).forceActionsEnabled();
		}
		if (!selection.equals(lastSelection)) {
			this.lastSelection = selection;
			fireSelectionChanged(lastSelection);
		}
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
			comp.setMenu(menu);
			for (Control child : comp.getChildren()) {
				child.setMenu(menu);
				if (child instanceof Composite) {
					setMenu((Composite) child, menu);
				}
			}
		}
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
		if (actionPart instanceof TaskEditorActionPart) {
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
			getTaskEditor().setMessage("Synchronize to retrieve task data", IMessageProvider.WARNING,
					new HyperlinkAdapter() {
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
