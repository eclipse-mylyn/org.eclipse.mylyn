/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.mylyn.internal.tasks.ui.SubmitTaskDataJob;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
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
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
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
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

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
public abstract class AbstractTaskEditorPage extends FormPage {

	private enum SectionName {
		ACTIONS_SECTION("Actions"), ATTACHMENTS_SECTION("Attachments"), ATTRIBUTES_SECTION("Attributes"), COMMENTS_SECTION(
				"Comments"), DESCRIPTION_SECTION("Description"), NEWCOMMENT_SECTION("New Comment"), PEOPLE_SECTION(
				"People"), PLANNING_SECTION("Personal Planning"), RELATEDBUGS_SECTION("Related Tasks");

		private String prettyName;

		SectionName(String prettyName) {
			this.prettyName = prettyName;
		}

		public String getPrettyName() {
			return prettyName;
		}
	}

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

						refreshEditor();
					} else {
						handleSubmitError(job.getError());
					}

					setGlobalBusy(false);
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

			getAttributeManager().setStorageUrl(task.getRepositoryUrl());
			getAttributeManager().setStorageId(task.getTaskId());
		}

	}

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	private static final String LABEL_HISTORY = "History";

	private static final Font TITLE_FONT = JFaceResources.getBannerFont();

	private TaskEditorActionPart actionPart;

	private ToggleTaskActivationAction activateAction;

	private AttributeManager attributeManager;

	private final List<IRepositoryTaskAttributeListener> attributesListeners = new ArrayList<IRepositoryTaskAttributeListener>();

	private Section attributesSection;

	private Color colorIncoming;

	private TaskEditorCommentPart commentPart;

	private AbstractRepositoryConnector connector;

	private final HashMap<Object, Control> controlBySelectableObject = new HashMap<Object, Control>();

	private TaskEditorDescriptionPart descriptionPart;

	private Composite editorComposite;

	private boolean expandedStateAttributes = false;

	// once the following bug is fixed, this check for first focus is probably
	// not needed -> Bug# 172033: Restore editor focus
	private boolean firstFocus = true;

	private ScrolledForm form;

	private boolean formBusy = false;

	private Action historyAction;

	private IRepositoryTaskSelection lastSelected = null;

	private Menu menu;

	private boolean needsAttachments;

	private boolean needsComments;

	private boolean needsHeader;

	private boolean needsPlanning;

	private TaskEditorRichTextPart newCommentPart;

	private NewSubTaskAction newSubTaskAction;

	private Action openBrowserAction;

	private RepositoryTaskOutlinePage outlinePage = null;

	private TaskEditorPlanningPart planningPart;

	private boolean reflow = true;

	private TaskRepository taskRepository;

	private final List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	private Action clearOutgoingAction;

	/**
	 * Focuses on form widgets when an item in the outline is selected.
	 */
	private final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode n = (RepositoryTaskOutlineNode) select;

					if (lastSelected != null
							&& ContentOutlineTools.getHandle(n).equals(ContentOutlineTools.getHandle(lastSelected))) {
						// we don't need to set the selection if it is already
						// set
						return;
					}
					lastSelected = n;

					boolean highlight = true;
					if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_COMMENTS)) {
						highlight = false;
					}

					Object data = n.getData();
					if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_NEW_COMMENT)) {
						if (commentPart != null) {
							commentPart.setFocus();
						}
					} else if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_DESCRIPTION)) {
						if (descriptionPart != null) {
							descriptionPart.setFocus();
						}
					} else if (data != null) {
						select(data, highlight);
					}
				}
				part.setFocus();
			}
		}
	};

	private final ISelectionProvider selectionProvider = new ISelectionProvider() {
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.add(listener);
		}

		public ISelection getSelection() {
			TaskComment selectedComment = null;
			if (commentPart != null) {
				selectedComment = commentPart.getSelectedComment();
			}
			RepositoryTaskSelection selection = new RepositoryTaskSelection(taskData.getTaskId(),
					taskData.getRepositoryUrl(), taskData.getConnectorKind(), "", selectedComment,
					taskData.getSummary());
			selection.setIsDescription(true);
			return selection;
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			// No implementation.
		}
	};

	private TaskEditorSummaryPart summaryPart;

	private SynchronizeEditorAction synchronizeEditorAction;

	private AbstractTask task;

	private RepositoryTaskData taskData;

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new TaskListChangeAdapter() {

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
											refreshEditor();
										}
									});

							// API EDITOR this needs to be tracked somewhere else
							if (actionPart != null) {
								actionPart.setSubmitEnabled(false);
							}
						} else {
							refreshEditor();
						}
					}
				});
			}
		}
	};

	private RepositoryTaskOutlineNode taskOutlineModel = null;

	private FormToolkit toolkit;

	public AbstractTaskEditorPage(TaskEditor editor) {
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void addAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.add(listener);
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

	/**
	 * @see #select(Object, boolean)
	 */
	public void addSelectableControl(Object item, Control control) {
		controlBySelectableObject.put(item, control);
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
		Section section = createSection(composite, getSectionLabel(SectionName.ACTIONS_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(section);

		actionPart = new TaskEditorActionPart(this);
		initializePart(section, actionPart);
	}

	private void createAttachmentSection(Composite composite) {
		// TODO: expand to show new attachments
		Section section = createSection(composite, getSectionLabel(SectionName.ATTACHMENTS_SECTION), false);
		section.setText(section.getText() + " (" + taskData.getAttachments().size() + ")");

		TaskEditorAttachmentPart attachmentPart = new TaskEditorAttachmentPart(this);
		attachmentPart.setSupportsDelete(supportsAttachmentDelete());
		initializePart(section, attachmentPart);
	}

	protected abstract AttributeManager createAttributeManager(TaskRepository taskRepository, String storageUrl,
			String storageId);

	private void createAttributeSection() {
		attributesSection = createSection(editorComposite, getSectionLabel(SectionName.ATTRIBUTES_SECTION),
				expandedStateAttributes || getAttributeManager().hasVisibleOutgoingChanges(taskData));

		TaskEditorAttributePart attributePart = new TaskEditorAttributePart(this);
		initializePart(attributesSection, attributePart);
	}

	private void createCommentSection(Composite composite) {
		Section commentsSection = createSection(composite, getSectionLabel(SectionName.COMMENTS_SECTION));

		commentPart = new TaskEditorCommentPart(this, commentsSection);
		commentPart.setSupportsDelete(supportsCommentDelete());
		initializePart(commentsSection, commentPart);
	}

	private void createDescriptionSection(Composite composite) {
		RepositoryTaskAttribute attribute = getAttributeManager().getTaskData().getAttribute(
				RepositoryTaskAttribute.DESCRIPTION);
		if (attribute != null) {
			Section descriptionSection = createSection(composite, getSectionLabel(SectionName.DESCRIPTION_SECTION));
			descriptionPart = new TaskEditorDescriptionPart(this, attribute);
			initializePart(descriptionSection, descriptionPart);
		}
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);

		try {
			reflow = false;

			form = managedForm.getForm();
			toolkit = managedForm.getToolkit();
			registerDropListener(form);

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
								refreshEditor();
							}
						});
			}

			if (taskData == null) {
				getParentEditor().setMessage(
						"Task data not available. Press synchronize button (right) to retrieve latest data.",
						IMessageProvider.WARNING);
			} else {
				createSections();
			}

			updateHeaderControls();

			form.setRedraw(true);
		} finally {
			reflow = true;
		}

		form.reflow(true);
	}

	private void createNewCommentSection(Composite composite) {
		RepositoryTaskAttribute attribute = getAttributeManager().getTaskData().getAttribute(
				RepositoryTaskAttribute.COMMENT_NEW);
		if (attribute != null) {
			Section newCommentSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
			newCommentSection.setText(getSectionLabel(SectionName.NEWCOMMENT_SECTION));
			newCommentSection.setLayout(new GridLayout());
			newCommentSection.setLayoutData(new GridData(GridData.FILL_BOTH));

			newCommentPart = new TaskEditorRichTextPart(this, attribute);
			initializePart(newCommentSection, newCommentPart);
		}
	}

	private void createPeopleSection(Composite composite) {
		Section peopleSection = createSection(composite, getSectionLabel(SectionName.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(peopleSection);

		TaskEditorPeoplePart peoplePart = new TaskEditorPeoplePart(this);
		initializePart(peopleSection, peoplePart);
	}

	private void createPlanningSection(Composite composite) {
		Section planningSection = createSection(composite, getSectionLabel(SectionName.PLANNING_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(planningSection);

		planningPart = new TaskEditorPlanningPart(this);
		initializePart(planningSection, planningPart);
	}

	private Section createSection(Composite composite, String title) {
		return createSection(composite, title, true);
	}

	private Section createSection(Composite composite, String title, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
		if (expandedState) {
			style |= ExpandableComposite.EXPANDED;
		}
		Section section = toolkit.createSection(composite, style);
		section.setText(title);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return section;
	}

	private void createSections() {
		createSummarySection(editorComposite);

		createAttributeSection();

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

		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

	private void createSummarySection(Composite composite) {
		summaryPart = new TaskEditorSummaryPart(this);
		getManagedForm().addPart(summaryPart);
		summaryPart.setInput(connector, taskRepository, taskData);
		summaryPart.setNeedsHeader(needsHeader());
		summaryPart.createControl(composite, toolkit);
		summaryPart.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	// TODO EDITOR move
	protected void deleteAttachment(RepositoryAttachment attachment) {
	}

	// TODO EDITOR move
	protected void deleteComment(TaskComment comment) {
	}

	@Override
	public void dispose() {
		TasksUi.getTaskListManager().getTaskList().removeChangeListener(TASKLIST_CHANGE_LISTENER);
		getSite().getPage().removeSelectionListener(selectionListener);
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

		attributeManager.save();

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
				final String historyUrl = connectorUi.getTaskHistoryUrl(taskRepository, taskData.getTaskKey());
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
				String taskUrl = connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskKey());
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

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered under
	 * <code>selectionChangedListeners</code>.
	 * 
	 * @param event
	 *            The selection event.
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.toArray();
		for (Object element : listeners) {
			final ISelectionChangedListener l = (ISelectionChangedListener) element;
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	/**
	 * Scroll to a specified piece of text
	 * 
	 * @param selectionComposite
	 *            The StyledText to scroll to
	 */
	private void focusOn(Control selectionComposite, boolean highlight) {
		int pos = 0;
		// if (previousText != null && !previousText.isDisposed()) {
		// previousText.setsetSelection(0);
		// }

		// if (selectionComposite instanceof FormText)
		// previousText = (FormText) selectionComposite;

		if (selectionComposite != null) {

			// if (highlight && selectionComposite instanceof FormText &&
			// !selectionComposite.isDisposed())
			// ((FormText) selectionComposite).set.setSelection(0, ((FormText)
			// selectionComposite).getText().length());

			// get the position of the text in the composite
			pos = 0;
			Control s = selectionComposite;
			if (s.isDisposed()) {
				return;
			}
			s.setEnabled(true);
			s.setFocus();
			s.forceFocus();
			while (s != null && s != getEditorComposite()) {
				if (!s.isDisposed()) {
					pos += s.getLocation().y;
					s = s.getParent();
				}
			}

			pos = pos - 60; // form.getOrigin().y;

		}
		if (!form.getBody().isDisposed()) {
			form.setOrigin(0, pos);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && taskOutlineModel != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	protected abstract AttributeEditorFactory getAttributeEditorFactory();

	public abstract AttributeEditorToolkit getAttributeEditorToolkit();

	protected AttributeManager getAttributeManager() {
		return attributeManager;
	}

	protected Color getColorIncoming() {
		return colorIncoming;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
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

	/**
	 * @since 2.1
	 */
	public TaskEditor getParentEditor() {
		return (TaskEditor) getEditor();
	}

	private String getSectionLabel(SectionName labelName) {
		return labelName.getPrettyName();
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
		taskData = taskEditorInput.getTaskData();
		if (taskData == null) {
			// TODO EDITOR load task data
			return;
		}

		taskRepository = TasksUi.getRepositoryManager().getRepository(taskData.getConnectorKind(),
				taskData.getRepositoryUrl());
		if (task != null) {
			attributeManager = createAttributeManager(taskRepository, task.getRepositoryUrl(), task.getTaskId());
		} else {
			attributeManager = createAttributeManager(taskRepository, taskData.getRepositoryUrl(), taskData.getTaskId());
		}

		connector = TasksUi.getRepositoryManager().getRepositoryConnector(taskRepository.getConnectorKind());

		attributeManager.addAttributeManagerListener(new IAttributeManagerListener() {
			public void attributeChanged(RepositoryTaskAttribute attribute) {
				getManagedForm().dirtyStateChanged();
			}
		});

		refreshInput();

		TasksUi.getTaskListManager().getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	private void initializePart(Section section, AbstractTaskEditorPart part) {
		getManagedForm().addPart(part);
		part.setInput(connector, taskRepository, taskData);
		part.createControl(section, toolkit);
		section.setClient(part.getControl());

		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			part.fillToolBar(toolBarManager);
			// TODO EDITOR toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			if (toolBarManager.getSize() > 0) {
				Composite toolbarComposite = toolkit.createComposite(section);
				toolbarComposite.setBackground(null);
				RowLayout rowLayout = new RowLayout();
				rowLayout.marginTop = 0;
				rowLayout.marginBottom = 0;
				toolbarComposite.setLayout(rowLayout);

				toolBarManager.createControl(toolbarComposite);
				section.setTextClient(toolbarComposite);
			}
		}
	}

	@Override
	public boolean isDirty() {
		return getAttributeManager().isDirty() || (getManagedForm() != null && getManagedForm().isDirty());
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
	 * @since 2.0 If existing task editor, update contents in place
	 */
	public void refreshEditor() {
		// Note: Marking read must run synchronously
		// If not, incomings resulting from subsequent synchronization
		// can get marked as read (without having been viewed by user
		if (task != null) {
			TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
		}

		if (getManagedForm().getForm().isDisposed()) {
			// editor possibly closed as part of submit, mark read
			return;
		}

		try {
			setGlobalBusy(true);

			doSave(new NullProgressMonitor());

			// TODO EDITOR commentComposites.clear();
			controlBySelectableObject.clear();

			refreshInput();

			updateHeaderControls();

			if (task != null) {
				TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
			}

			if (taskData == null) {
				// FIXME make message a hyperlink
				getParentEditor().setMessage(
						"Task data not available. Press synchronize button (right) to retrieve latest data.",
						IMessageProvider.WARNING);
			} else {
				// clear old controls
				menu = editorComposite.getMenu();
				removeSections();
				editorComposite.setMenu(menu);

				createSections();
				getParentEditor().setMessage(null, 0);
				getParentEditor().setActivePage(AbstractTaskEditorPage.this.getId());

				if (taskOutlineModel != null && outlinePage != null && !outlinePage.getControl().isDisposed()) {
					outlinePage.getOutlineTreeViewer().setInput(taskOutlineModel);
					outlinePage.getOutlineTreeViewer().refresh(true);
				}

				if (actionPart != null) {
					actionPart.setSubmitEnabled(true);
				}
			}

			getManagedForm().dirtyStateChanged();
		} finally {
			setGlobalBusy(false);
		}
	}

	private void refreshInput() {
		attributeManager.refreshInput();

		taskData = attributeManager.getTaskData();

		needsComments = !taskData.isNew();
		needsAttachments = !taskData.isNew();
		needsHeader = !taskData.isNew();
		needsPlanning = taskData.isNew();

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData);
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

	public void removeAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.remove(listener);
	}

	private void removeSections() {
		menu = editorComposite.getMenu();
		setMenu(editorComposite, null);
		for (Control control : editorComposite.getChildren()) {
			control.dispose();
		}
	}

	/**
	 * @see #addSelectableControl(Object, Control)
	 */
	public void removeSelectableControl(Object item) {
		controlBySelectableObject.remove(item);
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

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public boolean select(Object o, boolean highlight) {
		Control control = controlBySelectableObject.get(o);
		if (control != null && !control.isDisposed()) {

			// expand all children
			if (control instanceof ExpandableComposite) {
				ExpandableComposite ex = (ExpandableComposite) control;
				if (!ex.isExpanded()) {
					EditorUtil.toggleExpandableComposite(true, ex);
				}
			}

			// expand all parents of control
			Composite comp = control.getParent();
			while (comp != null) {
				if (comp instanceof Section) {
					((Section) comp).setExpanded(true);
				} else if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					if (!ex.isExpanded()) {
						EditorUtil.toggleExpandableComposite(true, ex);
					}

					// HACK: This is necessary
					// due to a bug in SWT's ExpandableComposite.
					// 165803: Expandable bars should expand when clicking anywhere
					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
					if (ex.getData() != null && ex.getData() instanceof Composite) {
						((Composite) ex.getData()).setVisible(true);
					}
				}
				comp = comp.getParent();
			}
			focusOn(control, highlight);
		} else if (o instanceof RepositoryTaskData) {
			focusOn(null, highlight);
		} else {
			return false;
		}
		return true;
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

	public void setGlobalBusy(boolean busy) {
		if (getParentEditor() != null) {
			getParentEditor().showBusy(busy);
		} else {
			showBusy(busy);
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
		setGlobalBusy(true);

		doSave(new NullProgressMonitor());

		SubmitTaskDataJob submitJob = new SubmitTaskDataJob(connector, taskRepository, taskData);
		submitJob.setAttachContext(actionPart.getAttachContext());
		submitJob.addJobChangeListener(new SubmitTaskJobListener());
		submitJob.schedule();
	}

	protected boolean supportsAttachmentDelete() {
		return false;
	}

	protected boolean supportsCommentDelete() {
		return false;
	}

	protected boolean supportsRefreshAttributes() {
		return true;
	}

}
