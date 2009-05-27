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

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.themes.IThemeManager;

import com.ibm.icu.text.DateFormat;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskPlanningEditor extends TaskFormPage {

	private static final int WIDTH_SUMMARY = 500;

	private static final int NOTES_MINSIZE = 100;

	private AbstractTask task;

	private Composite editorComposite;

	private Text endDate;

	private TextViewer summaryEditor;

	private Text issueReportURL;

	private CCombo priorityCombo;

	private CCombo statusCombo;

	private ImageHyperlink getDescLink;

	private ImageHyperlink openUrlLink;

	private final TaskEditor parentEditor;

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
									if (summaryEditor != null && summaryEditor.getTextWidget() != null) {
										updateTaskData(updateTask);
									}
								}
							});
						}
						break;
					}
				}
			}
		}

	};

	private FormToolkit toolkit;

	private ITaskActivityListener timingListener;

	private boolean isDirty;

	private CommonTextSupport textSupport;

	private boolean summaryChanged;

	private TaskRepository repository;

	private PersonalPart personalPart;

	public TaskPlanningEditor(TaskEditor editor) {
		super(editor, ITasksUiConstants.ID_PAGE_PLANNING, Messages.TaskPlanningEditor_Planning);
		this.parentEditor = editor;
		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
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

	/** public for testing */
	public void updateTaskData(final AbstractTask updateTask) {
		if (summaryEditor == null) {
			return;
		}

		if (!summaryEditor.getTextWidget().isDisposed()) {
			if (!summaryChanged) {
				summaryEditor.getTextWidget().setText(updateTask.getSummary());
			}
			if (parentEditor != null) {
				parentEditor.updateHeaderToolBar();
			}
		}

		if (!priorityCombo.isDisposed() && updateTask != null) {
			PriorityLevel level = PriorityLevel.fromString(updateTask.getPriority());
			if (level != null) {
				int prioritySelectionIndex = priorityCombo.indexOf(level.getDescription());
				priorityCombo.select(prioritySelectionIndex);
			}
		}
		if (!statusCombo.isDisposed()) {
			if (task.isCompleted()) {
				statusCombo.select(0);
			} else {
				statusCombo.select(1);
			}
		}
		if ((updateTask instanceof LocalTask) && !endDate.isDisposed()) {
			endDate.setText(getTaskDateString(updateTask));
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (task instanceof LocalTask) {
			String label = summaryEditor.getTextWidget().getText();
			task.setSummary(label);

			// TODO: refactor mutation into TaskList?
			task.setUrl(issueReportURL.getText());
			String priorityDescription = priorityCombo.getItem(priorityCombo.getSelectionIndex());
			PriorityLevel level = PriorityLevel.fromDescription(priorityDescription);
			if (level != null) {
				task.setPriority(level.toString());
			}
			if (!task.isCompleted() && statusCombo.getSelectionIndex() == 0) {
				task.setCompletionDate(new Date());
			} else {
				task.setCompletionDate(null);
			}
			TasksUiInternal.getTaskList().notifyElementChanged(task);
		}
		super.doSave(monitor);
		markDirty(false);
	}

	@Override
	public void doSaveAs() {
		// don't support saving as
	}

	@Override
	public boolean isDirty() {
		return isDirty || super.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();

		TaskEditorInput taskEditorInput = (TaskEditorInput) getEditorInput();
		task = (AbstractTask) taskEditorInput.getTask();

		if (task != null) {
			repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		}

		toolkit = managedForm.getToolkit();

		editorComposite = managedForm.getForm().getBody();
		GridLayout editorLayout = new GridLayout();
		editorLayout.verticalSpacing = 3;
		editorComposite.setLayout(editorLayout);
		//editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (task instanceof LocalTask) {
			createSummarySection(editorComposite);
		}

		personalPart = new PersonalPart(SWT.NONE, true);
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
		personalPart.initialize(getManagedForm(), repository, task, needsDueDate, getEditorSite());
		personalPart.createControl(editorComposite, toolkit);
		personalPart.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));

		getManagedForm().addPart(personalPart);

		if (summaryEditor != null && summaryEditor.getTextWidget() != null
				&& LocalRepositoryConnector.DEFAULT_SUMMARY.equals(summaryEditor.getTextWidget().getText())) {
			summaryEditor.setSelectedRange(0, summaryEditor.getTextWidget().getText().length());
			summaryEditor.getTextWidget().setFocus();
		} else if (summaryEditor != null && summaryEditor.getTextWidget() != null) {
			summaryEditor.getTextWidget().setFocus();
		}
	}

	@Override
	public void setFocus() {
		// form.setFocus();
		if (summaryEditor != null && summaryEditor.getTextWidget() != null
				&& !summaryEditor.getTextWidget().isDisposed()) {
			summaryEditor.getTextWidget().setFocus();
		}
	}

	private Text addNameValueComp(Composite parent, String label, String value, int style) {
		Composite nameValueComp = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 3;
		nameValueComp.setLayout(layout);
		toolkit.createLabel(nameValueComp, label, SWT.NONE).setForeground(
				toolkit.getColors().getColor(IFormColors.TITLE));
		Text text;
		if ((SWT.READ_ONLY & style) == SWT.READ_ONLY) {
			text = new Text(nameValueComp, style);
			toolkit.adapt(text, false, false);
			text.setText(value);
		} else {
			text = toolkit.createText(nameValueComp, value, style);
		}
		return text;
	}

	private void createSummarySection(Composite parent) {
		// Summary
		Composite summaryComposite = toolkit.createComposite(parent);
		GridLayout summaryLayout = new GridLayout();
		summaryLayout.verticalSpacing = 2;
		summaryLayout.marginHeight = 2;
		summaryLayout.marginLeft = 5;
		summaryComposite.setLayout(summaryLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryComposite);

		summaryEditor = addTextEditor(repository, summaryComposite, task.getSummary(), true, SWT.FLAT | SWT.SINGLE);

		GridDataFactory.fillDefaults().hint(WIDTH_SUMMARY, SWT.DEFAULT).minSize(NOTES_MINSIZE, SWT.DEFAULT).grab(true,
				false).applyTo(summaryEditor.getTextWidget());
		summaryEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		if (!(task instanceof LocalTask)) {
			summaryEditor.setEditable(false);
		} else {
			summaryEditor.setEditable(true);
			summaryEditor.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					if (!task.getSummary().equals(summaryEditor.getTextWidget().getText())) {
						summaryChanged = true;
						markDirty(true);
					}
				}
			});
		}
		toolkit.paintBordersFor(summaryComposite);

		Composite statusComposite = toolkit.createComposite(parent);
		GridLayout compLayout = new GridLayout(8, false);
		compLayout.verticalSpacing = 0;
		compLayout.horizontalSpacing = 5;
		compLayout.marginHeight = 3;
		statusComposite.setLayout(compLayout);
		statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite nameValueComp = toolkit.createComposite(statusComposite);
		GridLayout nameValueLayout = new GridLayout(2, false);
		nameValueLayout.marginHeight = 3;
		nameValueComp.setLayout(nameValueLayout);
		toolkit.createLabel(nameValueComp, Messages.TaskPlanningEditor_Priority).setForeground(
				toolkit.getColors().getColor(IFormColors.TITLE));
		priorityCombo = new CCombo(nameValueComp, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(priorityCombo, false, false);
		priorityCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.paintBordersFor(nameValueComp);

		// Populate the combo box with priority levels
		for (String priorityLevel : TaskListView.PRIORITY_LEVEL_DESCRIPTIONS) {
			priorityCombo.add(priorityLevel);
		}

		PriorityLevel level = PriorityLevel.fromString(task.getPriority());
		if (level != null) {
			int prioritySelectionIndex = priorityCombo.indexOf(level.getDescription());
			priorityCombo.select(prioritySelectionIndex);
		}

		if (!(task instanceof LocalTask)) {
			priorityCombo.setEnabled(false);
		} else {
			priorityCombo.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					TaskPlanningEditor.this.markDirty(true);

				}
			});
		}

		nameValueComp = toolkit.createComposite(statusComposite);
		nameValueComp.setLayout(new GridLayout(2, false));
		toolkit.createLabel(nameValueComp, Messages.TaskPlanningEditor_Status).setForeground(
				toolkit.getColors().getColor(IFormColors.TITLE));
		statusCombo = new CCombo(nameValueComp, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(statusCombo, true, true);
		statusCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.paintBordersFor(nameValueComp);
		statusCombo.add(Messages.TaskPlanningEditor_Complete);
		statusCombo.add(Messages.TaskPlanningEditor_Incomplete);
		if (task.isCompleted()) {
			statusCombo.select(0);
		} else {
			statusCombo.select(1);
		}
		if (!(task instanceof LocalTask)) {
			statusCombo.setEnabled(false);
		} else {
			statusCombo.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					TaskPlanningEditor.this.markDirty(true);
				}
			});
		}

		Date creationDate = task.getCreationDate();
		String creationDateString = (creationDate != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(
				creationDate) : ""; //$NON-NLS-1$
		addNameValueComp(statusComposite, Messages.TaskPlanningEditor_Created, creationDateString, SWT.FLAT
				| SWT.READ_ONLY);

		String completionDateString = ""; //$NON-NLS-1$
		if (task.isCompleted()) {
			completionDateString = getTaskDateString(task);
		}
		endDate = addNameValueComp(statusComposite, Messages.TaskPlanningEditor_Completed, completionDateString,
				SWT.FLAT | SWT.READ_ONLY);
		// URL
		Composite urlComposite = toolkit.createComposite(parent);
		GridLayout urlLayout = new GridLayout(4, false);
		urlLayout.verticalSpacing = 0;
		urlLayout.marginHeight = 2;
		urlLayout.marginLeft = 5;
		urlComposite.setLayout(urlLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(urlComposite);

		Label label = toolkit.createLabel(urlComposite, Messages.TaskPlanningEditor_URL);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		issueReportURL = toolkit.createText(urlComposite, task.getUrl(), SWT.FLAT);
		issueReportURL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (!(task instanceof LocalTask)) {
			issueReportURL.setEditable(false);
		} else {
			issueReportURL.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					markDirty(true);
				}
			});
		}

		getDescLink = toolkit.createImageHyperlink(urlComposite, SWT.NONE);
		getDescLink.setImage(CommonImages.getImage(TasksUiImages.TASK_RETRIEVE));
		getDescLink.setToolTipText(Messages.TaskPlanningEditor_Retrieve_task_description_from_URL);
		getDescLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		setButtonStatus();

		issueReportURL.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				setButtonStatus();
			}

			public void keyReleased(KeyEvent e) {
				setButtonStatus();
			}
		});

		getDescLink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				retrieveTaskDescription(issueReportURL.getText());
			}

			public void linkEntered(HyperlinkEvent e) {
			}

			public void linkExited(HyperlinkEvent e) {
			}
		});

		openUrlLink = toolkit.createImageHyperlink(urlComposite, SWT.NONE);
		openUrlLink.setImage(CommonImages.getImage(CommonImages.BROWSER_SMALL));
		openUrlLink.setToolTipText(Messages.TaskPlanningEditor_Open_with_Web_Browser);
		openUrlLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		openUrlLink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				TasksUiUtil.openUrl(issueReportURL.getText());
			}

			public void linkEntered(HyperlinkEvent e) {
			}

			public void linkExited(HyperlinkEvent e) {
			}
		});

		toolkit.paintBordersFor(urlComposite);
		toolkit.paintBordersFor(statusComposite);
	}

	private TextViewer addTextEditor(TaskRepository repository, Composite parent, String text, boolean spellCheck,
			int style) {
		SourceViewer viewer = new SourceViewer(parent, null, style);
		viewer.configure(new RepositoryTextViewerConfiguration(repository, spellCheck));
		textSupport.configure(viewer, new Document(text), spellCheck);
		viewer.getControl().setMenu(getEditor().getMenu());
		viewer.getTextWidget().setFont(getCommentFont());
		return viewer;
	}

	private void markDirty(boolean dirty) {
		if (!dirty) {
			summaryChanged = false;
		}
		isDirty = dirty;
		getManagedForm().dirtyStateChanged();
	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {
		AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(issueReportURL.getText()) {
			@Override
			protected void titleRetrieved(String pageTitle) {
				if (summaryEditor != null && summaryEditor.getControl() != null
						&& !summaryEditor.getControl().isDisposed()) {
					summaryEditor.getTextWidget().setText(pageTitle);
					TaskPlanningEditor.this.markDirty(true);
				}
			}
		};
		job.schedule();
	}

	/**
	 * Sets the Get Description button enabled or not depending on whether there is a URL specified
	 */
	protected void setButtonStatus() {
		String url = issueReportURL.getText();

		if (url.length() > 10 && (url.startsWith("http://") || url.startsWith("https://"))) { //$NON-NLS-1$ //$NON-NLS-2$
			// String defaultPrefix =
			// ContextCore.getPreferenceStore().getString(
			// TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
			// if (url.equals(defaultPrefix)) {
			// getDescButton.setEnabled(false);
			// } else {
			getDescLink.setEnabled(true);
			// }
		} else {
			getDescLink.setEnabled(false);
		}
	}

	private Font getCommentFont() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(CommonThemes.FONT_EDITOR_COMMENT);
		return font;
	}

	private String getTaskDateString(ITask task) {
		if (task == null) {
			return ""; //$NON-NLS-1$
		}
		if (task.getCompletionDate() == null) {
			return ""; //$NON-NLS-1$
		}

		String completionDateString = ""; //$NON-NLS-1$
		try {
			completionDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCompletionDate());
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format date", e)); //$NON-NLS-1$
			return completionDateString;
		}
		return completionDateString;
	}

	@Override
	public void dispose() {
		if (textSupport != null) {
			textSupport.dispose();
		}
		if (timingListener != null) {
			TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		}
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

	@Override
	public String toString() {
		return Messages.TaskPlanningEditor__info_editor_for_task_ + task + ")"; //$NON-NLS-1$
	}

	/** for testing - should cause dirty state */
	public void setDescription(String desc) {
		this.summaryEditor.getTextWidget().setText(desc);
	}

	/** for testing */
	public String getDescription() {
		return this.summaryEditor.getTextWidget().getText();
	}

	/** for testing - should cause dirty state */
	public void setNotes(String notes) {
		if (personalPart != null) {
			personalPart.setNotes(notes);
		}
	}
}
