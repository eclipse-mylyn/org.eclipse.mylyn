/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleDatePicker;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Mik Kersten
 * @author Ken Sueda (initial prototype)
 * @author Rob Elves
 */
public class TaskPlanningEditor extends TaskFormPage {

	private static final String RESET = "Reset";

	private static final int WIDTH_SUMMARY = 500;

	private static final int NOTES_MINSIZE = 100;

	private static final String CLEAR = "Clear";

	private static final String LABEL_DUE = "Due:";

	private static final String LABEL_SCHEDULE = "Scheduled for:";

	public static final String LABEL_INCOMPLETE = "Incomplete";

	public static final String LABEL_COMPLETE = "Complete";

	private static final String LABEL_PLAN = "Personal Planning";

	private static final String NO_TIME_ELAPSED = "0 seconds";

	// private static final String LABEL_OVERVIEW = "Task Info";

	private static final String LABEL_NOTES = "Notes";

	private DatePicker dueDatePicker;

	private ScheduleDatePicker scheduleDatePicker;

	private AbstractTask task;

	private Composite editorComposite;

	protected static final String CONTEXT_MENU_ID = "#MylynPlanningEditor";

	private Text pathText;

	private Text endDate;

	private ScrolledForm form;

	private TextViewer summaryEditor;

	private Text issueReportURL;

	private CCombo priorityCombo;

	private CCombo statusCombo;

	private TextViewer noteEditor;

	private Spinner estimated;

	private ImageHyperlink getDescLink;

	private ImageHyperlink openUrlLink;

	private final TaskEditor parentEditor;

	private final ITaskListChangeListener TASK_LIST_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getTarget() instanceof ITask) {
					final AbstractTask updateTask = (AbstractTask) taskContainerDelta.getTarget();
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
					}
				}
			}
		}

	};

	private FormToolkit toolkit;

	private ToggleTaskActivationAction activateAction;

	private ITaskActivityListener timingListener;

	public TaskPlanningEditor(TaskEditor editor) {
		super(editor, TasksUi.ID_PLANNING_PAGE, "Planning");
		this.parentEditor = editor;
		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
	}

	/** public for testing */
	public void updateTaskData(final AbstractTask updateTask) {
		if (scheduleDatePicker != null && !scheduleDatePicker.isDisposed()) {
			if (updateTask.getScheduledForDate() != null) {
				scheduleDatePicker.setScheduledDate(updateTask.getScheduledForDate());
			} else {
				scheduleDatePicker.setScheduledDate(null);
			}
		}

		if (summaryEditor == null) {
			return;
		}

		if (!summaryEditor.getTextWidget().isDisposed()) {
			if (!summaryEditor.getTextWidget().getText().equals(updateTask.getSummary())) {
				boolean wasDirty = TaskPlanningEditor.this.isDirty;
				summaryEditor.getTextWidget().setText(updateTask.getSummary());
				TaskPlanningEditor.this.markDirty(wasDirty);
			}
			if (parentEditor != null) {
				parentEditor.updateTitle(updateTask.getSummary());
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

		String note = noteEditor.getTextWidget().getText();// notes.getText();
		task.setNotes(note);
		task.setEstimatedTimeHours(estimated.getSelection());
		if (scheduleDatePicker != null && scheduleDatePicker.getScheduledDate() != null) {
			if (task.getScheduledForDate() == null
					|| (task.getScheduledForDate() != null && !scheduleDatePicker.getScheduledDate().equals(
							task.getScheduledForDate())) || (task).getScheduledForDate().isDay()) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, scheduleDatePicker.getScheduledDate());
				(task).setReminded(false);
			}
		} else {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null);
			(task).setReminded(false);
		}
		if (dueDatePicker != null && dueDatePicker.getDate() != null) {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, dueDatePicker.getDate().getTime());
		} else {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, null);
		}
//		if (parentEditor != null) {
//			parentEditor.notifyTaskChanged();
//		}
		markDirty(false);
	}

	@Override
	public void doSaveAs() {
		// don't support saving as
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		TaskEditorInput taskEditorInput = (TaskEditorInput) getEditorInput();

		task = (AbstractTask) taskEditorInput.getTask();

		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();

		editorComposite = form.getBody();
		GridLayout editorLayout = new GridLayout();
		editorLayout.verticalSpacing = 3;
		editorComposite.setLayout(editorLayout);
		//editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (task instanceof LocalTask) {
			createSummarySection(editorComposite);
		}
		createPlanningSection(editorComposite);
		createNotesSection(editorComposite);

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

	public Control getControl() {
		return form;
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
			toolkit.adapt(text, true, true);
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

		TaskRepository repository = null;
		if (task != null && !(task instanceof LocalTask)) {
			ITask repositoryTask = task;
			repository = TasksUi.getRepositoryManager().getRepository(repositoryTask.getConnectorKind(),
					repositoryTask.getRepositoryUrl());
		}
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
		toolkit.createLabel(nameValueComp, "Priority:").setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		priorityCombo = new CCombo(nameValueComp, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(priorityCombo, true, true);
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
		toolkit.createLabel(nameValueComp, "Status:").setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		statusCombo = new CCombo(nameValueComp, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(statusCombo, true, true);
		statusCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.paintBordersFor(nameValueComp);
		statusCombo.add(LABEL_COMPLETE);
		statusCombo.add(LABEL_INCOMPLETE);
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

		String creationDateString = "";
		try {
			creationDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCreationDate());
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format creation date", e));
		}
		addNameValueComp(statusComposite, "Created:", creationDateString, SWT.FLAT | SWT.READ_ONLY);

		String completionDateString = "";
		if (task.isCompleted()) {
			completionDateString = getTaskDateString(task);
		}
		endDate = addNameValueComp(statusComposite, "Completed:", completionDateString, SWT.FLAT | SWT.READ_ONLY);
		// URL
		Composite urlComposite = toolkit.createComposite(parent);
		GridLayout urlLayout = new GridLayout(4, false);
		urlLayout.verticalSpacing = 0;
		urlLayout.marginHeight = 2;
		urlLayout.marginLeft = 5;
		urlComposite.setLayout(urlLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(urlComposite);

		Label label = toolkit.createLabel(urlComposite, "URL:");
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
		getDescLink.setToolTipText("Retrieve task description from URL");
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
		openUrlLink.setToolTipText("Open with Web Browser");
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

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {
		RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(issueReportURL.getText()) {
			@Override
			protected void titleRetrieved(String pageTitle) {
				if (!getControl().isDisposed()) {
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

		if (url.length() > 10 && (url.startsWith("http://") || url.startsWith("https://"))) {
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

	private void createPlanningSection(Composite parent) {

		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText(LABEL_PLAN);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setExpanded(true);
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 15;
		layout.makeColumnsEqualWidth = false;
		sectionClient.setLayout(layout);
		GridData clientDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		sectionClient.setLayoutData(clientDataLayout);

		Composite nameValueComp = makeComposite(sectionClient, 3);
		Label label = toolkit.createLabel(nameValueComp, LABEL_SCHEDULE);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		scheduleDatePicker = new ScheduleDatePicker(nameValueComp, task, SWT.FLAT);
		scheduleDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(scheduleDatePicker, true, true);
		toolkit.paintBordersFor(nameValueComp);

		scheduleDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		scheduleDatePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				TaskPlanningEditor.this.markDirty(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});

		ImageHyperlink clearScheduledDate = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		clearScheduledDate.setImage(CommonImages.getImage(CommonImages.REMOVE));
		clearScheduledDate.setToolTipText(CLEAR);
		clearScheduledDate.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				scheduleDatePicker.setScheduledDate(null);
				(task).setReminded(false);
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		nameValueComp = makeComposite(sectionClient, 3);
		label = toolkit.createLabel(nameValueComp, LABEL_DUE);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		dueDatePicker = new DatePicker(nameValueComp, SWT.FLAT, DatePicker.LABEL_CHOOSE, true,
				TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));

		Calendar calendar = TaskActivityUtil.getCalendar();

		if (task.getDueDate() != null) {
			calendar.setTime(task.getDueDate());
			dueDatePicker.setDate(calendar);
		}

		dueDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		dueDatePicker.addPickerSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		dueDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(dueDatePicker, true, true);
		toolkit.paintBordersFor(nameValueComp);

		ImageHyperlink clearDueDate = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		clearDueDate.setImage(CommonImages.getImage(CommonImages.REMOVE));
		clearDueDate.setToolTipText(CLEAR);
		clearDueDate.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				dueDatePicker.setDate(null);
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		if (task != null && !(task instanceof LocalTask)) {
			AbstractRepositoryConnectorUi connector = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
			if (connector != null && connector.supportsDueDates(task)) {
				dueDatePicker.setEnabled(false);
				clearDueDate.setEnabled(false);
			}
		}

		// Estimated time
		nameValueComp = makeComposite(sectionClient, 3);
		label = toolkit.createLabel(nameValueComp, "Estimated hours:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		estimated = new Spinner(nameValueComp, SWT.FLAT);
		toolkit.adapt(estimated, true, true);
		estimated.setSelection(task.getEstimatedTimeHours());
		estimated.setDigits(0);
		estimated.setMaximum(100);
		estimated.setMinimum(0);
		estimated.setIncrement(1);
		estimated.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		estimated.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.paintBordersFor(nameValueComp);
		GridData estimatedDataLayout = new GridData();
		estimatedDataLayout.widthHint = 30;
		estimated.setLayoutData(estimatedDataLayout);

		ImageHyperlink clearEstimated = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		clearEstimated.setImage(CommonImages.getImage(CommonImages.REMOVE));
		clearEstimated.setToolTipText(CLEAR);
		clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				estimated.setSelection(0);
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		// Active Time
		nameValueComp = makeComposite(sectionClient, 3);
		GridDataFactory.fillDefaults().applyTo(nameValueComp);

		label = toolkit.createLabel(nameValueComp, "Active:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText("Time working on this task");

		String elapsedTimeString = NO_TIME_ELAPSED;
		try {
			elapsedTimeString = DateUtil.getFormattedDuration(TasksUiPlugin.getTaskActivityManager().getElapsedTime(
					task), false);
			if (elapsedTimeString.equals("")) {
				elapsedTimeString = NO_TIME_ELAPSED;
			}
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format elapsed time", e));
		}

		final Text elapsedTimeText = toolkit.createText(nameValueComp, elapsedTimeString);
		elapsedTimeText.setText(elapsedTimeString);

		GridData td = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		td.grabExcessHorizontalSpace = true;
		elapsedTimeText.setLayoutData(td);
		elapsedTimeText.setEditable(false);

		timingListener = new TaskActivityAdapter() {

			@Override
			public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
				if (task.equals(TaskPlanningEditor.this.task)) {
					String elapsedTimeString = NO_TIME_ELAPSED;
					try {
						elapsedTimeString = DateUtil.getFormattedDuration(newElapsedTime, false);
						if (elapsedTimeString.equals("")) {
							elapsedTimeString = NO_TIME_ELAPSED;
						}

					} catch (RuntimeException e) {
						StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not format elapsed time", e));
					}
					final String elapsedString = elapsedTimeString;
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							if (!elapsedTimeText.isDisposed()) {
								elapsedTimeText.setText(elapsedString);
							}
						}
					});

				}
			}
		};

		TasksUiPlugin.getTaskActivityManager().addActivityListener(timingListener);

		ImageHyperlink resetActivityTimeButton = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		resetActivityTimeButton.setImage(CommonImages.getImage(CommonImages.REMOVE));
		resetActivityTimeButton.setToolTipText(RESET);
		resetActivityTimeButton.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (MessageDialog.openConfirm(TaskPlanningEditor.this.getSite().getShell(),
						"Confirm Activity Time Deletion",
						"Do you wish to reset your activity time on this task?\n\nThis will take immediate affect and can not be undone.")) {
					MonitorUi.getActivityContextManager().removeActivityTime(task.getHandleIdentifier(), 0l,
							System.currentTimeMillis());
				}
			}
		});

		toolkit.paintBordersFor(sectionClient);
	}

	private Composite makeComposite(Composite parent, int col) {
		Composite nameValueComp = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		nameValueComp.setLayout(layout);
		return nameValueComp;
	}

	private void createNotesSection(Composite parent) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(LABEL_NOTES);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
//		section.addExpansionListener(new IExpansionListener() {
//			public void expansionStateChanging(ExpansionEvent e) {
//				form.reflow(true);
//			}
//
//			public void expansionStateChanged(ExpansionEvent e) {
//				form.reflow(true);
//			}
//		});
		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		container.setLayout(new GridLayout());
		GridData notesData = new GridData(GridData.FILL_BOTH);
		notesData.grabExcessVerticalSpace = true;
		container.setLayoutData(notesData);

		TaskRepository repository = null;
		if (task != null && !(task instanceof LocalTask)) {
			ITask repositoryTask = task;
			repository = TasksUi.getRepositoryManager().getRepository(repositoryTask.getConnectorKind(),
					repositoryTask.getRepositoryUrl());
		}

		noteEditor = addTextEditor(repository, container, task.getNotes(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);

		GridDataFactory.fillDefaults().minSize(NOTES_MINSIZE, NOTES_MINSIZE).grab(true, true).applyTo(
				noteEditor.getControl());
		noteEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		noteEditor.setEditable(true);

		noteEditor.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				if (!task.getNotes().equals(noteEditor.getTextWidget().getText())) {
					markDirty(true);
				}
			}
		});

		// commentViewer.addSelectionChangedListener(new
		// ISelectionChangedListener() {
		//
		// public void selectionChanged(SelectionChangedEvent event) {
		// getSite().getSelectionProvider().setSelection(commentViewer.getSelection());
		//				
		// }});

		toolkit.paintBordersFor(container);
	}

	private String getTaskDateString(ITask task) {

		if (task == null) {
			return "";
		}
		if (task.getCompletionDate() == null) {
			return "";
		}

		String completionDateString = "";
		try {
			completionDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCompletionDate());
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format date", e));
			return completionDateString;
		}
		return completionDateString;
	}

	// TODO: unused, delete?
	void createResourcesSection(Composite parent) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText("Resources");
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		toolkit.createLabel(container, "Task context file:");
		File contextFile = ContextCore.getContextManager().getFileForContext(task.getHandleIdentifier());
		if (contextFile != null) {
			pathText = toolkit.createText(container, contextFile.getAbsolutePath(), SWT.NONE);
			pathText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			GridDataFactory.fillDefaults().hint(400, SWT.DEFAULT).applyTo(pathText);
			pathText.setEditable(false);
			pathText.setEnabled(true);
		}
		toolkit.paintBordersFor(container);
	}

	@Override
	public void dispose() {
		if (timingListener != null) {
			TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		}
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

	@Override
	public String toString() {
		return "(info editor for task: " + task + ")";
	}

	/** for testing - should cause dirty state */
	public void setNotes(String notes) {
		this.noteEditor.getTextWidget().setText(notes);
	}

	/** for testing - should cause dirty state */
	public void setDescription(String desc) {
		this.summaryEditor.getTextWidget().setText(desc);
	}

	/** for testing */
	public String getDescription() {
		return this.summaryEditor.getTextWidget().getText();
	}

	/** for testing */
	public String getFormTitle() {
		return form.getText();
	}

}
