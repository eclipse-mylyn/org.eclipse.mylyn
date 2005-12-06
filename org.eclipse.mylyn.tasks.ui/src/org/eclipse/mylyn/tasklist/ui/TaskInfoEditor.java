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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ui.views.DatePicker;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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

	private Button browse;

	private Text pathText;

	private ScrolledForm form;

	private Text description;

	private Text issueReportURL;

	private Text notes;

	private Spinner estimated;

	private boolean isDirty = false;

	private MylarTaskEditor parentEditor = null;

	private ITaskActivityListener TASK_LIST_LISTENER = new ITaskActivityListener() {
		public void taskActivated(ITask activeTask) {
			if (task != null && !browse.isDisposed()
					&& activeTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				browse.setEnabled(false);
			}
		}

		public void tasksActivated(List<ITask> tasks) {
			for (ITask t : tasks) {
				taskActivated(t);
			}
		}

		public void taskDeactivated(ITask deactiveTask) {
			if (task != null && !browse.isDisposed()
					&& deactiveTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				browse.setEnabled(true);
			}
		}

		public void tasklistRead() {
			// ignore
		}

		public void taskChanged(ITask updateTask) {
			if (updateTask != null && updateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				if (!description.isDisposed()) {
					description.setText(updateTask.getDescription(false));
				}
				if (!pathText.isDisposed() && !updateTask.getContextPath().equals(task.getContextPath())) {
					pathText.setText("<Mylar_Dir>/" + task.getContextPath());
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
		task.setIssueReportURL(issueReportURL.getText());
		String note = notes.getText();
		task.setNotes(note);
		task.setEstimatedTimeHours(estimated.getSelection());
		// links.clear();
		// TableItem[] items = table.getItems();
		// for (int i = 0; i < items.length; i++) {
		// if (items[i].getData() instanceof String) {
		// links.add((String)items[i].getData());
		// }
		// }
		// "<MylarDir>/" + res + ".xml"
		String path = pathText.getText();
		path = path.substring(path.indexOf('/') + 1, path.lastIndexOf('.'));
		task.setContextPath(path);
		if (datePicker != null && datePicker.getDate() != null) {
			task.setReminderDate(datePicker.getDate().getTime());
		}
		refreshTaskListView(task);
		MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(task);
		markDirty(false);
	}

	@Override
	public void doSaveAs() {
		// don't support saving
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
			MylarPlugin.log(e, "content failed");
		}
		return null;
	}

	private void createOverviewSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(DESCRIPTION_OVERVIEW);
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
		// td.colspan = 2;
		description.setLayoutData(td);
		if (!task.isLocal()) {
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
		issueReportURL = toolkit.createText(container, task.getIssueReportURL(), SWT.BORDER);
		issueReportURL.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		// issueReportURL.setForeground(HYPERLINK);

		if (!task.isLocal()) {
			issueReportURL.setEnabled(false);
		} else {
			issueReportURL.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					markDirty(true);
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
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
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
		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		container.setLayout(layout);

		Label label = toolkit.createLabel(container, "Reminder:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		datePicker = new DatePicker(container, SWT.NULL);

		Calendar calendar = Calendar.getInstance();
		if (task.getReminderDate() != null) {
			calendar.setTime(task.getReminderDate());
			datePicker.setDate(calendar);
		}
		datePicker.setBackground(new Color(Display.getDefault(), 255, 255, 255));
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				TaskInfoEditor.this.markDirty(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});
		label = toolkit.createLabel(container, " ");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		label = toolkit.createLabel(container, "Estimated time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		Composite estimatedComposite = toolkit.createComposite(container);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 2;
		gridLayout.makeColumnsEqualWidth = false;
		estimatedComposite.setLayout(gridLayout);

		estimated = new Spinner(estimatedComposite, SWT.BORDER);
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

		GridData estimatedSpinnerGridData = new org.eclipse.swt.layout.GridData();
		estimatedSpinnerGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		estimatedSpinnerGridData.grabExcessHorizontalSpace = true;
		estimatedSpinnerGridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		estimated.setData(estimatedSpinnerGridData);

		label = toolkit.createLabel(estimatedComposite, "hours ");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		GridData hoursLabelGridData = new org.eclipse.swt.layout.GridData();
		hoursLabelGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		hoursLabelGridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		label.setData(hoursLabelGridData);

		// Temp hack because couldn't get the estimatedComposite above to span
		// two columns
		label = toolkit.createLabel(container, " ");

		label = toolkit.createLabel(container, "Elapsed time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		String reminderDateString = "";
		try {
			if (task.getReminderDate() != null) {
				reminderDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(task.getReminderDate());
			}
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "Could not format reminder date", true);
		}
		
		Text text2 = toolkit.createText(container, reminderDateString, SWT.BORDER);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		td.colspan = 2;
		text2.setLayoutData(td);
		text2.setEditable(false);
		text2.setEnabled(false);

		label = toolkit.createLabel(container, "Creation date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String creationDateString = "";
		try {
			creationDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCreationDate());
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "Could not format creation date", true);
		}

		Text creationDate = toolkit.createText(container, creationDateString, SWT.BORDER);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		td.colspan = 2;
		creationDate.setLayoutData(td);
		creationDate.setEditable(false);
		creationDate.setEnabled(false);

		label = toolkit.createLabel(container, "Completion date:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		String completionDateString = "";
		if (task.getCompletionDate() != null) {
			try {
				completionDateString = DateFormat.getDateInstance(DateFormat.LONG).format(task.getCompletionDate());
			} catch (RuntimeException e) {
				MylarPlugin.fail(e, "Could not format date", true);
			}
		}
		Text endDate = toolkit.createText(container, completionDateString, SWT.BORDER);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		td.colspan = 2;
		endDate.setLayoutData(td);
		endDate.setEditable(false);
		endDate.setEnabled(false);
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
		layout.numColumns = 3;
		container.setLayout(layout);

		Label l = toolkit.createLabel(container, "Task Handle:");
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		Text handle = toolkit.createText(container, task.getHandleIdentifier(), SWT.BORDER);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		handle.setLayoutData(td);
		handle.setEditable(false);
		handle.setEnabled(false);

		Label l2 = toolkit.createLabel(container, "Task context file:");
		l2.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		pathText = toolkit.createText(container, "<Mylar_Dir>/" + task.getContextPath() + ".xml", SWT.BORDER);
		pathText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		pathText.setEditable(false);
		pathText.setEnabled(false);

		browse = toolkit.createButton(container, "Change", SWT.PUSH | SWT.CENTER);
		if (task.isActive()) {
			browse.setEnabled(false);
		} else {
			browse.setEnabled(true);
		}
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (task.isActive()) {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Task Message",
							"Task can not be active when changing taskscape");
				} else {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					String[] ext = { "*.xml" };
					dialog.setFilterExtensions(ext);

					String mylarDir = MylarPlugin.getDefault().getMylarDataDirectory() + "/";
					mylarDir = mylarDir.replaceAll("\\\\", "/");
					dialog.setFilterPath(mylarDir);

					String res = dialog.open();
					if (res != null) {
						res = res.replaceAll("\\\\", "/");
						pathText.setText("<MylarDir>/" + res + ".xml");
						markDirty(true);
					}
				}
			}
		});
		toolkit.createLabel(container, "");
		l = toolkit.createLabel(container, "Go to Mylar Preferences to change <Mylar_Dir>");
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	}

	private void refreshTaskListView(ITask task) {
		if (TaskListView.getDefault() != null)
			TaskListView.getDefault().notifyTaskDataChanged(task);
	}

	private void markDirty(boolean dirty) {
		isDirty = dirty;
		if (parentEditor != null) {
			parentEditor.updatePartName();
		}
		return;
	}

	public void setParentEditor(MylarTaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}
}
