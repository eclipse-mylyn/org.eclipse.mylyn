/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.commons.workbench.forms.DatePicker;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleDatePicker;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Shawn Minto
 * @author Sam Davis
 */
public class PlanningPart extends AbstractLocalEditorPart {

	private boolean needsDueDate;

	private String notesString;

	private RichTextEditor noteEditor;

	private static final int CONTROL_WIDTH = 120;

	private DatePicker dueDatePicker;

	private Text activeTimeText;

	private Spinner estimatedTimeSpinner;

	private ScheduleDatePicker scheduleDatePicker;

	private static final String PERSONAL_NOTES = Messages.PlanningPart_Personal_Notes;

	private final ITaskListChangeListener TASK_LIST_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getElement() instanceof ITask) {
					final AbstractTask updateTask = (AbstractTask) taskContainerDelta.getElement();
					if (updateTask != null && getTask() != null
							&& updateTask.getHandleIdentifier().equals(getTask().getHandleIdentifier())) {
						if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									refresh(false);
								}
							});
						}
					}
				}
			}
		}
	};

	private final ITaskActivationListener activationListener = new TaskActivationAdapter() {

		@Override
		public void taskActivated(ITask task) {
			if (task.equals(getTask())) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (activeTimeText != null && !activeTimeText.isDisposed()) {
							updateActiveTime();
						}
					}
				});
			}
		}

		@Override
		public void taskDeactivated(ITask task) {
			if (task.equals(getTask())) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (activeTimeText != null && !activeTimeText.isDisposed()) {
							updateActiveTime();
						}
					}
				});
			}
		}
	};

	private final ITaskActivityListener timingListener = new TaskActivityAdapter() {

		@Override
		public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
			if (task.equals(PlanningPart.this.getTask())) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (activeTimeText != null && !activeTimeText.isDisposed()) {
							updateActiveTime();
						}
					}
				});
			}
		}
	};

	private final IPropertyChangeListener ACTIVITY_PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED)) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (activeTimeText != null && !activeTimeText.isDisposed()) {
							updateActiveTime();
						}
					}
				});
			}
		}

	};

	private CommonTextSupport textSupport;

	private TaskFormPage page;

	private Composite activeTimeComposite;

	private ToolBarManager toolBarManager;

	private boolean needsNotes;

	private boolean alwaysExpand;

	private Composite sectionClient;

	private boolean activeTimeEnabled;

	private Label scheduledLabel;

	private Composite layoutControl;

	public PlanningPart(int sectionStyle) {
		super(sectionStyle, Messages.PersonalPart_Personal_Planning);
		this.activeTimeEnabled = true;
		this.needsNotes = true;
	}

	public void initialize(IManagedForm managedForm, TaskRepository taskRepository, AbstractTask task,
			boolean needsDueDate, TaskFormPage page, CommonTextSupport textSupport) {
		super.initialize(managedForm, taskRepository, task);
		this.needsDueDate = needsDueDate;
		this.page = page;
		this.textSupport = textSupport;
	}

	private boolean notesEqual() {
		if (getTask().getNotes() == null && notesString == null) {
			return true;
		}

		if (getTask().getNotes() != null && notesString != null) {
			return getTask().getNotes().equals(notesString) || notesString.equals(PERSONAL_NOTES);
		}
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		Assert.isNotNull(getTask());

		if (!notesEqual()) {
			getTask().setNotes(notesString);
			// XXX REFRESH THE TASLKIST
		}

		if (scheduleDatePicker != null && scheduleDatePicker.getScheduledDate() != null) {
			if (getTask().getScheduledForDate() == null
					|| (getTask().getScheduledForDate() != null && !scheduleDatePicker.getScheduledDate().equals(
							getTask().getScheduledForDate()))
					|| getTask().getScheduledForDate() instanceof DayDateRange) {
				TasksUiPlugin.getTaskActivityManager()
						.setScheduledFor(getTask(), scheduleDatePicker.getScheduledDate());
				getTask().setReminded(false);
			}
		} else {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor(getTask(), null);
			getTask().setReminded(false);
		}

		if (estimatedTimeSpinner != null) {
			getTask().setEstimatedTimeHours(estimatedTimeSpinner.getSelection());
		}

		if (dueDatePicker != null && dueDatePicker.getDate() != null) {
			TasksUiPlugin.getTaskActivityManager().setDueDate(getTask(), dueDatePicker.getDate().getTime());
		} else {
			TasksUiPlugin.getTaskActivityManager().setDueDate(getTask(), null);
		}

		super.commit(onSave);
	}

	@Override
	public Control createControl(Composite parent, final FormToolkit toolkit) {
		this.notesString = getTask().getNotes();
		if (this.notesString == null) {
			this.notesString = ""; //$NON-NLS-1$
		}
		boolean expand = isAlwaysExpand() || notesString.length() > 0;
		final Section section = createSection(parent, toolkit, expand);
		section.clientVerticalSpacing = 0;
		if (section.isExpanded()) {
			expandSection(toolkit, section);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (sectionClient == null) {
						expandSection(toolkit, section);
						if (page instanceof AbstractTaskEditorPage) {
							((AbstractTaskEditorPage) page).reflow();
						}
					}
				}
			});
		}

		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
		TasksUiPlugin.getTaskActivityManager().addActivityListener(timingListener);
		TasksUiPlugin.getTaskActivityManager().addActivationListener(activationListener);
		MonitorUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(ACTIVITY_PROPERTY_LISTENER);

		setSection(toolkit, section);
		return section;
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		sectionClient = toolkit.createComposite(section);
		GridLayoutFactory.fillDefaults().applyTo(sectionClient);

		// create nested composite with GridData to enable resizing behavior of maximize action
		layoutControl = toolkit.createComposite(sectionClient);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(layoutControl);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = (needsDueDate) ? 6 : 4;
		layoutControl.setLayout(layout);

		createScheduledDatePicker(toolkit, layoutControl);

		// disable due date picker if it's a repository due date
		if (needsDueDate) {
			createDueDatePicker(toolkit, layoutControl);
		}

		createEstimatedTime(toolkit, layoutControl);

		if (needsNotes()) {
			createNotesArea(toolkit, layoutControl, layout.numColumns);
		}

		createActiveTime(toolkit, layoutControl, layout.numColumns);

		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		CommonUiUtil.setMenu(sectionClient, section.getParent().getMenu());
	}

	private void createNotesArea(final FormToolkit toolkit, Composite parent, int numColumns) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 1;
		composite.setLayout(layout);
		GridDataFactory.fillDefaults().span(numColumns, SWT.DEFAULT).grab(true, true).applyTo(composite);

		if (page != null) {
			IContextService contextService = (IContextService) page.getEditorSite().getService(IContextService.class);
			if (contextService != null) {
				AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(getRepository());
				if (extension != null) {
					noteEditor = new RichTextEditor(getRepository(), SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL,
							contextService, extension, getTask());
				}
			}
		}
		if (noteEditor == null) {
			noteEditor = new RichTextEditor(getRepository(), SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL, null,
					null, getTask());
		}
		noteEditor.setSpellCheckingEnabled(true);
		noteEditor.createControl(composite, toolkit);

		noteEditor.setText(notesString);

		noteEditor.getControl().setLayoutData(
				EditorUtil.getTextControlLayoutData(page, noteEditor.getViewer().getControl(), true));
		noteEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		noteEditor.setReadOnly(false);
		if (textSupport != null) {
			textSupport.install(noteEditor.getViewer(), true);
		}
		noteEditor.getViewer().addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				notesString = PERSONAL_NOTES.equals(noteEditor.getText()) ? "" : noteEditor.getText(); //$NON-NLS-1$
				if (!notesEqual()) {
					markDirty();
				}
			}
		});
		addNotesLabelText(toolkit, composite);
		toolkit.paintBordersFor(composite);
	}

	private void addNotesLabelText(final FormToolkit toolkit, final Composite composite) {
		if (!noteEditor.getViewer().getTextWidget().isFocusControl()) {
			setNotesLabelText(composite);
		}
		noteEditor.getViewer().getTextWidget().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (PERSONAL_NOTES.equals(noteEditor.getText())) {
					noteEditor.setText(""); //$NON-NLS-1$

					if (noteEditor.getViewer() != null) {
						noteEditor.getViewer().getTextWidget().setForeground(toolkit.getColors().getForeground());
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				setNotesLabelText(composite);
			}
		});
	}

	private void setNotesLabelText(Composite composite) {
		if (notesString.length() == 0) {
			notesString = PERSONAL_NOTES;
			noteEditor.setText(notesString);
			if (noteEditor.getViewer() != null) {
				noteEditor.getViewer()
						.getTextWidget()
						.setForeground(composite.getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
			}
		}
	}

	private void createActiveTime(FormToolkit toolkit, Composite toolbarComposite, int numColumns) {
		activeTimeComposite = toolkit.createComposite(toolbarComposite);
		GridDataFactory.fillDefaults().span(numColumns, SWT.DEFAULT).grab(false, false).applyTo(activeTimeComposite);
		activeTimeComposite.setBackground(null);
		activeTimeComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.center = true;
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		activeTimeComposite.setLayout(rowLayout);

		String labelString;
		if (MonitorUiPlugin.getDefault().isTrackingOsTime()) {
			labelString = Messages.PlanningPart_Active_time_Label;
		} else {
			String productName = CommonUiUtil.getProductName(Messages.PlanningPart_Default_Product);
			labelString = NLS.bind(Messages.PlanningPart_Active_time_in_Product_Label, productName);
		}
		Label label = toolkit.createLabel(activeTimeComposite, labelString);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText(Messages.TaskEditorPlanningPart_Time_working_on_this_task);
		label.setBackground(null);

		activeTimeText = new Text(activeTimeComposite, SWT.FLAT | SWT.READ_ONLY);
		activeTimeText.setText("00:00"); //$NON-NLS-1$
		activeTimeText.setFont(EditorUtil.TEXT_FONT);
		activeTimeText.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		toolkit.adapt(activeTimeText, true, false);
		activeTimeText.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		activeTimeText.setBackground(null);
		activeTimeText.setEditable(false);

		ImageHyperlink resetActivityTimeButton = toolkit.createImageHyperlink(activeTimeComposite, SWT.NONE);
		resetActivityTimeButton.setBackground(null);
		resetActivityTimeButton.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR_DISABLED));
		resetActivityTimeButton.setHoverImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		resetActivityTimeButton.setToolTipText(Messages.PlanningPart_Reset_Active_Time);
		resetActivityTimeButton.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (MessageDialog.openConfirm(getControl().getShell(),
						Messages.TaskEditorPlanningPart_Confirm_Activity_Time_Deletion,
						Messages.TaskEditorPlanningPart_Do_you_wish_to_reset_your_activity_time_on_this_task_)) {
					MonitorUi.getActivityContextManager().removeActivityTime(getTask().getHandleIdentifier(), 0l,
							System.currentTimeMillis());
				}
			}
		});
		updateActiveTime();
	}

	private void updateActiveTime() {
		boolean show = TasksUiInternal.isActivityTrackingEnabled() && isActiveTimeEnabled();
		long elapsedTime = TasksUiInternal.getActiveTime(getTask());
		boolean visible = activeTimeComposite != null && activeTimeComposite.isVisible();
		if (show && (elapsedTime > 0 || getTask().isActive())) {
			if (activeTimeComposite != null && !activeTimeComposite.isVisible()) {
				activeTimeComposite.setVisible(true);
				((GridData) activeTimeComposite.getLayoutData()).exclude = false;
				activeTimeComposite.getParent().layout();
			}
			String elapsedTimeString = DateUtil.getFormattedDurationShort(elapsedTime);
			if (elapsedTimeString.equals("")) { //$NON-NLS-1$
				elapsedTimeString = Messages.TaskEditorPlanningPart_0_SECOUNDS;
			}
			activeTimeText.setText(elapsedTimeString);
		} else {
			if (activeTimeComposite != null) {
				((GridData) activeTimeComposite.getLayoutData()).exclude = true;
				activeTimeComposite.getParent().layout();
				activeTimeComposite.setVisible(false);
			}
		}
		if (!needsNotes() && visible != (activeTimeComposite != null && activeTimeComposite.isVisible())) {
			if (page instanceof AbstractTaskEditorPage) {
				((AbstractTaskEditorPage) page).reflow();
			}
		}
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

		Composite composite = createComposite(parent, 1, toolkit);

		dueDatePicker = new DatePicker(composite, SWT.FLAT, DatePicker.LABEL_CHOOSE, true, 0);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(dueDatePicker);
		dueDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		dueDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		if (getTask().getDueDate() != null) {
			Calendar calendar = TaskActivityUtil.getCalendar();
			calendar.setTime(getTask().getDueDate());
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
	}

	private void createEstimatedTime(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Estimated);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		// Estimated time
		estimatedTimeSpinner = new Spinner(composite, SWT.FLAT);
		estimatedTimeSpinner.setDigits(0);
		estimatedTimeSpinner.setMaximum(10000);
		estimatedTimeSpinner.setMinimum(0);
		estimatedTimeSpinner.setIncrement(1);
		estimatedTimeSpinner.setSelection(getTask().getEstimatedTimeHours());
		if (!PlatformUiUtil.spinnerHasNativeBorder()) {
			estimatedTimeSpinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		}
		estimatedTimeSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (getTask().getEstimatedTimeHours() != estimatedTimeSpinner.getSelection()) {
					markDirty();
				}
			}
		});

//		ImageHyperlink clearEstimated = toolkit.createImageHyperlink(composite, SWT.NONE);
//		clearEstimated.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR_DISABLED));
//		clearEstimated.setHoverImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
//		clearEstimated.setToolTipText(Messages.TaskEditorPlanningPart_Clear);
//		clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				estimatedTime.setSelection(0);
//				markDirty();
//			}
//		});
		toolkit.paintBordersFor(composite);
	}

	private void createScheduledDatePicker(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, Messages.TaskEditorPlanningPart_Scheduled);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		scheduleDatePicker = new ScheduleDatePicker(composite, getTask(), SWT.FLAT);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(scheduleDatePicker);
		scheduleDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
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
	}

	@Override
	public void dispose() {
		TasksUiPlugin.getTaskActivityManager().removeActivationListener(activationListener);
		TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
		MonitorUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(ACTIVITY_PROPERTY_LISTENER);

		if (toolBarManager != null) {
			toolBarManager.dispose();
		}
	}

	@Override
	protected void setSection(FormToolkit toolkit, Section section) {
		super.setSection(toolkit, section);
		if (section.getTextClient() == null) {
			Composite toolbarComposite = toolkit.createComposite(section);
			toolbarComposite.setBackground(null);
			RowLayout rowLayout = new RowLayout();
			rowLayout.marginLeft = 0;
			rowLayout.marginRight = 0;
			rowLayout.marginTop = 0;
			rowLayout.marginBottom = 0;
			rowLayout.center = true;
			toolbarComposite.setLayout(rowLayout);

			createScheduledLabel(toolbarComposite, section, toolkit);

			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			// TODO toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			if (toolBarManager.getSize() > 0) {
				toolBarManager.createControl(toolbarComposite);
				section.clientVerticalSpacing = 0;
				section.descriptionVerticalSpacing = 0;
			}

			section.setTextClient(toolbarComposite);
		}
	}

	private void createScheduledLabel(Composite composite, Section section, FormToolkit toolkit) {
		scheduledLabel = toolkit.createLabel(composite, ""); //$NON-NLS-1$
		scheduledLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		scheduledLabel.setBackground(null);
		updateScheduledLabel(section.isExpanded());
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent event) {
				updateScheduledLabel(event.getState());
			}
		});
	}

	private void updateScheduledLabel(boolean sectionIsExpanded) {
		if (scheduledLabel != null && !scheduledLabel.isDisposed()) {
			if (!sectionIsExpanded && !getTask().isCompleted()) {
				DateRange date = getTask().getScheduledForDate();
				if (date != null) {
					scheduledLabel.setText(getLabel(date));
					scheduledLabel.setToolTipText(NLS.bind(Messages.PlanningPart_Scheduled_for_X_Tooltip,
							date.toString()));
				} else {
					scheduledLabel.setText(""); //$NON-NLS-1$
					scheduledLabel.setToolTipText(null);
				}
				if (!scheduledLabel.isVisible()) {
					scheduledLabel.setVisible(true);
				}
				scheduledLabel.getParent().getParent().layout(true);
			} else {
				if (scheduledLabel.isVisible()) {
					scheduledLabel.setVisible(false);
					scheduledLabel.getParent().getParent().layout(true);
				}
			}
		}
	}

	/**
	 * Returns a short label that describes <code>dateRage</code>. Public for testing.
	 */
	public static String getLabel(DateRange dateRange) {
		if (dateRange instanceof WeekDateRange) {
			if (dateRange.isPast() || dateRange.isPresent()) {
				return Messages.PlanningPart_This_Week;
			} else if (TaskActivityUtil.getNextWeek().compareTo(dateRange) == 0) {
				return Messages.PlanningPart_Next_Week;
			}
		} else {
			if (dateRange.isPast() || dateRange.isPresent()) {
				return Messages.PlanningPart_Today;
			}
			if (TaskActivityUtil.getCurrentWeek().includes(dateRange)) {
				return Messages.PlanningPart_This_Week;
			}
			if (TaskActivityUtil.getNextWeek().includes(dateRange)) {
				return Messages.PlanningPart_Next_Week;
			}
		}
		return Messages.PlanningPart_Later;
	}

	protected void fillToolBar(ToolBarManager toolBarManager) {
	}

	public boolean needsNotes() {
		return needsNotes;
	}

	public void setNeedsNotes(boolean needsNotes) {
		this.needsNotes = needsNotes;
	}

	public boolean isAlwaysExpand() {
		return alwaysExpand;
	}

	public void setAlwaysExpand(boolean alwaysExpand) {
		this.alwaysExpand = alwaysExpand;
	}

	@Override
	protected void refresh(boolean discardChanges) {
		if (scheduleDatePicker != null && !scheduleDatePicker.isDisposed()) {
			if (getTask().getScheduledForDate() != null) {
				scheduleDatePicker.setScheduledDate(getTask().getScheduledForDate());
			} else {
				scheduleDatePicker.setScheduledDate(null);
			}
		}

		if (scheduledLabel != null && !scheduledLabel.isDisposed()) {
			updateScheduledLabel(getSection().isExpanded());
		}

		if (estimatedTimeSpinner != null && !estimatedTimeSpinner.isDisposed()) {
			estimatedTimeSpinner.setSelection(getTask().getEstimatedTimeHours());
		}

		// TODO refresh notes
	}

	public boolean isActiveTimeEnabled() {
		return activeTimeEnabled;
	}

	public void setActiveTimeEnabled(boolean activeTimeEnabled) {
		this.activeTimeEnabled = activeTimeEnabled;
		if (activeTimeComposite != null && !activeTimeComposite.isDisposed()) {
			updateActiveTime();
		}
	}

	public RichTextEditor getNoteEditor() {
		return noteEditor;
	}

	public Control getLayoutControl() {
		return layoutControl;
	}

}
