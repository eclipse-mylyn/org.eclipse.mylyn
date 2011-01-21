/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fixes for bug 237503
 *     Frank Becker - fixes for bug 252300
 *	   Kevin Sawicki - fixes for bug 306029
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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.PopupDialog;
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
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFormUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientCanvas;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.FocusTracker;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskAttachmentDropListener;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorContributionExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorNewCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineNode;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlinePage;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPlanningPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorRichTextPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.internal.tasks.ui.editors.ToolBarButtonContribution;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Extend to provide a task editor page.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskEditorPage extends TaskFormPage implements ISelectionProvider,
		ISelectionChangedListener {

	/**
	 * Causes the form page to reflow on resize.
	 */
	private final class ParentResizeHandler implements Listener {
		private int generation;

		public void handleEvent(Event event) {
			++generation;

			Display.getCurrent().timerExec(300, new Runnable() {
				int scheduledGeneration = generation;

				public void run() {
					if (getManagedForm().getForm().isDisposed()) {
						return;
					}

					// only reflow if this is the latest generation to prevent
					// unnecessary reflows while the form is being resized
					if (scheduledGeneration == generation) {
						getManagedForm().reflow(true);
					}
				}
			});
		}
	}

	private class SubmitTaskJobListener extends SubmitJobListener {

		private final boolean attachContext;

		public SubmitTaskJobListener(boolean attachContext) {
			this.attachContext = attachContext;
		}

		@Override
		public void done(SubmitJobEvent event) {
			final SubmitJob job = event.getJob();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				private void addTask(ITask newTask) {
					AbstractTaskContainer parent = null;
					AbstractTaskEditorPart actionPart = getPart(ID_PART_ACTIONS);
					if (actionPart instanceof TaskEditorActionPart) {
						parent = ((TaskEditorActionPart) actionPart).getCategory();
					}
					TasksUiInternal.getTaskList().addTask(newTask, parent);
				}

				public void run() {
					try {
						if (job.getStatus() == null) {
							TasksUiInternal.synchronizeRepositoryInBackground(getTaskRepository());
							if (job.getTask().equals(getTask())) {
								refresh();
							} else {
								ITask oldTask = getTask();
								ITask newTask = job.getTask();
								addTask(newTask);

								TaskMigrator migrator = new TaskMigrator(oldTask);
								migrator.setDelete(true);
								migrator.setEditor(getTaskEditor());
								migrator.setMigrateDueDate(!connector.hasRepositoryDueDate(getTaskRepository(),
										newTask, taskData));
								migrator.execute(newTask);
							}
						}
						handleTaskSubmitted(new SubmitJobEvent(job));
					} finally {
						showEditorBusy(false);
					}
				}
			});
		}

		@Override
		public void taskSubmitted(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
			if (!getModel().getTaskData().isNew() && attachContext) {
				AttachmentUtil.postContext(connector, getModel().getTaskRepository(), task, "", null, monitor); //$NON-NLS-1$
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
					if (refreshDisabled || busy) {
						return;
					}

					if (!isDirty() && task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED) {
						// automatically refresh if the user has not made any changes and there is no chance of missing incomings
						AbstractTaskEditorPage.this.refresh();
					} else {
						getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Task_has_incoming_changes,
								IMessageProvider.WARNING, new HyperlinkAdapter() {
									@Override
									public void linkActivated(HyperlinkEvent e) {
										AbstractTaskEditorPage.this.refresh();
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

	private class NotInTaskListListener extends HyperlinkAdapter implements ITaskListChangeListener, IDisposable {

		public NotInTaskListListener() {
			TasksUiPlugin.getTaskList().addChangeListener(this);
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			if (TasksUiPlugin.getTaskList().getTask(task.getRepositoryUrl(), task.getTaskId()) == null) {
				TasksUiPlugin.getTaskList().addTask(task, TasksUiPlugin.getTaskList().getDefaultCategory());
				getTaskEditor().setMessage(null, IMessageProvider.NONE);
			}
		}

		public void containersChanged(Set<TaskContainerDelta> containers) {
			// clears message if task is added to Task List.
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (task.equals(taskContainerDelta.getElement())) {
					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.ADDED)) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								getTaskEditor().setMessage(null, IMessageProvider.NONE);
							}
						});
					}
				}
			}
		}

		public void dispose() {
			TasksUiPlugin.getTaskList().removeChangeListener(this);
		}

	};

	private class AdditionalMenuAction extends Action {

		public AdditionalMenuAction() {
			setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor(PopupDialog.POPUP_IMG_MENU));
			setDisabledImageDescriptor(JFaceResources.getImageRegistry().getDescriptor(
					PopupDialog.POPUP_IMG_MENU_DISABLED));

		}

		@Override
		public void runWithEvent(Event event) {
			ToolItem toolItem = (ToolItem) event.widget;
			Menu menu = getMenuCreator().getMenu(toolItem.getControl());
			Rectangle bounds = toolItem.getParent().getBounds();
			Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
			topLeft = toolItem.getControl().getShell().toDisplay(topLeft);
			menu.setLocation(topLeft.x, topLeft.y);
			menu.setVisible(true);
		}

	}

	private class MenuCreator implements IMenuCreator {

		private MenuManager menuManager;

		private Menu menu;

		public MenuCreator() {
		}

		public void dispose() {
			if (menu != null) {
				menu.dispose();
				menu = null;
			}
			if (menuManager != null) {
				menuManager.dispose();
				menuManager = null;
			}
		}

		public Menu getMenu(Control parent) {
			if (menuManager == null) {
				menuManager = new MenuManager();
				initialize(menuManager);
			}
			return menuManager.createContextMenu(parent);
		}

		public Menu getMenu(Menu parent) {
			return null;
		}

		protected void initialize(MenuManager menuManager) {
		}

	}

	private static final String ERROR_NOCONNECTIVITY = Messages.AbstractTaskEditorPage_Unable_to_submit_at_this_time;

	public static final String ID_PART_ACTIONS = "org.eclipse.mylyn.tasks.ui.editors.parts.actions"; //$NON-NLS-1$

	public static final String ID_PART_ATTACHMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.attachments"; //$NON-NLS-1$

	public static final String ID_PART_ATTRIBUTES = "org.eclipse.mylyn.tasks.ui.editors.parts.attributes"; //$NON-NLS-1$

	public static final String ID_PART_COMMENTS = "org.eclipse.mylyn.tasks.ui.editors.parts.comments"; //$NON-NLS-1$

	public static final String ID_PART_DESCRIPTION = "org.eclipse.mylyn.tasks.ui.editors.parts.descriptions"; //$NON-NLS-1$

	public static final String ID_PART_NEW_COMMENT = "org.eclipse.mylyn.tasks.ui.editors.parts.newComment"; //$NON-NLS-1$

	public static final String ID_PART_PEOPLE = "org.eclipse.mylyn.tasks.ui.editors.parts.people"; //$NON-NLS-1$

	public static final String ID_PART_PLANNING = "org.eclipse.mylyn.tasks.ui.editors.parts.planning"; //$NON-NLS-1$

	public static final String ID_PART_SUMMARY = "org.eclipse.mylyn.tasks.ui.editors.parts.summary"; //$NON-NLS-1$

	public static final String PATH_ACTIONS = "actions"; //$NON-NLS-1$

	public static final String PATH_ATTACHMENTS = "attachments"; //$NON-NLS-1$

	public static final String PATH_ATTRIBUTES = "attributes"; //$NON-NLS-1$

	public static final String PATH_COMMENTS = "comments"; //$NON-NLS-1$

	public static final String PATH_HEADER = "header"; //$NON-NLS-1$

	public static final String PATH_PEOPLE = "people"; //$NON-NLS-1$

	public static final String PATH_PLANNING = "planning"; //$NON-NLS-1$

//	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.page";

	private AttributeEditorFactory attributeEditorFactory;

	private AttributeEditorToolkit attributeEditorToolkit;

	private AbstractRepositoryConnector connector;

	private final String connectorKind;

	private StructuredSelection defaultSelection;

	private Composite editorComposite;

	private ScrolledForm form;

	private boolean busy;

	private ISelection lastSelection;

	private TaskDataModel model;

	private boolean needsAddToCategory;

	private boolean reflow;

	private volatile boolean refreshDisabled;

	private final ListenerList selectionChangedListeners;

	private SynchronizeEditorAction synchronizeEditorAction;

	private ITask task;

	private TaskData taskData;

//	private ITaskListChangeListener taskListChangeListener;

	private FormToolkit toolkit;

	private TaskEditorOutlinePage outlinePage;

	private TaskAttachmentDropListener defaultDropListener;

	private CommonTextSupport textSupport;

	private Composite partControl;

	private GradientCanvas footerComposite;

	private boolean needsFooter;

	private Button submitButton;

	private boolean submitEnabled;

	private boolean needsSubmit;

	private boolean needsSubmitButton;

	private boolean needsPrivateSection;

	private FocusTracker focusTracker;

	/**
	 * @since 3.1
	 */
	public AbstractTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label);
		Assert.isNotNull(connectorKind);
		this.connectorKind = connectorKind;
		this.reflow = true;
		this.selectionChangedListeners = new ListenerList();
		this.submitEnabled = true;
		this.needsSubmit = true;
	}

	public AbstractTaskEditorPage(TaskEditor editor, String connectorKind) {
		this(editor, "id", "label", connectorKind); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @since 3.1
	 * @see FormPage#getEditor()
	 */
	@Override
	public TaskEditor getEditor() {
		return (TaskEditor) super.getEditor();
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
		return CommonTextSupport.canPerformAction(actionId, EditorUtil.getFocusControl(this));
	}

	public void close() {
		if (Display.getCurrent() != null) {
			getSite().getPage().closeEditor(getTaskEditor(), false);
		} else {
			// TODO consider removing asyncExec()
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
	}

	protected AttributeEditorFactory createAttributeEditorFactory() {
		return new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite());
	}

	AttributeEditorToolkit createAttributeEditorToolkit() {
		return new AttributeEditorToolkit(textSupport);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.addListener(SWT.Resize, new ParentResizeHandler());

		if (needsFooter()) {
			partControl = getEditor().getToolkit().createComposite(parent);
			GridLayout partControlLayout = new GridLayout(1, false);
			partControlLayout.marginWidth = 0;
			partControlLayout.marginHeight = 0;
			partControlLayout.verticalSpacing = 0;
			partControl.setLayout(partControlLayout);

			super.createPartControl(partControl);
			getManagedForm().getForm().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			footerComposite = new GradientCanvas(partControl, SWT.NONE);
			footerComposite.setSeparatorVisible(true);
			footerComposite.setSeparatorAlignment(SWT.TOP);
			GridLayout headLayout = new GridLayout();
			headLayout.marginHeight = 0;
			headLayout.marginWidth = 0;
			headLayout.horizontalSpacing = 0;
			headLayout.verticalSpacing = 0;
			headLayout.numColumns = 1;
			footerComposite.setLayout(headLayout);
			footerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			FormColors colors = getEditor().getToolkit().getColors();
			Color top = colors.getColor(IFormColors.H_GRADIENT_END);
			Color bottom = colors.getColor(IFormColors.H_GRADIENT_START);
			footerComposite.setBackgroundGradient(new Color[] { bottom, top }, new int[] { 100 }, true);

			footerComposite.putColor(IFormColors.H_BOTTOM_KEYLINE1, colors.getColor(IFormColors.H_BOTTOM_KEYLINE1));
			footerComposite.putColor(IFormColors.H_BOTTOM_KEYLINE2, colors.getColor(IFormColors.H_BOTTOM_KEYLINE2));
			footerComposite.putColor(IFormColors.H_HOVER_LIGHT, colors.getColor(IFormColors.H_HOVER_LIGHT));
			footerComposite.putColor(IFormColors.H_HOVER_FULL, colors.getColor(IFormColors.H_HOVER_FULL));
			footerComposite.putColor(IFormColors.TB_TOGGLE, colors.getColor(IFormColors.TB_TOGGLE));
			footerComposite.putColor(IFormColors.TB_TOGGLE_HOVER, colors.getColor(IFormColors.TB_TOGGLE_HOVER));
			footerComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));

			createFooterContent(footerComposite);
		} else {
			super.createPartControl(parent);
		}
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();
		registerDefaultDropListener(form);
		CommonFormUtil.disableScrollingOnFocus(form);

		try {
			setReflow(false);

			editorComposite = form.getBody();
			// TODO consider using TableWrapLayout, it makes resizing much faster
			GridLayout editorLayout = new GridLayout();
			editorLayout.verticalSpacing = 0;
			editorComposite.setLayout(editorLayout);

			//form.setData("focusScrolling", Boolean.FALSE);

//			menuManager = new MenuManager();
//			menuManager.setRemoveAllWhenShown(true);
//			getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, this, true);
//			editorComposite.setMenu(menuManager.createContextMenu(editorComposite));
			editorComposite.setMenu(getTaskEditor().getMenu());

			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(getConnectorKind());
			if (connectorUi == null) {
				getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Synchronize_to_update_editor_contents,
						IMessageProvider.INFORMATION, new HyperlinkAdapter() {
							@Override
							public void linkActivated(HyperlinkEvent e) {
								AbstractTaskEditorPage.this.refresh();
							}
						});
			}

			if (taskData != null) {
				createFormContentInternal();
			}

			updateHeaderMessage();
		} finally {
			setReflow(true);

			// if the editor is restored as part of workbench startup then we must reflow() asynchronously
			// otherwise the editor layout is incorrect
			boolean reflowRequired = calculateReflowRequired(form);

			if (reflowRequired) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						// this fixes a problem with layout that occurs when an editor
						// is restored before the workbench is fully initialized
						reflow();
					}
				});
			}
		}
	}

	private boolean calculateReflowRequired(ScrolledForm form) {
		Composite stopComposite = getEditor().getEditorParent().getParent().getParent();
		Composite composite = form.getParent();
		while (composite != null) {
			Rectangle clientArea = composite.getClientArea();
			if (clientArea.width > 1) {
				return false;
			}
			if (composite == stopComposite) {
				return true;
			}
			composite = composite.getParent();
		}
		return true;
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
		attributeEditorFactory.setEditorToolkit(attributeEditorToolkit);

		createParts();

		focusTracker = new FocusTracker();
		focusTracker.track(editorComposite);
//		AbstractTaskEditorPart summaryPart = getPart(ID_PART_SUMMARY);
//		if (summaryPart != null) {
//			lastFocusControl = summaryPart.getControl();
//		}
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
		if (!taskData.isNew() && connector.getTaskAttachmentHandler() != null
				&& (AttachmentUtil.canDownloadAttachment(task) || AttachmentUtil.canUploadAttachment(task))) {
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

		if (needsPrivateSection() || taskData.isNew()) {
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

		descriptors.addAll(getContributionPartDescriptors());
		return descriptors;
	}

	private Collection<TaskEditorPartDescriptor> getContributionPartDescriptors() {
		return TaskEditorContributionExtensionReader.getRepositoryEditorContributions();
	}

	protected void createParts() {
		List<TaskEditorPartDescriptor> descriptors = new LinkedList<TaskEditorPartDescriptor>(createPartDescriptors());
		// single column
		createParts(PATH_HEADER, editorComposite, descriptors);
		createParts(PATH_ATTRIBUTES, editorComposite, descriptors);
		createParts(PATH_PLANNING, editorComposite, descriptors);
		createParts(PATH_ATTACHMENTS, editorComposite, descriptors);
		createParts(PATH_COMMENTS, editorComposite, descriptors);
		// two column
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);
		createParts(PATH_ACTIONS, bottomComposite, descriptors);
		createParts(PATH_PEOPLE, bottomComposite, descriptors);
		bottomComposite.pack(true);

	}

	private void createParts(String path, final Composite parent, final Collection<TaskEditorPartDescriptor> descriptors) {
		for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();) {
			final TaskEditorPartDescriptor descriptor = it.next();
			if (path == null || path.equals(descriptor.getPath())) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Error creating task editor part: \"" + descriptor.getId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
					}

					public void run() throws Exception {
						AbstractTaskEditorPart part = descriptor.createPart();
						part.setPartId(descriptor.getId());
						initializePart(parent, part, descriptors);
					}
				});
				it.remove();
			}
		}
	}

	private void createSubParts(final AbstractTaskEditorSection parentPart,
			final Collection<TaskEditorPartDescriptor> descriptors) {
		for (final TaskEditorPartDescriptor descriptor : descriptors) {
			int i;
			String path = descriptor.getPath();
			if (path != null && (i = path.indexOf("/")) != -1) { //$NON-NLS-1$
				String parentId = path.substring(0, i);
				final String subPath = path.substring(i + 1);
				if (parentId.equals(parentPart.getPartId())) {
					SafeRunner.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
									"Error creating task editor part: \"" + descriptor.getId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
						}

						public void run() throws Exception {
							AbstractTaskEditorPart part = descriptor.createPart();
							part.setPartId(descriptor.getId());
							getManagedForm().addPart(part);
							part.initialize(AbstractTaskEditorPage.this);
							parentPart.addSubPart(subPath, part);
						}
					});
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (textSupport != null) {
			textSupport.dispose();
		}
		if (attributeEditorToolkit != null) {
			attributeEditorToolkit.dispose();
		}
		TasksUiPlugin.getTaskDataManager().removeListener(TASK_DATA_LISTENER);
		super.dispose();
	}

	public void doAction(String actionId) {
		CommonTextSupport.doAction(actionId, EditorUtil.getFocusControl(this));
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!isDirty()) {
			return;
		}

		getManagedForm().commit(true);

		if (model.isDirty()) {
			try {
				model.save(monitor);
			} catch (final CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error saving task", e)); //$NON-NLS-1$
				getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Could_not_save_task, IMessageProvider.ERROR,
						new HyperlinkAdapter() {
							@Override
							public void linkActivated(HyperlinkEvent event) {
								TasksUiInternal.displayStatus(Messages.AbstractTaskEditorPage_Save_failed,
										e.getStatus());
							}
						});
			}
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
						"Failed to set summary for task \"" + task + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
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
		if (!submitEnabled || !needsSubmit()) {
			return;
		}

		try {
			showEditorBusy(true);

			doSave(new NullProgressMonitor());

			SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector,
					getModel().getTaskRepository(), task, getModel().getTaskData(),
					getModel().getChangedOldAttributes());
			submitJob.addSubmitJobListener(new SubmitTaskJobListener(getAttachContext()));
			submitJob.schedule();
		} catch (RuntimeException e) {
			showEditorBusy(false);
			throw e;
		}
	}

	/**
	 * Override for customizing the tool bar.
	 */
	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		final TaskRepository taskRepository = (model != null) ? getModel().getTaskRepository() : null;

		if (taskData == null) {
			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
			toolBarManager.appendToGroup("repository", synchronizeEditorAction); //$NON-NLS-1$
		} else {
			if (taskData.isNew()) {
				DeleteTaskEditorAction deleteAction = new DeleteTaskEditorAction(getTask());
				deleteAction.setImageDescriptor(CommonImages.CLEAR);
				toolBarManager.appendToGroup("new", deleteAction); //$NON-NLS-1$
			} else if (taskRepository != null) {
				ClearOutgoingAction clearOutgoingAction = new ClearOutgoingAction(
						Collections.singletonList((IRepositoryElement) task));
				(clearOutgoingAction).setTaskEditorPage(this);
				if (clearOutgoingAction.isEnabled()) {
					toolBarManager.appendToGroup("new", clearOutgoingAction); //$NON-NLS-1$
				}

				if (task.getSynchronizationState() != SynchronizationState.OUTGOING_NEW) {
					synchronizeEditorAction = new SynchronizeEditorAction();
					synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
					toolBarManager.appendToGroup("repository", synchronizeEditorAction); //$NON-NLS-1$
				}

				NewSubTaskAction newSubTaskAction = new NewSubTaskAction();
				newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(task));
				if (newSubTaskAction.isEnabled()) {
					toolBarManager.appendToGroup("new", newSubTaskAction); //$NON-NLS-1$
				}

				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskData.getConnectorKind());
				if (connectorUi != null) {
					final String historyUrl = connectorUi.getTaskHistoryUrl(taskRepository, task);
					if (historyUrl != null) {
						final Action historyAction = new Action() {
							@Override
							public void run() {
								TasksUiUtil.openUrl(historyUrl);
							}
						};

						historyAction.setText(Messages.AbstractTaskEditorPage_History);
						historyAction.setImageDescriptor(TasksUiImages.TASK_REPOSITORY_HISTORY);
						historyAction.setToolTipText(Messages.AbstractTaskEditorPage_History);
						if (getEditor().openWithBrowserAction != null) {
							getEditor().openWithBrowserAction.setMenuCreator(new MenuCreator() {
								@Override
								protected void initialize(MenuManager menuManager) {
									menuManager.add(historyAction);
								};
							});
						} else {
							toolBarManager.prependToGroup("open", historyAction); //$NON-NLS-1$
						}
					}
				}
			}
			if (needsSubmitButton()) {
				ToolBarButtonContribution submitButtonContribution = new ToolBarButtonContribution(
						"org.eclipse.mylyn.tasks.toolbars.submit") { //$NON-NLS-1$
					@Override
					protected Control createButton(Composite composite) {
						submitButton = new Button(composite, SWT.FLAT);
						submitButton.setText(Messages.TaskEditorActionPart_Submit + " "); //$NON-NLS-1$
						submitButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
						submitButton.setBackground(null);
						submitButton.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								doSubmit();
							}
						});
						return submitButton;
					}
				};
				submitButtonContribution.marginLeft = 10;
				toolBarManager.add(submitButtonContribution);
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

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			updateOutlinePage();
			return outlinePage;
		}
		// TODO 3.5 replace by getTextSupport() method
		if (adapter == CommonTextSupport.class) {
			return textSupport;
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
						selectReveal(select);
						getEditor().setActivePage(getId());
					}
				}
			});
		}
		if (getModel() != null) {
			TaskEditorOutlineNode node = TaskEditorOutlineNode.parse(getModel().getTaskData(), false);
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
		if (getManagedForm() != null) {
			for (IFormPart part : getManagedForm().getParts()) {
				if (part instanceof AbstractTaskEditorPart) {
					AbstractTaskEditorPart taskEditorPart = (AbstractTaskEditorPart) part;
					if (partId.equals(taskEditorPart.getPartId())) {
						return taskEditorPart;
					}
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
		return getEditor();
	}

	public TaskRepository getTaskRepository() {
		// FIXME model can be null
		return getModel().getTaskRepository();
	}

	/**
	 * Invoked after task submission has completed. This method is invoked on the UI thread in all cases whether
	 * submission was successful, canceled or failed. The value returned by <code>event.getJob().getStatus()</code>
	 * indicates the result of the submit job. Sub-classes may override but are encouraged to invoke the super method.
	 * 
	 * @since 3.2
	 * @see SubmitJob
	 */
	protected void handleTaskSubmitted(SubmitJobEvent event) {
		IStatus status = event.getJob().getStatus();
		if (status != null && status.getSeverity() != IStatus.CANCEL) {
			handleSubmitError(event.getJob());
		}
	}

	private void handleSubmitError(SubmitJob job) {
		if (form != null && !form.isDisposed()) {
			final IStatus status = job.getStatus();
			if (status.getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
				TasksUiInternal.displayStatus(Messages.AbstractTaskEditorPage_Comment_required, status);
				AbstractTaskEditorPart newCommentPart = getPart(ID_PART_NEW_COMMENT);
				if (newCommentPart != null) {
					newCommentPart.setFocus();
				}
			} else if (status.getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
				if (TasksUiUtil.openEditRepositoryWizard(getTaskRepository()) == Window.OK) {
					submitEnabled = true;
					doSubmit();
				}
			} else {
				String message;
				if (status.getCode() == RepositoryStatus.ERROR_IO) {
					message = ERROR_NOCONNECTIVITY;
				} else if (status.getMessage().length() > 0) {
					message = Messages.AbstractTaskEditorPage_Submit_failed_ + status.getMessage();
				} else {
					message = Messages.AbstractTaskEditorPage_Submit_failed;
				}
				getTaskEditor().setMessage(message, IMessageProvider.ERROR, new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						TasksUiInternal.displayStatus(Messages.AbstractTaskEditorPage_Submit_failed, status);
					}
				});
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		TaskEditorInput taskEditorInput = (TaskEditorInput) input;
		this.task = taskEditorInput.getTask();
		this.defaultSelection = new StructuredSelection(task);
		this.lastSelection = defaultSelection;
		IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
		this.textSupport = new CommonTextSupport(handlerService);
		this.textSupport.setSelectionChangedListener(this);

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
					IManagedForm form = getManagedForm();
					if (form != null) {
						form.dirtyStateChanged();
					}
				}
			});
			setNeedsAddToCategory(model.getTaskData().isNew());
		} catch (final CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error opening task", e)); //$NON-NLS-1$
			getTaskEditor().setStatus(Messages.AbstractTaskEditorPage_Error_opening_task,
					Messages.AbstractTaskEditorPage_Open_failed, e.getStatus());
		}
	}

	private void initializePart(Composite parent, AbstractTaskEditorPart part,
			Collection<TaskEditorPartDescriptor> descriptors) {
		getManagedForm().addPart(part);
		part.initialize(this);
		if (part instanceof AbstractTaskEditorSection) {
			createSubParts((AbstractTaskEditorSection) part, descriptors);
		}
		if (parent != null) {
			part.createControl(parent, toolkit);
			if (part.getControl() != null) {
				if (ID_PART_ACTIONS.equals(part.getPartId())) {
					// do not expand horizontally
					GridDataFactory.fillDefaults()
							.align(SWT.FILL, SWT.FILL)
							.grab(false, false)
							.applyTo(part.getControl());
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
			try {
				form.setRedraw(false);
				// help the layout managers: ensure that the form width always matches
				// the parent client area width.
				Rectangle parentClientArea = form.getParent().getClientArea();
				Point formSize = form.getSize();
				if (formSize.x != parentClientArea.width) {
					ScrollBar verticalBar = form.getVerticalBar();
					int verticalBarWidth = verticalBar != null ? verticalBar.getSize().x : 15;
					form.setSize(parentClientArea.width - verticalBarWidth, formSize.y);
				}

				form.layout(true, false);
				form.reflow(true);
			} finally {
				form.setRedraw(true);
			}
		}
	}

	/**
	 * Updates the editor contents in place.
	 * 
	 * @deprecated Use {@link #refresh()} instead
	 */
	@Deprecated
	public void refreshFormContent() {
		refresh();
	}

	/**
	 * Updates the editor contents in place.
	 */
	@Override
	public void refresh() {
		if (getManagedForm() == null || getManagedForm().getForm().isDisposed()) {
			// editor possibly closed or page has not been initialized
			return;
		}

		try {
			showEditorBusy(true);

			boolean hasIncoming = false;

			if (getTask() != null) {
				hasIncoming = getTask().getSynchronizationState().isIncoming();
			}
			if (model != null) {
				doSave(new NullProgressMonitor());
				refreshInput();
			} else {
				initModel(getTaskEditor().getTaskEditorInput());
			}

			if (taskData != null) {
				try {
					setReflow(false);
					// prevent menu from being disposed when disposing control on the form during refresh
					Menu menu = editorComposite.getMenu();
					CommonUiUtil.setMenu(editorComposite, null);

					// clear old controls and parts
					for (Control control : editorComposite.getChildren()) {
						control.dispose();
					}
					if (focusTracker != null) {
						focusTracker.reset();
					}
					lastSelection = null;
					for (IFormPart part : getManagedForm().getParts()) {
						part.dispose();
						getManagedForm().removePart(part);
					}

					// restore menu
					editorComposite.setMenu(menu);

					createFormContentInternal();

					getTaskEditor().setMessage(null, 0);
					if (hasIncoming) {
						getTaskEditor().setActivePage(getId());
					}

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
			getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Failed_to_read_task_data_ + e.getMessage(),
					IMessageProvider.ERROR);
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
			getSite().getSelectionProvider().setSelection(selection);
		}
	}

	@Override
	public void setFocus() {
		if (focusTracker != null && focusTracker.setFocus()) {
			return;
		} else {
			IFormPart[] parts = getManagedForm().getParts();
			if (parts.length > 0) {
				parts[0].setFocus();
				return;
			}
		}
		super.setFocus();
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
		if (submitButton != null && !submitButton.isDisposed()) {
			submitButton.setEnabled(enabled);
		}
		submitEnabled = enabled;
	}

	private void setTaskData(TaskData taskData) {
		this.taskData = taskData;
	}

	@Override
	public void showBusy(boolean busy) {
		if (getManagedForm() != null && !getManagedForm().getForm().isDisposed() && this.busy != busy) {
			setSubmitEnabled(!busy);
			CommonUiUtil.setEnabled(editorComposite, !busy);
			this.busy = busy;
		}
	}

	// TODO m4.0 remove
	public void showEditorBusy(boolean busy) {
		getTaskEditor().showBusy(busy);
		refreshDisabled = busy;
	}

	private void updateHeaderMessage() {
		if (taskData == null) {
			getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Synchronize_to_retrieve_task_data,
					IMessageProvider.WARNING, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent e) {
							if (synchronizeEditorAction != null) {
								synchronizeEditorAction.run();
							}
						}
					});
		}
		if (getTaskEditor().getMessage() == null
				&& TasksUiPlugin.getTaskList().getTask(task.getRepositoryUrl(), task.getTaskId()) == null) {
			getTaskEditor().setMessage(Messages.AbstractTaskEditorPage_Add_task_to_tasklist,
					IMessageProvider.INFORMATION, new NotInTaskListListener());
		}
	}

	@Override
	public Control getPartControl() {
		return partControl != null ? partControl : super.getPartControl();
	}

	/**
	 * Returns true, if the page has an always visible footer.
	 * 
	 * @see #setNeedsFooter(boolean)
	 */
	private boolean needsFooter() {
		return needsFooter;
	}

	/**
	 * Specifies that the page should provide an always visible footer. This flag is not set by default.
	 * 
	 * @see #createFooterContent(Composite)
	 * @see #needsFooter()
	 */
	@SuppressWarnings("unused")
	private void setNeedsFooter(boolean needsFooter) {
		this.needsFooter = needsFooter;
	}

	private void createFooterContent(Composite parent) {
		parent.setLayout(new GridLayout());

//		submitButton = toolkit.createButton(parent, Messages.TaskEditorActionPart_Submit, SWT.NONE);
//		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//		submitButtonData.widthHint = 100;
//		submitButton.setBackground(null);
//		submitButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
//		submitButton.setLayoutData(submitButtonData);
//		submitButton.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				doSubmit();
//			}
//		});
	}

	/**
	 * Returns true, if the page supports a submit operation.
	 * 
	 * @since 3.2
	 * @see #setNeedsSubmit(boolean)
	 */
	public boolean needsSubmit() {
		return needsSubmit;
	}

	/**
	 * Specifies that the page supports the submit operation. This flag is set to true by default.
	 * 
	 * @since 3.2
	 * @see #needsSubmit()
	 * @see #doSubmit()
	 */
	public void setNeedsSubmit(boolean needsSubmit) {
		this.needsSubmit = needsSubmit;
	}

	/**
	 * Returns true, if the page provides a submit button.
	 * 
	 * @since 3.2
	 * @see #setNeedsSubmitButton(boolean)
	 */
	public boolean needsSubmitButton() {
		return needsSubmitButton;
	}

	/**
	 * Specifies that the page supports submitting. This flag is set to false by default.
	 * 
	 * @since 3.2
	 * @see #needsSubmitButton()
	 */
	public void setNeedsSubmitButton(boolean needsSubmitButton) {
		this.needsSubmitButton = needsSubmitButton;
	}

	/**
	 * Returns true, if the page provides a submit button.
	 * 
	 * @since 3.2
	 * @see #setNeedsPrivateSection(boolean)
	 */
	public boolean needsPrivateSection() {
		return needsPrivateSection;
	}

	/**
	 * Specifies that the page should provide the private section. This flag is not set by default.
	 * 
	 * @since 3.2
	 * @see #needsPrivateSection()
	 */
	public void setNeedsPrivateSection(boolean needsPrivateSection) {
		this.needsPrivateSection = needsPrivateSection;
	}

//	private void fillLeftHeaderToolBar(IToolBarManager toolBarManager) {
//		if (needsSubmit()) {
//			ControlContribution submitButtonContribution = new ControlContribution(
//					"org.eclipse.mylyn.tasks.toolbars.submit") { //$NON-NLS-1$
//				@Override
//				protected int computeWidth(Control control) {
//					return super.computeWidth(control) + 5;
//				}
//
//				@Override
//				protected Control createControl(Composite parent) {
//					Composite composite = new Composite(parent, SWT.NONE);
//					composite.setBackground(null);
//					GridLayout layout = new GridLayout();
//					layout.marginWidth = 0;
//					layout.marginHeight = 0;
//					layout.marginLeft = 10;
//					composite.setLayout(layout);
//
//					submitButton = toolkit.createButton(composite, Messages.TaskEditorActionPart_Submit + " ", SWT.NONE); //$NON-NLS-1$
//					submitButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
//					submitButton.setBackground(null);
//					submitButton.addListener(SWT.Selection, new Listener() {
//						public void handleEvent(Event e) {
//							doSubmit();
//						}
//					});
//					GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BOTTOM).applyTo(submitButton);
//					return composite;
//				}
//			};
//			toolBarManager.add(submitButtonContribution);
//		}
//	}

	@Override
	public boolean selectReveal(Object object) {
		if (object instanceof TaskEditorOutlineNode) {
			TaskEditorOutlineNode node = (TaskEditorOutlineNode) object;
			TaskAttribute attribute = node.getData();
			if (attribute != null) {
				if (TaskAttribute.TYPE_COMMENT.equals(attribute.getMetaData().getType())) {
					AbstractTaskEditorPart actionPart = this.getPart(AbstractTaskEditorPage.ID_PART_COMMENTS);
					if (actionPart != null && actionPart.getControl() instanceof ExpandableComposite) {
						CommonFormUtil.setExpanded((ExpandableComposite) actionPart.getControl(), true);
						if (actionPart.getControl() instanceof Section) {
							Control client = ((Section) actionPart.getControl()).getClient();
							if (client instanceof Composite) {
								for (Control control : ((Composite) client).getChildren()) {
									// toggle subsections
									if (control instanceof Section) {
										CommonFormUtil.setExpanded((Section) control, true);
									}
								}
							}
						}
					}
					EditorUtil.reveal(this.getManagedForm().getForm(), attribute.getId());
					return true;
				} else if (TaskAttribute.TYPE_ATTACHMENT.equals(attribute.getMetaData().getType())) {
					AbstractTaskEditorPart actionPart = this.getPart(AbstractTaskEditorPage.ID_PART_ATTACHMENTS);
					if (actionPart != null && actionPart.getControl() instanceof ExpandableComposite) {
						CommonFormUtil.setExpanded((ExpandableComposite) actionPart.getControl(), true);
						if (actionPart.getControl() instanceof Section) {
							Control client = actionPart.getControl();
							if (client instanceof Composite) {
								for (Control control : ((Composite) client).getChildren()) {
									if (control instanceof Composite) {
										for (Control control1 : ((Composite) control).getChildren()) {
											if (control1 instanceof org.eclipse.swt.widgets.Table) {
												org.eclipse.swt.widgets.Table attachmentTable = ((org.eclipse.swt.widgets.Table) control1);
												TableItem[] attachments = attachmentTable.getItems();
												int index = 0;
												for (TableItem attachment : attachments) {
													Object data = attachment.getData();
													if (data instanceof ITaskAttachment) {
														ITaskAttachment attachmentData = ((ITaskAttachment) data);
														if (attachmentData.getTaskAttribute() == attribute) {
															attachmentTable.deselectAll();
															attachmentTable.select(index);
															IManagedForm mform = actionPart.getManagedForm();
															ScrolledForm form = mform.getForm();
															EditorUtil.focusOn(form, attachmentTable);
															return true;
														}
													}
													index++;
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
					TaskEditorAttributePart actionPart = (TaskEditorAttributePart) this.getPart(AbstractTaskEditorPage.ID_PART_ATTRIBUTES);
					Section section = actionPart.getSection();
					boolean expanded = section.isExpanded();
					if (!expanded && actionPart != null && actionPart.getControl() instanceof ExpandableComposite) {
						CommonFormUtil.setExpanded((ExpandableComposite) actionPart.getControl(), true);
						if (!expanded) {
							CommonFormUtil.setExpanded((ExpandableComposite) actionPart.getControl(), false);
						}
					}

					EditorUtil.reveal(this.getManagedForm().getForm(), attribute.getId());
					return true;
				}
			} else {
				TaskRelation taskRelation = node.getTaskRelation();
				TaskRepository taskRepository = node.getTaskRepository();
				if (taskRelation != null && taskRepository != null) {
					String taskID = taskRelation.getTaskId();
					TasksUiUtil.openTask(taskRepository, taskID);
				} else {
					EditorUtil.reveal(this.getManagedForm().getForm(), node.getLabel());
				}
				return true;
			}
		}
		return super.selectReveal(object);
	}

}
