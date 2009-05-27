/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleDatePicker;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Shawn Minto
 */
public class PersonalPart extends AbstractLocalEditorPart {

	private boolean needsDueDate;

	private String notesString;

	private RichTextEditor noteEditor;

	private static final int CONTROL_WIDTH = 135;

	private DatePicker dueDatePicker;

	private Text elapsedTimeText;

	private Spinner estimatedTime;

	private ScheduleDatePicker scheduleDatePicker;

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
									updateFromTask(updateTask);
								}
							});
						}
					}
				}
			}
		}

	};

	private final ITaskActivityListener timingListener = new TaskActivityAdapter() {

		@Override
		public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
			if (task.equals(PersonalPart.this.task)) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (elapsedTimeText != null && !elapsedTimeText.isDisposed()) {
							updateElapsedTime();
						}
					}
				});

			}
		}
	};

	private CommonTextSupport textSupport;

	private final boolean expandNotesVertically;

	private IEditorSite editorSite;

	private Composite actualTimeComposite;

	public PersonalPart(int sectionStyle, boolean expandNotesVertically) {
		super(sectionStyle, Messages.PersonalPart_Personal_Planning);
		this.expandNotesVertically = expandNotesVertically;
	}

	public void initialize(IManagedForm managedForm, TaskRepository taskRepository, AbstractTask task,
			boolean needsDueDate, IEditorSite site) {
		super.initialize(managedForm, taskRepository, task);
		this.needsDueDate = needsDueDate;

		this.textSupport = new CommonTextSupport((IHandlerService) site.getService(IHandlerService.class));
		this.textSupport.setSelectionChangedListener((TaskEditorActionContributor) site.getActionBarContributor());
		this.editorSite = site;
	}

	private boolean notesEqual() {
		if (task.getNotes() == null && notesString == null) {
			return true;
		}

		if (task.getNotes() != null && notesString != null) {
			return task.getNotes().equals(notesString);
		}
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		Assert.isNotNull(task);

		if (!notesEqual()) {
			task.setNotes(notesString);
			// XXX REFRESH THE TASLKIST
		}

		if (scheduleDatePicker != null && scheduleDatePicker.getScheduledDate() != null) {
			if (task.getScheduledForDate() == null
					|| (task.getScheduledForDate() != null && !scheduleDatePicker.getScheduledDate().equals(
							task.getScheduledForDate())) || (task).getScheduledForDate() instanceof DayDateRange) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, scheduleDatePicker.getScheduledDate());
				(task).setReminded(false);
			}
		} else {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null);
			(task).setReminded(false);
		}

		if (estimatedTime != null) {
			task.setEstimatedTimeHours(estimatedTime.getSelection());
		}

		if (dueDatePicker != null && dueDatePicker.getDate() != null) {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, dueDatePicker.getDate().getTime());
		} else {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, null);
		}

		super.commit(onSave);
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, false);
		Composite composite = toolkit.createComposite(section);
		int numColumns = (needsDueDate) ? 6 : 4;
		composite.setLayout(new GridLayout(numColumns, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		createScheduledDatePicker(toolkit, composite);

		// disable due date picker if it's a repository due date
		if (needsDueDate) {
			createDueDatePicker(toolkit, composite);
		}

		createEstimatedTime(toolkit, composite);

//		createActualTime(toolkit, composite);

		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
		TasksUiPlugin.getTaskActivityManager().addActivityListener(timingListener);

		this.notesString = task.getNotes();
		if (this.notesString == null) {
			this.notesString = ""; //$NON-NLS-1$
		}

		createNotesArea(toolkit, composite, numColumns);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
		return section;
	}

	private void createNotesArea(FormToolkit toolkit, Composite parent, int numColumns) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridDataFactory.fillDefaults().span(numColumns, SWT.DEFAULT).grab(true, expandNotesVertically).applyTo(
				composite);

		Label labelControl = toolkit.createLabel(composite, Messages.PersonalPart_Notes);
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		if (editorSite != null) {
			IContextService contextService = (IContextService) editorSite.getService(IContextService.class);
			if (contextService != null) {
				AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(taskRepository);
				if (extension != null) {
					noteEditor = new RichTextEditor(taskRepository, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL,
							contextService, extension);
				}
			}
		}
		if (noteEditor == null) {
			noteEditor = new RichTextEditor(taskRepository, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		}
		noteEditor.setSpellCheckingEnabled(true);
		noteEditor.createControl(composite, toolkit);
		noteEditor.setText(notesString);

		final GridData gd = new GridData(GridData.FILL_BOTH);
		int widthHint = 0;

		if (getManagedForm() != null && getManagedForm().getForm() != null) {
			widthHint = getManagedForm().getForm().getClientArea().width - 90;
		}
		if (widthHint <= 0 && editorSite != null && editorSite.getPage() != null) {
			EditorAreaHelper editorManager = ((WorkbenchPage) editorSite.getPage()).getEditorPresentation();
			if (editorManager != null && editorManager.getLayoutPart() != null) {
				widthHint = editorManager.getLayoutPart().getControl().getBounds().width - 90;
			}
		}

		if (widthHint <= 0) {
			widthHint = 100;
		}

		gd.widthHint = widthHint;
		gd.minimumHeight = 100;
		gd.heightHint = 100;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = expandNotesVertically;
		gd.horizontalIndent = 10;

		noteEditor.getControl().setLayoutData(gd);
		noteEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		noteEditor.setReadOnly(false);
		textSupport.install(noteEditor.getViewer(), true);
		noteEditor.getDefaultViewer().addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				notesString = noteEditor.getText();
				if (!notesEqual()) {
					markDirty();
				}
			}
		});
		toolkit.paintBordersFor(composite);
	}

	private void createActualTime(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Active);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText(Messages.TaskEditorPlanningPart_Time_working_on_this_task);
		label.setBackground(null);

		Composite nameValueComp = createComposite(parent, 2, toolkit);
		nameValueComp.setBackground(null);

		elapsedTimeText = new Text(nameValueComp, SWT.FLAT | SWT.READ_ONLY);
		elapsedTimeText.setFont(EditorUtil.TEXT_FONT);
		elapsedTimeText.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		toolkit.adapt(elapsedTimeText, true, false);
		elapsedTimeText.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		elapsedTimeText.setBackground(null);
		updateElapsedTime();
		elapsedTimeText.setEditable(false);

		ImageHyperlink resetActivityTimeButton = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		resetActivityTimeButton.setBackground(null);
		resetActivityTimeButton.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		resetActivityTimeButton.setToolTipText(Messages.TaskEditorPlanningPart_Reset);
		resetActivityTimeButton.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (MessageDialog.openConfirm(getControl().getShell(),
						Messages.TaskEditorPlanningPart_Confirm_Activity_Time_Deletion,
						Messages.TaskEditorPlanningPart_Do_you_wish_to_reset_your_activity_time_on_this_task_)) {
					MonitorUi.getActivityContextManager().removeActivityTime(task.getHandleIdentifier(), 0l,
							System.currentTimeMillis());
				}
			}
		});
	}

	private void updateElapsedTime() {
		long elapsedTime = TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);
		if (elapsedTime > 0) {
			if (actualTimeComposite != null && !actualTimeComposite.isVisible()) {
				actualTimeComposite.setVisible(true);
			}
		} else {
			if (actualTimeComposite != null && actualTimeComposite.isVisible()) {
				actualTimeComposite.setVisible(false);
			}
		}
		String elapsedTimeString = DateUtil.getFormattedDurationShort(elapsedTime);
		if (elapsedTimeString.equals("")) { //$NON-NLS-1$
			elapsedTimeString = Messages.TaskEditorPlanningPart_0_SECOUNDS;
		}
		elapsedTimeText.setText(elapsedTimeString);

	}

	private Composite createComposite(Composite parent, int col, FormToolkit toolkit) {
		Composite nameValueComp = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		nameValueComp.setLayout(layout);
		return nameValueComp;
	}

	private void createDueDatePicker(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Due);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		dueDatePicker = new DatePicker(composite, SWT.FLAT, DatePicker.LABEL_CHOOSE, true, 0);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(dueDatePicker);
		dueDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		dueDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		if (task.getDueDate() != null) {
			Calendar calendar = TaskActivityUtil.getCalendar();
			calendar.setTime(task.getDueDate());
			dueDatePicker.setDate(calendar);
		}
		dueDatePicker.addPickerSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				markDirty();
			}
		});
		toolkit.adapt(dueDatePicker, false, false);
		toolkit.paintBordersFor(composite);

		ImageHyperlink clearDueDate = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearDueDate.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearDueDate.setToolTipText(Messages.TaskEditorPlanningPart_Clear);
		clearDueDate.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				dueDatePicker.setDate(null);
				markDirty();
			}
		});
	}

	private void createEstimatedTime(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Estimated);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		// Estimated time
		estimatedTime = new Spinner(composite, SWT.FLAT);
		estimatedTime.setDigits(0);
		estimatedTime.setMaximum(100);
		estimatedTime.setMinimum(0);
		estimatedTime.setIncrement(1);
		estimatedTime.setSelection(task.getEstimatedTimeHours());
		estimatedTime.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		estimatedTime.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (task.getEstimatedTimeHours() != estimatedTime.getSelection()) {
					markDirty();
				}
			}
		});

		ImageHyperlink clearEstimated = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearEstimated.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearEstimated.setToolTipText(Messages.TaskEditorPlanningPart_Clear);
		clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				estimatedTime.setSelection(0);
				markDirty();
			}
		});
		toolkit.paintBordersFor(composite);
	}

	private void createScheduledDatePicker(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Scheduled);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		scheduleDatePicker = new ScheduleDatePicker(composite, task, SWT.FLAT);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(scheduleDatePicker);
		scheduleDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(scheduleDatePicker, false, false);
		toolkit.paintBordersFor(composite);

		scheduleDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		scheduleDatePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}

			public void widgetSelected(SelectionEvent arg0) {
				markDirty();
			}
		});

		ImageHyperlink clearScheduledDate = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearScheduledDate.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearScheduledDate.setToolTipText(Messages.TaskEditorPlanningPart_Clear);
		clearScheduledDate.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				scheduleDatePicker.setScheduledDate(null);
				// XXX why is this set here?
				task.setReminded(false);
				markDirty();
			}
		});
	}

	@Override
	public void dispose() {

		if (textSupport != null) {
			textSupport.dispose();
		}

		TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

	private void updateFromTask(AbstractTask updateTask) {
		if (scheduleDatePicker != null && !scheduleDatePicker.isDisposed()) {
			if (updateTask.getScheduledForDate() != null) {
				scheduleDatePicker.setScheduledDate(updateTask.getScheduledForDate());
			} else {
				scheduleDatePicker.setScheduledDate(null);
			}
		}

		if (estimatedTime != null && !estimatedTime.isDisposed()) {
			estimatedTime.setSelection(updateTask.getEstimatedTimeHours());
		}
	}

	@Override
	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			actualTimeComposite = toolkit.createComposite(section);
			actualTimeComposite.setBackground(null);
			RowLayout rowLayout = new RowLayout();
			EditorUtil.center(rowLayout);
			rowLayout.marginTop = 0;
			rowLayout.marginBottom = 0;
			actualTimeComposite.setLayout(rowLayout);

			createActualTime(toolkit, actualTimeComposite);

			section.setTextClient(actualTimeComposite);
		}

		super.setSection(toolkit, section);
	}

	/** for testing - should cause dirty state */
	public void setNotes(String notes) {
		noteEditor.setText(notes);
	}

}
