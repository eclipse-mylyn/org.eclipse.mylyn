/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin.PriorityLevel;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.Task.TaskStatus;
import org.eclipse.mylar.tasklist.ui.views.DatePicker;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.EditorPart;

/**
 * For details on forms, go to:
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/pde-ui-home/working/EclipseForms/EclipseForms.html
 * 
 * @author Mik Kersten
 * @author Ken Sueda (initial prototype)
 * @author Rob Elves (added additional fields)
 */
public class TaskInfoEditor extends EditorPart {

	private static final String DESCRIPTION_OVERVIEW = "Task Summary";

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

	private Button completedCheckbox;

	private Text pathText;

	private Text endDate;

	private ScrolledForm form;

	private Text description;

	private Text issueReportURL;

	private Combo priorityCombo;
	
	private Combo statusCombo;

	private Text notes;

	private Spinner estimated;

	private boolean isDirty = false;

	private MylarTaskEditor parentEditor = null;

	private ITaskActivityListener TASK_LIST_LISTENER = new ITaskActivityListener() {
		public void taskActivated(ITask activeTask) {
			// if (task != null && !browse.isDisposed()
			// &&
			// activeTask.getHandleIdentifier().equals(task.getHandleIdentifier()))
			// {
			// browse.setEnabled(false);
			// }
		}

		public void tasksActivated(List<ITask> tasks) {
			for (ITask t : tasks) {
				taskActivated(t);
			}
		}

		public void taskDeactivated(ITask deactiveTask) {
			// if (task != null && !browse.isDisposed()
			// &&
			// deactiveTask.getHandleIdentifier().equals(task.getHandleIdentifier()))
			// {
			// browse.setEnabled(true);
			// }
		}

		public void tasklistRead() {
			// ignore
		}

		public void taskChanged(ITask updateTask) {
			if (updateTask != null && updateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				if (!description.isDisposed()) {					
					description.setText(updateTask.getDescription(false));					
//					TaskInfoEditor.this.setPartName(updateTask.getDescription(true));
					parentEditor.changeTitle();
				}
				if (!priorityCombo.isDisposed()) {
					int selectionIndex = priorityCombo.indexOf(updateTask.getPriority());
					priorityCombo.select(selectionIndex);
				}
				if (!statusCombo.isDisposed()) {
					int selectionIndex = statusCombo.indexOf(updateTask.getStatus().toString());
					statusCombo.select(selectionIndex);					
				}
				if (updateTask.isLocal() && !endDate.isDisposed()) {
					endDate.setText(getTaskDateString(updateTask));
				} 

			}
		}

		public void tasklistModified() {
			// TODO Auto-generated method stub

		}
	};

	public TaskInfoEditor() {
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
		MylarTaskListPlugin.getTaskListManager().addListener(TASK_LIST_LISTENER);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		String label = description.getText();
		task.setDescription(label);
		task.setUrl(issueReportURL.getText());
		String note = notes.getText();
		task.setNotes(note);
		task.setEstimatedTimeHours(estimated.getSelection());
		if (datePicker != null && datePicker.getDate() != null) {
			task.setReminderDate(datePicker.getDate().getTime());
		} else {
			task.setReminderDate(null);
		}
		task.setPriority(priorityCombo.getItem(priorityCombo.getSelectionIndex()));
		
		// Method not implemented yet
//		task.setStatus(statusCombo.getItem(statusCombo.getSelectionIndex()));
		
//		MylarTaskListPlugin.getTaskListManager().setStatus(task, statusCombo.getItem(statusCombo.getSelectionIndex()));

		refreshTaskListView(task);
		MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(task);

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
		form.getBody().setLayout(new TableWrapLayout());
		editorComposite = form.getBody();

		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 10;
		layout.topMargin = 10;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		editorComposite.setLayout(layout);
		// editorComposite.setLayoutData(new
		// TableWrapData(TableWrapData.FILL_GRAB));

		// Put the info onto the editor
		createContent(editorComposite, toolkit);
		form.setFocus();
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
			createOverviewSection(parent, toolkit);
			createPlanningSection(parent, toolkit);
			createDocumentationSection(parent, toolkit);
			// createRelatedLinksSection(parent, toolkit);
			createDetailsSection(parent, toolkit);
		} catch (SWTException e) {
			MylarStatusHandler.log(e, "content failed");
		}
		return null;
	}

	private void createOverviewSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);		
		section.setText(DESCRIPTION_OVERVIEW);
		
		if(!task.isLocal()) {
			section.setDescription("To modify these fields use the repository editor.");
		}
		
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
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
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Label l = toolkit.createLabel(container, "Description:");
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		description = toolkit.createText(container, task.getDescription(true), SWT.BORDER);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		
		description.setLayoutData(td);
		if (!task.isLocal()) {
			description.setEnabled(false);
		} else {
			description.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					markDirty(true);
					
				}

				public void keyReleased(KeyEvent e) {
					// ignore
					
				}   });

		}

		Label urlLabel = toolkit.createLabel(container, "Web Link:");
		urlLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		issueReportURL = toolkit.createText(container, task.getUrl(), SWT.BORDER);
		issueReportURL.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		// issueReportURL.setForeground(HYPERLINK);

		if (!task.isLocal()) {
			issueReportURL.setEditable(false);
		} else {
			issueReportURL.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					markDirty(true);
				}
			});
		}
		

		Label label = toolkit.createLabel(container, "Status:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		
		Composite statusComposite = toolkit.createComposite(container);
		statusComposite.setLayout(new GridLayout(2, false));
		
		statusCombo = new Combo(statusComposite, SWT.DROP_DOWN);

		// Populate the combo box
		for (TaskStatus status : Task.TaskStatus.values()) {
			statusCombo.add(status.toString());
		}

		int selectionIndex = statusCombo.indexOf(task.getStatus().toString());
		statusCombo.select(selectionIndex);

		if (!task.isLocal()) {
			statusCombo.setEnabled(false);
		} else {
			statusCombo.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TaskInfoEditor.this.markDirty(true);

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing

				}
			});
		}
		

		priorityCombo = new Combo(statusComposite, SWT.DROP_DOWN);

		// Populate the combo box with priority levels
		for (String priorityLevel : TaskListView.PRIORITY_LEVELS) {
			priorityCombo.add(priorityLevel);
		}
		
		int prioritySelectionIndex = priorityCombo.indexOf(task.getPriority());
		priorityCombo.select(prioritySelectionIndex);

		if (!task.isLocal()) {
			priorityCombo.setEnabled(false);
		} else {
			priorityCombo.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TaskInfoEditor.this.markDirty(true);

				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing

				}
			});

		}
	}

	private void createDocumentationSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText("Documentation");
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
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
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		notes = toolkit.createText(container, task.getNotes(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		TableWrapData tablewrap = new TableWrapData(TableWrapData.FILL_GRAB);
		tablewrap.heightHint = 200;
		tablewrap.grabVertical = true;
		notes.setLayoutData(tablewrap);
		notes.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
			}
		});

		toolkit.paintBordersFor(container);
	}

	private void createPlanningSection(Composite parent, FormToolkit toolkit) {

		Section section = toolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR);
		section.setText("Planning");
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
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
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 5;
		layout.rightMargin = 50;
		sectionClient.setLayout(layout);
		sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		// Reminder
		Label label = toolkit.createLabel(sectionClient, "Reminder:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		datePicker = new DatePicker(sectionClient, SWT.FILL);

		Calendar calendar = Calendar.getInstance();
		if (task.getReminderDate() != null) {
			calendar.setTime(task.getReminderDate());
			datePicker.setDate(calendar);
		}
		datePicker.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				TaskInfoEditor.this.markDirty(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});

		datePicker.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		removeReminder = toolkit.createButton(sectionClient, "Clear", SWT.PUSH
				| SWT.CENTER);
		if (task.isActive()) {
			removeReminder.setEnabled(false);
		} else {
			removeReminder.setEnabled(true);
		}
		removeReminder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				datePicker.setDate(null);
				datePicker.setDateText("");
				TaskInfoEditor.this.markDirty(true);
			}
		});

		// 2 Blank columns after Reminder clear button
		toolkit.createLabel(sectionClient, "");
		toolkit.createLabel(sectionClient, "");

		// Estimated time

		label = toolkit.createLabel(sectionClient, "Estimated time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		estimated = new Spinner(sectionClient, SWT.BORDER);
		estimated.setSelection(task.getEstimateTimeHours());
		estimated.setDigits(0);
		estimated.setMaximum(100);
		estimated.setMinimum(0);
		estimated.setIncrement(1);
		estimated.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				TaskInfoEditor.this.markDirty(true);
			}
		});

		estimated.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		label = toolkit.createLabel(sectionClient, "hours ");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		
		// Creation date
		label = toolkit.createLabel(sectionClient, "Creation date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String creationDateString = "";
		try {
			creationDateString = DateFormat.getDateInstance(DateFormat.LONG)
					.format(task.getCreationDate());
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format creation date", true);
		}

		Text creationDate = toolkit.createText(sectionClient,
				creationDateString, SWT.BORDER);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		creationDate.setLayoutData(td);
		creationDate.setEditable(false);
		creationDate.setEnabled(false);
		
		// Elapsed Time
		label = toolkit.createLabel(sectionClient, "Elapsed time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String elapsedTimeString = "0";
		try {
			elapsedTimeString = DateUtil.getFormattedDuration(task
					.getElapsedTime(), true);
			if (elapsedTimeString.equals(""))
				elapsedTimeString = "0";
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format reminder date", true);
		}

		Text reminderText = toolkit.createText(sectionClient,
				elapsedTimeString, SWT.BORDER);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		reminderText.setLayoutData(td);
		reminderText.setEditable(false);

	

		// Blank Column
		toolkit.createLabel(sectionClient, "");

		// Completion date
		label = toolkit.createLabel(sectionClient, "Completion date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String completionDateString = "";
		if (task.isCompleted()) {
			completionDateString = getTaskDateString(task);
		}
		endDate = toolkit.createText(sectionClient, completionDateString,
				SWT.BORDER);		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		endDate.setLayoutData(td);
		endDate.setEditable(false);
		endDate.setEnabled(false);

	}

	
// private String getTaskDateString(ITask task) {
//
//		String completionDateString = "";
//		try {
//			completionDateString = DateFormat.getDateInstance(DateFormat.LONG)
//					.format(task.getCompletionDate());
//		} catch (RuntimeException e) {
//			MylarStatusHandler.log(e, "Could not format date");
//			return completionDateString;
//		}
//		return completionDateString;
//	}
	
	private String getTaskDateString(ITask task) {

		String completionDateString = "";
		try {
			completionDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCompletionDate());
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not format date", true);
			return completionDateString;
		}
		return completionDateString;
	}

	private void createDetailsSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Resources");
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
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
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		// Label l = toolkit.createLabel(container, "Task Handle:");
		// l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		// Text handle = toolkit.createText(container,
		// task.getHandleIdentifier(), SWT.BORDER);
		// TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		// td.colspan = 2;
		// handle.setLayoutData(td);
		// handle.setEditable(false);
		// handle.setEnabled(false);

		Label l2 = toolkit.createLabel(container, "Task context file:");
		l2.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		File contextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		// String contextPath = MylarPlugin.getDefault().getDataDirectory()
		// + '/' + task.getContextPath() +
		// MylarContextManager.CONTEXT_FILE_EXTENSION;
		if (contextFile != null) {
			pathText = toolkit.createText(container, contextFile.getAbsolutePath(), SWT.BORDER);
			pathText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
			pathText.setEditable(false);
			pathText.setEnabled(false);
		}

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

	private void refreshTaskListView(ITask task) {
		if (TaskListView.getDefault() != null)
			TaskListView.getDefault().notifyTaskDataChanged(task);
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
		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_LIST_LISTENER);
	}
	
	
	
}
