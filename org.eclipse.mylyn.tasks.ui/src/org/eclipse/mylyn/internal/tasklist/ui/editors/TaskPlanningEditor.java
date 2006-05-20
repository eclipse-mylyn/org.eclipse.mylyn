/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.core.util.DateUtil;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.actions.NewLocalTaskAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskEditorCopyAction;
import org.eclipse.mylar.internal.tasklist.ui.views.DatePicker;
import org.eclipse.mylar.internal.tasklist.ui.views.RetrieveTitleFromUrlJob;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListChangeListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.Task.PriorityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.EditorPart;

/**
 * For details on forms, go to:
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/pde-ui-home/working/EclipseForms/EclipseForms.html
 * 
 * @author Mik Kersten
 * @author Ken Sueda (initial prototype)
 * @author Rob Elves
 */
public class TaskPlanningEditor extends EditorPart {

	private static final String LABEL_SCHEDULE = "Scheduled for:";

	private static final String DESCRIPTION_ESTIMATED = "Time that the task has been actively worked on.\n Inactivity timeout is "
			+ MylarPlugin.getContextManager().getInactivityTimeout() + " seconds.";

	private static final String LABEL_INCOMPLETE = "Incomplete";

	private static final String LABEL_COMPLETE = "Complete";

	private static final String LABEL_PLAN = "Personal Planning";

	private static final String NO_TIME_ELAPSED = "0 seconds";

	private static final String LABEL_OVERVIEW = "Task Info";

	private static final String LABEL_NOTES = "Notes";

	private DatePicker datePicker;

	private ITask task;

	private TaskEditorInput editorInput;

	private Composite editorComposite;

	private TaskEditorCopyAction copyAction;

	private RetargetAction pasteAction;

	private RetargetAction cutAction;

	private static final String cutActionDefId = "org.eclipse.ui.edit.cut";

	private static final String pasteActionDefId = "org.eclipse.ui.edit.paste";

	private Button removeReminder;

	private Text pathText;

	private Text endDate;

	private ScrolledForm form;

	private Text description;

	private Text issueReportURL;

	private Combo priorityCombo;

	private Combo statusCombo;

	private Text notes;

	private Spinner estimated;

	private Button getDescButton;

	private boolean isDirty = false;

	private MylarTaskEditor parentEditor = null;

	private ITaskListChangeListener TASK_LIST_LISTENER = new ITaskListChangeListener() {

		public void localInfoChanged(final ITask updateTask) {
			if (updateTask != null && task != null
					&& updateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							
							if( datePicker != null && !datePicker.isDisposed() && updateTask.getReminderDate() != null) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(updateTask.getReminderDate());
								datePicker.setDate(cal);
							}
							
							if (description == null)
								return;
							if (!description.isDisposed()) {
								description.setText(updateTask.getDescription());
								parentEditor.changeTitle();
							}
							if (!priorityCombo.isDisposed()) {
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
							if (!(updateTask instanceof AbstractRepositoryTask) && !endDate.isDisposed()) {
								endDate.setText(getTaskDateString(updateTask));
							}
						}
					});
				}
			}
		}

		public void repositoryInfoChanged(ITask task) {
			localInfoChanged(task);
		}

		public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
			// ignore
		}

		public void taskDeleted(ITask task) {
			// ignore
		}

		public void containerAdded(AbstractTaskContainer container) {
			// ignore
		}

		public void containerDeleted(AbstractTaskContainer container) {
			// ignore
		}

		public void taskAdded(ITask task) {
			// ignore
		}

		public void containerInfoChanged(AbstractTaskContainer container) {
			// ignore
		}

	};

	public TaskPlanningEditor() {
		super();
		cutAction = new RetargetAction(ActionFactory.CUT.getId(), WorkbenchMessages.Workbench_cut);
		cutAction.setToolTipText(WorkbenchMessages.Workbench_cutToolTip);
		cutAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
		cutAction.setAccelerator(SWT.CTRL | 'x');
		cutAction.setActionDefinitionId(cutActionDefId);

		pasteAction = new RetargetAction(ActionFactory.PASTE.getId(), WorkbenchMessages.Workbench_paste);
		pasteAction.setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);
		pasteAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setDisabledImageDescriptor(WorkbenchImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setAccelerator(SWT.CTRL | 'v');
		pasteAction.setActionDefinitionId(pasteActionDefId);

		copyAction = new TaskEditorCopyAction();
		copyAction.setText(WorkbenchMessages.Workbench_copy);
		copyAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setAccelerator(SWT.CTRL | 'c');

		copyAction.setEnabled(false);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addChangeListener(TASK_LIST_LISTENER);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!(task instanceof AbstractRepositoryTask)) {
			String label = description.getText();
			// task.setDescription(label);
			MylarTaskListPlugin.getTaskListManager().getTaskList().renameTask((Task) task, label);

			// TODO: refactor mutation into TaskList?
			task.setUrl(issueReportURL.getText());
			String priorityDescription = priorityCombo.getItem(priorityCombo.getSelectionIndex());
			PriorityLevel level = PriorityLevel.fromDescription(priorityDescription);
			if (level != null) {
				task.setPriority(level.toString());
			}
			if (statusCombo.getSelectionIndex() == 0) {
				task.setCompleted(true);
			} else {
				task.setCompleted(false);
			}
		}

		String note = notes.getText();
		task.setNotes(note);
		task.setEstimatedTimeHours(estimated.getSelection());
		if (datePicker != null && datePicker.getDate() != null) {
			MylarTaskListPlugin.getTaskListManager().setReminder(task, datePicker.getDate().getTime());
			// task.setReminderDate(datePicker.getDate().getTime());
		} else {
			// task.setReminderDate(null);
			MylarTaskListPlugin.getTaskListManager().setReminder(task, null);
		}
		MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);

		// Method not implemented yet
		// task.setStatus(statusCombo.getItem(statusCombo.getSelectionIndex()));

		// MylarTaskListPlugin.getTaskListManager().setStatus(task,
		// statusCombo.getItem(statusCombo.getSelectionIndex()));

		// refreshTaskListView(task);

		markDirty(false);
	}

	@Override
	public void doSaveAs() {
		// don't support saving as
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof TaskEditorInput)) {
			throw new PartInitException("Invalid Input: Must be TaskEditorInput");
		}
		setSite(site);
		setInput(input);
		editorInput = (TaskEditorInput) input;
		setPartName(editorInput.getLabel());
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
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		// String trucatedDescription = task.getDescription();
		// int maxLength = 50;
		// if (trucatedDescription.length() > maxLength) {
		// trucatedDescription = trucatedDescription.substring(0, maxLength) +
		// "...";
		// }
		// form.setText(trucatedDescription);
		form.setText(task.getDescription());

		editorComposite = form.getBody();
		editorComposite.setLayout(new GridLayout());
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		// Put the info onto the editor
		createContent(editorComposite, toolkit);
		// form.setFocus();
		if (description != null && NewLocalTaskAction.DESCRIPTION_DEFAULT.equals(description.getText())) {
			description.setSelection(0);
			description.setFocus();
		}
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	public Control getControl() {
		return form;
	}

	public void setTask(ITask task) throws Exception {
		if (task == null)
			throw new Exception("ITask object is null.");
		this.task = task;
	}

	private Composite createContent(Composite parent, FormToolkit toolkit) {
		TaskEditorInput taskEditorInput = (TaskEditorInput) getEditorInput();

		task = taskEditorInput.getTask();
		if (task == null) {
			MessageDialog.openError(parent.getShell(), "No such task", "No task exists with this id");
			return null;
		}

		try {
			if (!(task instanceof AbstractRepositoryTask)) {
				createSummarySection(parent, toolkit);
			}
			createPlanningSection(parent, toolkit);
			createNotesSection(parent, toolkit);
			// // createRelatedLinksSection(parent, toolkit);
			createResourcesSection(parent, toolkit);
		} catch (SWTException e) {
			MylarStatusHandler.log(e, "content failed");
		}
		return null;
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_OVERVIEW);
		section.setExpanded(true);
		// if (task instanceof AbstractRepositoryTask) {
		// section.setDescription("To modify these fields use the repository
		// editor.");
		// }

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
		GridLayout compLayout = new GridLayout();
		compLayout.numColumns = 2;
		container.setLayout(compLayout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l = toolkit.createLabel(container, "Description:");
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		description = toolkit.createText(container, task.getDescription(), SWT.NONE);
		description.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(container);

		if (task instanceof AbstractRepositoryTask) {
			description.setEnabled(false);
		} else {
			description.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					markDirty(true);
				}
			});
		}

		Label urlLabel = toolkit.createLabel(container, "Web Link:");
		urlLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		Composite urlComposite = toolkit.createComposite(container);
		GridLayout urlLayout = new GridLayout(2, false);
		urlLayout.marginWidth = 1;
		urlComposite.setLayout(urlLayout);
		GridData urlGridData = new GridData(GridData.FILL_HORIZONTAL);
		urlComposite.setLayoutData(urlGridData);

		issueReportURL = toolkit.createText(urlComposite, task.getUrl(), SWT.NONE);
		GridData gridLayout = new GridData(GridData.FILL_HORIZONTAL);
		issueReportURL.setLayoutData(gridLayout);

		if (task instanceof AbstractRepositoryTask) {
			issueReportURL.setEditable(false);
		} else {
			issueReportURL.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					markDirty(true);
				}
			});
		}

		getDescButton = toolkit.createButton(urlComposite, "Get Description", SWT.PUSH);
		getDescButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		toolkit.paintBordersFor(urlComposite);
		setButtonStatus();

		issueReportURL.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				setButtonStatus();
			}

			public void keyReleased(KeyEvent e) {
				setButtonStatus();
			}
		});

		getDescButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				retrieveTaskDescription(issueReportURL.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label label = toolkit.createLabel(container, "Status:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		Composite statusComposite = toolkit.createComposite(container);
		statusComposite.setLayout(new GridLayout(2, false));

		priorityCombo = new Combo(statusComposite, SWT.READ_ONLY);

		// Populate the combo box with priority levels
		for (String priorityLevel : TaskListView.PRIORITY_LEVEL_DESCRIPTIONS) {
			priorityCombo.add(priorityLevel);
		}

		PriorityLevel level = PriorityLevel.fromString(task.getPriority());
		if (level != null) {
			int prioritySelectionIndex = priorityCombo.indexOf(level.getDescription());
			priorityCombo.select(prioritySelectionIndex);
		}

		if (task instanceof AbstractRepositoryTask) {
			priorityCombo.setEnabled(false);
		} else {
			priorityCombo.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TaskPlanningEditor.this.markDirty(true);

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing

				}
			});
		}

		statusCombo = new Combo(statusComposite, SWT.READ_ONLY);

		statusCombo.add(LABEL_COMPLETE);
		statusCombo.add(LABEL_INCOMPLETE);
		if (task.isCompleted()) {
			statusCombo.select(0);
		} else {
			statusCombo.select(1);
		}
		if (task instanceof AbstractRepositoryTask) {
			statusCombo.setEnabled(false);
		} else {
			statusCombo.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (statusCombo.getSelectionIndex() == 0) {
						task.setCompleted(true);
					} else {
						task.setCompleted(false);
					}
					TaskPlanningEditor.this.markDirty(true);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing

				}
			});
		}
		// statusCombo.setEnabled(false);

	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {

		try {
			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(issueReportURL.getText()) {

				@Override
				protected void setTitle(final String pageTitle) {
					description.setText(pageTitle);
					TaskPlanningEditor.this.markDirty(true);
				}

			};
			job.schedule();

		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not open task web page", false);
		}
	}

	/**
	 * Sets the Get Description button enabled or not depending on whether there
	 * is a URL specified
	 */
	protected void setButtonStatus() {
		String url = issueReportURL.getText();

		if (url.length() > 10 && (url.startsWith("http://") || url.startsWith("https://"))) {
			// String defaultPrefix =
			// MylarPlugin.getDefault().getPreferenceStore().getString(
			// TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
			// if (url.equals(defaultPrefix)) {
			// getDescButton.setEnabled(false);
			// } else {
			getDescButton.setEnabled(true);
			// }
		} else {
			getDescButton.setEnabled(false);
		}
	}

	private void createNotesSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(LABEL_NOTES);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		container.setLayout(new GridLayout());

		notes = toolkit.createText(container, task.getNotes(), SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		notes.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		GridData notesDataLayout = new GridData(GridData.FILL_BOTH);
		notes.setLayoutData(notesDataLayout);
		notes.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
			}
		});

		toolkit.paintBordersFor(container);
	}

	private void createPlanningSection(Composite parent, FormToolkit toolkit) {

		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
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
		layout.numColumns = 6;
		layout.makeColumnsEqualWidth = false;
		sectionClient.setLayout(layout);
		GridData clientDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		sectionClient.setLayoutData(clientDataLayout);

		// Reminder
		Label label = toolkit.createLabel(sectionClient, LABEL_SCHEDULE);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		datePicker = new DatePicker(sectionClient, SWT.NONE, DatePicker.LABEL_CHOOSE);

		Calendar calendar = Calendar.getInstance();
		if (task.getReminderDate() != null) {
			calendar.setTime(task.getReminderDate());
			datePicker.setDate(calendar);
		}

		datePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				task.setReminded(false);
				TaskPlanningEditor.this.markDirty(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});
		datePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		removeReminder = toolkit.createButton(sectionClient, "Clear", SWT.PUSH | SWT.CENTER);
		removeReminder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				datePicker.setDate(null);
				task.setReminded(false);
				TaskPlanningEditor.this.markDirty(true);
			}
		});

		// 1 Blank column after Reminder clear button
		Label dummy = toolkit.createLabel(sectionClient, "");
		GridData dummyLabelDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		dummyLabelDataLayout.horizontalSpan = 1;
		dummyLabelDataLayout.widthHint = 30;
		dummy.setLayoutData(dummyLabelDataLayout);

		// Creation date
		label = toolkit.createLabel(sectionClient, "Creation date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String creationDateString = "";
		try {
			creationDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCreationDate());
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format creation date", true);
		}

		Text creationDate = toolkit.createText(sectionClient, creationDateString, SWT.NONE);
		GridData creationDateDataLayout = new GridData();
		creationDateDataLayout.widthHint = 120;
		creationDate.setLayoutData(creationDateDataLayout);
		creationDate.setEditable(false);
		creationDate.setEnabled(true);

		// Estimated time

		label = toolkit.createLabel(sectionClient, "Estimated time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		estimated = new Spinner(sectionClient, SWT.NONE);
		estimated.setSelection(task.getEstimateTimeHours());
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
		GridData estimatedDataLayout = new GridData();
		estimatedDataLayout.widthHint = 110;
		estimated.setLayoutData(estimatedDataLayout);

		label = toolkit.createLabel(sectionClient, "hours ");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		// 1 Blank column
		Label blankLabel2 = toolkit.createLabel(sectionClient, "");
		GridData blankLabl2Layout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		blankLabl2Layout.horizontalSpan = 1;
		blankLabl2Layout.widthHint = 25;
		blankLabel2.setLayoutData(blankLabl2Layout);

		// Completion date
		label = toolkit.createLabel(sectionClient, "Completion date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String completionDateString = "";
		if (task.isCompleted()) {
			completionDateString = getTaskDateString(task);
		}
		endDate = toolkit.createText(sectionClient, completionDateString, SWT.NONE);
		GridData endDateDataLayout = new GridData();
		endDateDataLayout.widthHint = 120;
		endDate.setLayoutData(endDateDataLayout);

		endDate.setEditable(false);
		endDate.setEnabled(true);
		toolkit.paintBordersFor(sectionClient);

		// Elapsed Time

		label = toolkit.createLabel(sectionClient, "Elapsed time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		label.setToolTipText(DESCRIPTION_ESTIMATED);

		Composite elapsedComposite = toolkit.createComposite(sectionClient);
		GridLayout elapsedLayout = new GridLayout();
		elapsedLayout.numColumns = 2;
		elapsedLayout.marginWidth = 1;
		elapsedLayout.makeColumnsEqualWidth = false;
		elapsedComposite.setLayout(elapsedLayout);
		GridData elapsedCompositeGridData = new GridData();
		elapsedCompositeGridData.horizontalSpan = 5;
		elapsedComposite.setLayoutData(elapsedCompositeGridData);

		String elapsedTimeString = NO_TIME_ELAPSED;
		try {
			elapsedTimeString = DateUtil.getFormattedDuration(task.getElapsedTime(), true);
			if (elapsedTimeString.equals(""))
				elapsedTimeString = NO_TIME_ELAPSED;
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format elapsed time", true);
		}

		final Text elapsedTimeText = toolkit.createText(elapsedComposite, elapsedTimeString, SWT.NONE);
		GridData td = new GridData(GridData.FILL_HORIZONTAL);
		elapsedTimeText.setLayoutData(td);
		elapsedTimeText.setEditable(false);

		// Refresh Button
		Button timeRefresh = toolkit.createButton(elapsedComposite, "Refresh", SWT.PUSH | SWT.CENTER);

		timeRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String elapsedTimeString = NO_TIME_ELAPSED;
				try {
					elapsedTimeString = DateUtil.getFormattedDuration(task.getElapsedTime(), true);
					if (elapsedTimeString.equals("")) {
						elapsedTimeString = NO_TIME_ELAPSED;
					}

				} catch (RuntimeException e1) {
					MylarStatusHandler.fail(e1, "Could not format elapsed time", true);
				}
				elapsedTimeText.setText(elapsedTimeString);
			}
		});

		toolkit.paintBordersFor(elapsedComposite);
	}

	private String getTaskDateString(ITask task) {

		if (task == null)
			return "";
		if (task.getCompletionDate() == null)
			return "";

		String completionDateString = "";
		try {
			completionDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCompletionDate());
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format date", true);
			return completionDateString;
		}
		return completionDateString;
	}

	private void createResourcesSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
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

		Label l2 = toolkit.createLabel(container, "Task context file:");
		l2.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		File contextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		// String contextPath = MylarPlugin.getDefault().getDataDirectory()
		// + '/' + task.getContextPath() +
		// MylarContextManager.CONTEXT_FILE_EXTENSION;
		if (contextFile != null) {
			pathText = toolkit.createText(container, contextFile.getAbsolutePath(), SWT.NONE);
			pathText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			pathText.setEditable(false);
			pathText.setEnabled(false);
		}
		toolkit.paintBordersFor(container);

		// browse = toolkit.createButton(container, "Change", SWT.PUSH |
		// SWT.CENTER);
		// if (task.isActive()) {
		// browse.setEnabled(false);
		// } else {
		// browse.setEnabled(true);
		// }
		// browse.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// if (task.isActive()) {
		// MessageDialog.openInformation(Display.getDefault().getActiveShell(),
		// "Task Message",
		// "Task can not be active when changing taskscape");
		// } else {
		// FileDialog dialog = new
		// FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		// String[] ext = { "*.xml" };
		// dialog.setFilterExtensions(ext);
		//
		// String mylarDir = MylarPlugin.getDefault().getDataDirectory() + "/";
		// mylarDir = mylarDir.replaceAll("\\\\", "/");
		// dialog.setFilterPath(mylarDir);
		//
		// String res = dialog.open();
		// if (res != null) {
		// res = res.replaceAll("\\\\", "/");
		// pathText.setText("<MylarDir>/" + res + ".xml");
		// markDirty(true);
		// }
		// }
		// }
		// });
		// toolkit.createLabel(container, "");
		// l = toolkit.createLabel(container, "Go to Task List Preferences to
		// change task context directory");
		// l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	}

	private void markDirty(boolean dirty) {
		isDirty = dirty;
		if (parentEditor != null) {
			parentEditor.markDirty();
		}
		return;
	}

	public void setParentEditor(MylarTaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public void dispose() {
		MylarTaskListPlugin.getTaskListManager().getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

	@Override
	public String toString() {
		return "(info editor for task: " + task + ")";
	}

}
