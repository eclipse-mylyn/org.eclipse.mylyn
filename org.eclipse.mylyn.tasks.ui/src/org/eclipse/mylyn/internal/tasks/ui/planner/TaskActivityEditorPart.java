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

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 * Note: Some methods have been generalized to remove duplicate code but the
 * design still isn't right (long parameter lists, inflexible table creation).
 * Needs refactoring. (Planned tasks section is currently disabled but should
 * also use the new common methods)
 * 
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (added tasks in progress section, refactored-out
 *         similar code)
 * @author Mik Kersten (rewrite)
 */
public class TaskActivityEditorPart extends EditorPart {

	private static final String LABEL_PLANNED_ACTIVITY = "Planned Activity";

	private static final String LABEL_DIALOG = "Summary";

	private static final String LABEL_PAST_ACTIVITY = "Past Activity";

	private TaskActivityEditorInput editorInput = null;

	private String[] activityColumnNames = new String[] { " ", " !", "Description", "Created", "Completed", "Elapsed",
			"Estimated" };

	private int[] activityColumnWidths = new int[] { 20, 30, 300, 90, 90, 70, 70 };

	private int[] activitySortConstants = new int[] { TaskActivitySorter.ICON, TaskActivitySorter.PRIORITY,
			TaskActivitySorter.DESCRIPTION, TaskActivitySorter.CREATION_DATE, TaskActivitySorter.COMPLETED_DATE,
			TaskActivitySorter.DURATION, TaskActivitySorter.ESTIMATED };

	private String[] planColumnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated", "Reminder" };

	private int[] planSortConstants = new int[] { TaskPlanSorter.ICON, TaskPlanSorter.PRIORITY,
			TaskPlanSorter.DESCRIPTION, TaskPlanSorter.DURATION, TaskPlanSorter.ESTIMATED, TaskPlanSorter.REMINDER };

	private int[] planColumnWidths = new int[] { 20, 30, 340, 90, 90, 100 };

	private static final String LABEL_ESTIMATED = "Total estimatedTime: ";

	private static final String NO_TIME_ELAPSED = "&nbsp;";

	private static final String BLANK_CELL = "&nbsp;";

	private Label totalEstimatedHoursLabel;

	private Label numberCompleted;

	private Label totalTimeOnCompleted;

	private Label numberInProgress;

	private Label totalTimeOnIncomplete;

	private Label totalEstimatedTime;

	private Label totalTime;

	private TaskActivityContentProvider activityContentProvider;

	private PlannedTasksContentProvider planContentProvider;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		editorInput = (TaskActivityEditorInput) input;
		setPartName(editorInput.getName());
		setTitleToolTip(editorInput.getToolTipText());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sform = toolkit.createScrolledForm(parent);
		Composite editorComposite = sform.getBody();

		editorComposite.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL_BOTH;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		editorComposite.setLayoutData(gridData);

		createSummarySection(editorComposite, toolkit, editorInput.getReportStartDate());
		String label = LABEL_PAST_ACTIVITY;

		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
		allTasks.addAll(editorInput.getCompletedTasks());
		allTasks.addAll(editorInput.getInProgressTasks());

		SashForm sashForm = new SashForm(editorComposite, SWT.VERTICAL);

		sashForm.setLayout(new GridLayout());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		activityContentProvider = new TaskActivityContentProvider(editorInput);

		final TableViewer activityViewer = createTableSection(sashForm, toolkit, label, activityColumnNames,
				activityColumnWidths, activitySortConstants);
		activityViewer.setContentProvider(activityContentProvider);
		activityViewer.setLabelProvider(new TaskPlannerLabelProvider());
		setSorters(activityColumnNames, activitySortConstants, activityViewer.getTable(), activityViewer, false);
		activityViewer.setInput(editorInput);

		activityViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateLabels();
			}
		});

		MenuManager activityContextMenuMgr = new MenuManager("#ActivityPlannerPopupMenu");
		activityContextMenuMgr.setRemoveAllWhenShown(true);
		activityContextMenuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskActivityEditorPart.this.fillContextMenu(activityViewer, manager);

			}
		});
		Menu menu = activityContextMenuMgr.createContextMenu(activityViewer.getControl());
		activityViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(activityContextMenuMgr, activityViewer);

		planContentProvider = new PlannedTasksContentProvider(editorInput);
		final TableViewer planViewer = createTableSection(sashForm, toolkit, LABEL_PLANNED_ACTIVITY, planColumnNames,
				planColumnWidths, planSortConstants);
		planViewer.setContentProvider(planContentProvider);
		planViewer.setLabelProvider(new TaskPlanLabelProvider());
		// createPlanCellEditorListener(planViewer.getTable(), planViewer);
		// planViewer.setCellModifier(new PlannedTasksCellModifier(planViewer));
		// initDrop(planViewer, planContentProvider);
		setSorters(planColumnNames, planSortConstants, planViewer.getTable(), planViewer, true);
		planViewer.setInput(editorInput);

		// planViewer.addSelectionChangedListener(new
		// ISelectionChangedListener() {
		// public void selectionChanged(SelectionChangedEvent event) {
		// updateLabels();
		// }
		// });

		// MenuManager planContextMenuMgr = new
		// MenuManager("#PlanPlannerPopupMenu");
		// planContextMenuMgr.setRemoveAllWhenShown(true);
		// planContextMenuMgr.addMenuListener(new IMenuListener() {
		// public void menuAboutToShow(IMenuManager manager) {
		// TaskActivityEditorPart.this.fillContextMenu(planViewer, manager);
		// }
		// });
		// Menu planMenu =
		// planContextMenuMgr.createContextMenu(planViewer.getControl());
		// planViewer.getControl().setMenu(planMenu);
		// getSite().registerContextMenu(planContextMenuMgr, planViewer);

		totalEstimatedHoursLabel = toolkit.createLabel(editorComposite, LABEL_ESTIMATED + "0 hours  ", SWT.NULL);
		createButtons(editorComposite, toolkit, planViewer, planContentProvider);
		updateLabels();
	}

	private void fillContextMenu(TableViewer viewer, IMenuManager manager) {
		if (!viewer.getSelection().isEmpty()) {
			manager.add(new OpenTaskEditorAction(viewer));
			manager.add(new RemoveTaskAction(viewer));
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		} else {
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}

	@Override
	public void setFocus() {
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit, Date startDate) {
		Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		summarySection.setText(LABEL_DIALOG);
		summarySection.setLayout(new GridLayout());
		summarySection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite summaryContainer = toolkit.createComposite(summarySection);
		summarySection.setClient(summaryContainer);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		summaryContainer.setLayout(layout);

		String formatString = "yyyy-MM-dd, h:mm a";
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

		if (startDate != null) {
			String dateLabel = "Activity since " + format.format(startDate);
			// DateFormat.getDateInstance(DateFormat.MEDIUM).format(reportStartDate)
			toolkit.createLabel(summaryContainer, dateLabel, SWT.NULL);
//			startLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
			toolkit.createLabel(summaryContainer, "", SWT.NULL);
		}

		String numComplete = "Number completed: " + editorInput.getCompletedTasks().size();
		numberCompleted = toolkit.createLabel(summaryContainer, numComplete, SWT.NULL);
//		numberCompleted.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String totalCompletedTaskTime = "Total time on completed: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks(), false);
		totalTimeOnCompleted = toolkit.createLabel(summaryContainer, totalCompletedTaskTime, SWT.NULL);
//		totalTimeOnCompleted.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String numInProgress = "Number in progress: " + editorInput.getInProgressTasks().size();
		numberInProgress = toolkit.createLabel(summaryContainer, numInProgress, SWT.NULL);
//		numberInProgress.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String totalInProgressTaskTime = "Total time on incomplete: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnInProgressTasks(), false);
		totalTimeOnIncomplete = toolkit.createLabel(summaryContainer, totalInProgressTaskTime, SWT.NULL);
//		totalTimeOnIncomplete.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String spacer = "        ";
		String totalEstimated = "Total estimatedTime time: " + editorInput.getTotalTimeEstimated() + " hours" + spacer;
		totalEstimatedTime = toolkit.createLabel(summaryContainer, totalEstimated, SWT.NULL);
//		totalEstimatedTime.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String grandTotalTime = "Total time: " + getTotalTime();
		totalTime = toolkit.createLabel(summaryContainer, grandTotalTime, SWT.NULL);
//		totalTime.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

	}

	private void updateSummarySection() {
		String numComplete = "Number completed: " + editorInput.getCompletedTasks().size();
		numberCompleted.setText(numComplete);

		String totalCompletedTaskTime = "Total time on completed: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks(), false);
		totalTimeOnCompleted.setText(totalCompletedTaskTime);

		String numInProgress = "Number in progress: " + editorInput.getInProgressTasks().size();
		numberInProgress.setText(numInProgress);

		String totalInProgressTaskTime = "Total time on incomplete: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnInProgressTasks(), false);
		totalTimeOnIncomplete.setText(totalInProgressTaskTime);

		String spacer = "        ";
		String totalEstimated = "Total estimatedTime time: " + editorInput.getTotalTimeEstimated() + " hours" + spacer;
		totalEstimatedTime.setText(totalEstimated);

		String grandTotalTime = "Total time: " + getTotalTime();
		totalTime.setText(grandTotalTime);

	}

	// private void createPlanCellEditorListener(final Table planTable, final
	// TableViewer planTableViewer) {
	// CellEditor[] editors = new CellEditor[planColumnNames.length + 1];
	// final ComboBoxCellEditor estimateEditor = new
	// ComboBoxCellEditor(planTable, TaskListManager.ESTIMATE_TIMES,
	// SWT.READ_ONLY);
	// final ReminderCellEditor reminderEditor = new
	// ReminderCellEditor(planTable);
	// editors[0] = null; // not used
	// editors[1] = null;// not used
	// editors[2] = null;// not used
	// editors[3] = null;// not used
	// editors[4] = estimateEditor;
	// editors[5] = reminderEditor;
	// reminderEditor.addListener(new ICellEditorListener() {
	// public void applyEditorValue() {
	// Object selection = ((IStructuredSelection)
	// planTableViewer.getSelection()).getFirstElement();
	// if (selection instanceof ITask) {
	// ((ITask) selection).setReminderDate(reminderEditor.getReminderDate());
	// planTableViewer.refresh();
	// }
	// }
	//
	// public void cancelEditor() {
	// }
	//
	// public void editorValueChanged(boolean oldValidState, boolean
	// newValidState) {
	// }
	//
	// });
	// estimateEditor.addListener(new ICellEditorListener() {
	// public void applyEditorValue() {
	// Object selection = ((IStructuredSelection)
	// planTableViewer.getSelection()).getFirstElement();
	// if (selection instanceof ITask) {
	// ITask task = (ITask) selection;
	// int estimate = (Integer) estimateEditor.getValue();
	// if (estimate == -1) {
	// estimate = 0;
	// }
	// task.setEstimatedTimeHours(estimate);
	// updateLabels();
	// planTableViewer.refresh();
	// }
	// }
	//
	// public void cancelEditor() {
	// }
	//
	// public void editorValueChanged(boolean oldValidState, boolean
	// newValidState) {
	// }
	//
	// });
	// planTableViewer.setCellEditors(editors);
	// }

	private String getTotalTime() {
		return DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks()
				+ editorInput.getTotalTimeSpentOnInProgressTasks(), false);
	}

	private TableViewer createTableSection(Composite parent, FormToolkit toolkit, String title, String[] columnNames,
			int[] columnWidths, int[] sortConstants) {
		Section tableSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR); // |
																								// ExpandableComposite.TWISTIE
		tableSection.setText(title);
		// tableSection.setExpanded(true);
		tableSection.marginHeight = 8;
		tableSection.setLayout(new GridLayout());
		tableSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite detailContainer = toolkit.createComposite(tableSection);
		tableSection.setClient(detailContainer);
		detailContainer.setLayout(new GridLayout());
		detailContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

		return createTable(detailContainer, toolkit, columnNames, columnWidths, sortConstants);
	}

	private TableViewer createTable(Composite parent, FormToolkit toolkit, String[] columnNames, int[] columnWidths,
			int[] sortConstants) {
		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		Table table = toolkit.createTable(parent, style);

		table.setLayout(new GridLayout());
		GridData tableGridData = new GridData(GridData.FILL_BOTH);
		tableGridData.heightHint = 100;
		table.setLayoutData(tableGridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(true);

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT, i);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}

		TableViewer tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);

		final OpenTaskEditorAction openAction = new OpenTaskEditorAction(tableViewer);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openAction.run();
			}
		});

		return tableViewer;
	}

	private void setSorters(String[] columnNames, int[] sortConstants, Table table, TableViewer tableViewer,
			boolean plan) {
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = table.getColumn(i);
			addColumnSelectionListener(tableViewer, column, sortConstants[i], plan);
		}
	}

	private void addColumnSelectionListener(final TableViewer tableViewer, TableColumn column,
			final int sorterConstant, final boolean plan) {
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (plan) { // TODO: bad modularity
					tableViewer.setSorter(new TaskPlanSorter(sorterConstant));
				} else {
					tableViewer.setSorter(new TaskActivitySorter(sorterConstant));
				}
			}
		});
	}

	private void createButtons(Composite parent, FormToolkit toolkit, final TableViewer viewer,
			final PlannedTasksContentProvider contentProvider) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(parent.getBackground());
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;

		// Button addIncomplete = toolkit.createButton(container, "Add
		// Incomplete", SWT.PUSH | SWT.CENTER);
		// addIncomplete.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// Set<ITask> incompleteTasks = editorInput.getInProgressTasks();
		// for (ITask task : incompleteTasks) {
		// contentProvider.addTask(task);
		// viewer.refresh();
		// updateLabels();
		// }
		// }
		// });
		//
		// Button addToCategory = toolkit.createButton(container, "Add Planned
		// to Category...", SWT.PUSH | SWT.CENTER);
		// addToCategory.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// addPlannedTasksToCategory(contentProvider);
		// }
		// });

		Button exportToHTML = toolkit.createButton(container, "Export to HTML...", SWT.PUSH | SWT.CENTER);
		exportToHTML.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportToHtml();
			}
		});
	}

	// private void initDrop(final TableViewer tableViewer, final
	// PlannedTasksContentProvider contentProvider) {
	// Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	//
	// tableViewer.addDropSupport(DND.DROP_MOVE, types, new
	// ViewerDropAdapter(tableViewer) {
	// {
	// setFeedbackEnabled(false);
	// }
	//
	// @Override
	// public boolean performDrop(Object data) {
	//
	// IStructuredSelection selection = ((IStructuredSelection)
	// TaskListView.getDefault().getViewer()
	// .getSelection());
	//
	// for (Iterator iter = selection.iterator(); iter.hasNext();) {
	// Object selectedObject = iter.next();
	// if (selectedObject instanceof ITask) {
	// contentProvider.addTask((ITask) selectedObject);
	// updateLabels();
	// continue;
	// } else if (selectedObject instanceof ITaskListElement) {
	// // if
	// (MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement)
	// selectedObject) != null) {
	// ITask task = null;
	// if (selectedObject instanceof ITask) {
	// task = (ITask) selectedObject;
	// } else if (selectedObject instanceof AbstractQueryHit) {
	// task = ((AbstractQueryHit)
	// selectedObject).getOrCreateCorrespondingTask();
	// }
	// if (task != null) {
	// contentProvider.addTask(task);
	// updateLabels();
	// continue;
	// }
	// // }
	// } else {
	// return false;
	// }
	// }
	// tableViewer.refresh();
	// return true;
	// }
	//
	// @Override
	// public boolean validateDrop(Object targetObject, int operation,
	// TransferData transferType) {
	// Object selectedObject = ((IStructuredSelection)
	// TaskListView.getDefault().getViewer().getSelection())
	// .getFirstElement();
	// if (!(selectedObject instanceof AbstractRepositoryQuery)) {
	// // && ((ITaskListElement) selectedObject).isDragAndDropEnabled()) {
	// return true;
	// }
	// return false;
	// }
	// });
	// }

	// private class PlannedTasksCellModifier implements ICellModifier {
	//
	// private TableViewer tableViewer;
	//
	// public PlannedTasksCellModifier(TableViewer tableViewer) {
	// this.tableViewer = tableViewer;
	// }
	//
	// public boolean canModify(Object element, String property) {
	// int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
	// if (columnIndex == 5 || columnIndex == 4) {
	// return true;
	// }
	// return false;
	// }
	//
	// public Object getValue(Object element, String property) {
	// if (element instanceof ITask) {
	// int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
	// if (element instanceof ITask) {
	// if (columnIndex == 5) {
	// if (((ITask) element).getReminderDate() != null) {
	// return DateFormat.getDateInstance(DateFormat.MEDIUM).format(
	// ((ITask) element).getReminderDate());
	// } else {
	// return null;
	// }
	// } else if (columnIndex == 4) {
	// return new Integer(Arrays.asList(TaskListManager.ESTIMATE_TIMES).indexOf(
	// ((ITask) element).getEstimateTimeHours()));
	// }
	//
	// }
	// }
	// return null;
	// }
	//
	// public void modify(Object element, String property, Object value) {
	// int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
	// if (element instanceof ITask) {
	// ITask task = (ITask) element;
	// if (columnIndex == 4) {
	// if (value instanceof Integer) {
	// task.setEstimatedTimeHours(((Integer) value).intValue() * 10);
	// tableViewer.refresh();
	// }
	// }
	// }
	// }
	// }

	// private void addPlannedTasksToCategory(PlannedTasksContentProvider
	// contentProvider) {
	// List<AbstractTaskContainer> categories =
	// MylarTaskListPlugin.getTaskListManager().getTaskList().getUserCategories();
	// String[] categoryNames = new String[categories.size()];
	// int i = 0;
	// for (AbstractTaskContainer category : categories) {
	// categoryNames[i++] = category.getDescription();
	// }
	// if (categories.size() > 0) {
	// ComboSelectionDialog dialog = new
	// ComboSelectionDialog(Display.getCurrent().getActiveShell(), LABEL_DIALOG,
	// "Select destination category: ", categoryNames, 0);
	// int confirm = dialog.open();
	// if (confirm == ComboSelectionDialog.OK) {
	// String selected = dialog.getSelectedString();
	// AbstractTaskContainer destinationCategory = null;
	// for (AbstractTaskContainer category : categories) {
	// if (category.getDescription().equals(selected)) {
	// destinationCategory = category;
	// break; // will go to the first one
	// }
	// }
	// if (destinationCategory != null && destinationCategory instanceof
	// TaskCategory) {
	// TaskCategory taskCategory = (TaskCategory) destinationCategory;
	// for (ITask task : editorInput.getPlannedTasks()) {
	// if (!taskCategory.getChildren().contains(task)) {
	// MylarTaskListPlugin.getTaskListManager().getTaskList().moveToContainer(taskCategory,
	// task);
	// }
	// }
	// if (TaskListView.getDefault() != null) {
	// TaskListView.getDefault().refreshAndFocus();
	// }
	// } else {
	// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
	// LABEL_DIALOG,
	// "Can not add plan tasks into a query category.");
	// }
	// }
	// } else {
	// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
	// LABEL_DIALOG,
	// "No categories in task list.");
	// }
	// }

	private void updateLabels() {
		totalEstimatedHoursLabel.setText(LABEL_ESTIMATED + editorInput.getPlannedEstimate() + " hours");
		updateSummarySection();
	}

	private void exportToHtml() {
		File outputFile;
		try {
			FileDialog dialog = new FileDialog(getSite().getWorkbenchWindow().getShell());
			dialog.setText("Specify a file name");
			dialog.setFilterExtensions(new String[] { "*.html", "*.*" });
			String filename = dialog.open();

			if (filename == null || filename.equals(""))
				return;

			if (!filename.endsWith(".html"))
				filename += ".html";
			outputFile = new File(filename);
			// outputStream = new FileOutputStream(outputFile, true);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("<html><head></head><body>"
			// + "<link rel=\"stylesheet\"
					// href=\"http://eclipse.org/mylar/style.css\"
					// type=\"text/css\"></head><body>"
					);

			exportSummarySection(writer);

			exportActivitySection(writer);

			exportPlanSection(writer);

			writer.write("</body></html>");
			writer.close();
		} catch (FileNotFoundException e) {
			StatusHandler.log(e, "could not resolve file");
		} catch (IOException e) {
			StatusHandler.log(e, "could not write to file");
		}
	}

	private void exportPlanSection(BufferedWriter writer) throws IOException {

		writer.write("<H2>" + LABEL_PLANNED_ACTIVITY + "</H2>");

		writer.write("<table border=\"1\" width=\"100%\" id=\"plannedActivityTable\">");
		writer.write("<tr>");
		writer
				.write("<td width=\"59\"><b>Type</b></td><td width=\"55\"><b>Priority</b></td><td width=\"495\"><b>Description</b></td>");
		writer.write("<td><b>Elapsed</b></td><td><b>Estimated</b></td><td><b>Reminder</b></td>");
		writer.write("</tr>");

		for (Object element : planContentProvider.getElements(null)) {
			if (element instanceof AbstractTask) {
				AbstractTask currentTask = (AbstractTask) element;

				String formatString = "dd-MM-yyyy";
				SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

				String elapsedTimeString = DateUtil.getFormattedDuration(TasksUiPlugin.getTaskListManager().getElapsedTime(currentTask), false);
				String estimatedTimeString = currentTask.getEstimateTimeHours() + " hours";
				if (elapsedTimeString.equals(""))
					elapsedTimeString = BLANK_CELL;

				Date reminderDate = currentTask.getScheduledForDate();
				String reminderDateString = BLANK_CELL;
				if (reminderDate != null) {
					reminderDateString = format.format(reminderDate);
				}

				writer.write("<tr>");
				writer.write("<td width=\"59\">ICON</td><td width=\"55\">" + currentTask.getPriority()
						+ "</td><td width=\"495\">");
				if (currentTask.hasValidUrl()) {
					writer.write("<a href='" + currentTask.getUrl() + "'>" + currentTask.getSummary() + "</a>");
				} else {
					writer.write(currentTask.getSummary());
				}
				writer.write("</td><td>" + elapsedTimeString + "</td><td>" + estimatedTimeString + "</td><td>"
						+ reminderDateString + "</td>");
				writer.write("</tr>");

			}
		}
		writer.write("</table>");
		writer.write("<BR></BR>");
		writer.write("<H3>" + totalEstimatedHoursLabel.getText() + "</H3>");

	}

	private void exportActivitySection(BufferedWriter writer) throws IOException {

		writer.write("<H2>" + LABEL_PAST_ACTIVITY + "</H2>");

		writer.write("<table border=\"1\" width=\"100%\" id=\"activityTable\">");
		writer.write("<tr>");
		writer
				.write("<td width=\"59\"><b>Type</b></td><td width=\"55\"><b>Priority</b></td><td width=\"495\"><b>Description</b></td>");
		writer
				.write("<td><b>Created</b></td><td><b>Completed</b></td><td><b>Elapsed</b></td><td><b>Estimated</b></td>");
		writer.write("</tr>");

		for (Object element : activityContentProvider.getElements(null)) {
			if (element instanceof AbstractTask) {
				AbstractTask currentTask = (AbstractTask) element;

				String formatString = "dd-MM-yyyy";
				SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

				String elapsedTimeString = DateUtil.getFormattedDuration(TasksUiPlugin.getTaskListManager().getElapsedTime(currentTask), false);
				String estimatedTimeString = currentTask.getEstimateTimeHours() + " hours";
				if (elapsedTimeString.equals(""))
					elapsedTimeString = NO_TIME_ELAPSED;

				Date creationDate = currentTask.getCreationDate();
				String creationDateString = BLANK_CELL;
				if (creationDate != null) {
					creationDateString = format.format(creationDate);
				}

				String completionDateString = BLANK_CELL;
				Date completedDate = currentTask.getCompletionDate();
				if (completedDate != null) {
					completionDateString = format.format(completedDate);
				}

				writer.write("<tr>");
				writer.write("<td width=\"59\">ICON</td><td width=\"55\">" + currentTask.getPriority()
						+ "</td><td width=\"495\">");

				if (currentTask.hasValidUrl()) {
					writer.write("<a href='" + currentTask.getUrl() + "'>" + currentTask.getSummary() + "</a>");
				} else {
					writer.write(currentTask.getSummary());
				}

				writer.write("</td><td>" + creationDateString + "</td>");
				writer.write("<td>" + completionDateString + "</td><td>" + elapsedTimeString + "</td><td>"
						+ estimatedTimeString + "</td>");
				writer.write("</tr>");
			}
		}
		writer.write("</table>");
	}

	private void exportSummarySection(BufferedWriter writer) throws IOException {
		Date startDate = editorInput.getReportStartDate();
		writer.write("<H2>" + LABEL_DIALOG + "</H2>");

		String formatString = "yyyy-MM-dd, h:mm a";
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

		writer.write("<table border=\"0\" width=\"75%\" id=\"table1\">\n<tr>\n");
		writer.write("<td width=\"138\">Activity since:</td> ");
		String dateLabel = "Not Available";
		if (startDate != null) {
			dateLabel = format.format(startDate);
		}
		writer.write("<td>" + dateLabel + "</td>");
		writer.write("<td width=\"169\">&nbsp;</td><td width=\"376\">&nbsp;</td>\n</tr>");

		writer.write("<tr><td width=\"138\">Number Completed:</td><td>" + editorInput.getCompletedTasks().size()
				+ "</td>");

		writer.write("<td width=\"169\">Total time on completed:</td><td width=\"376\">"
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks(), false) + "</td>");
		writer.write("</tr>");

		writer.write("<tr><td width=\"138\">Number in Progress:</td><td>" + editorInput.getInProgressTasks().size()
				+ "</td>");
		writer.write("<td width=\"169\">Total time on incompleted:</td><td width=\"376\">"
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnInProgressTasks(), false) + "</td>");
		writer.write("</tr>");

		writer.write("<tr><td width=\"138\">Total estimatedTime time:</td><td>" + totalEstimatedHoursLabel.getText()
				+ "</td>");
		writer.write("<td width=\"169\">Total time:</td><td width=\"376\">" + getTotalTime() + "</td>");
		writer.write("</tr>");

		writer.write("</table>");
	}
}
