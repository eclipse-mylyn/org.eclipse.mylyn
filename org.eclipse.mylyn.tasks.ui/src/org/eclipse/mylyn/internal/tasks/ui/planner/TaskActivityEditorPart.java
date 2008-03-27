/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
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
 * Note: Some methods have been generalized to remove duplicate code but the design still isn't right (long parameter
 * lists, inflexible table creation). Needs refactoring. (Planned tasks section is currently disabled but should also
 * use the new common methods)
 * 
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (added tasks in progress section, refactored-out similar code)
 * @author Mik Kersten (rewrite)
 */
public class TaskActivityEditorPart extends EditorPart {

	private static final String LABEL_PLANNED_ACTIVITY = "Planned Activity";

	private static final String LABEL_DIALOG = "Summary";

	private static final String LABEL_PAST_ACTIVITY = "Past Activity";

	private TaskActivityEditorInput editorInput = null;

	private final String[] activityColumnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated",
			"Created", "Completed" };

	private final int[] activityColumnWidths = new int[] { 100, 30, 200, 70, 70, 90, 90 };

	private final int[] activitySortConstants = new int[] { TaskActivitySorter.ICON, TaskActivitySorter.PRIORITY,
			TaskActivitySorter.DESCRIPTION, TaskActivitySorter.DURATION, TaskActivitySorter.ESTIMATED,
			TaskActivitySorter.CREATION_DATE, TaskActivitySorter.COMPLETED_DATE };

//	private String[] planColumnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated", "Reminder" };
//
//	private int[] planSortConstants = new int[] { TaskPlanSorter.ICON, TaskPlanSorter.PRIORITY,
//			TaskPlanSorter.DESCRIPTION, TaskPlanSorter.DURATION, TaskPlanSorter.ESTIMATED, TaskPlanSorter.REMINDER };
//
//	private int[] planColumnWidths = new int[] { 100, 30, 200, 90, 90, 100 };

	private static final String LABEL_ESTIMATED = "Total estimated time: ";

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

		createSummarySection(editorComposite, toolkit, editorInput.getReportStartDate(), editorInput.getReportEndDate());
		String label = LABEL_PAST_ACTIVITY;

		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
		allTasks.addAll(editorInput.getCompletedTasks());
		allTasks.addAll(editorInput.getInProgressTasks());

		SashForm sashForm = new SashForm(editorComposite, SWT.VERTICAL);

		sashForm.setLayout(new GridLayout());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		activityContentProvider = new TaskActivityContentProvider(editorInput);

		final TreeViewer activityViewer = createTableSection(sashForm, toolkit, label, activityColumnNames,
				activityColumnWidths, activitySortConstants);
		activityViewer.setContentProvider(activityContentProvider);
		activityViewer.setLabelProvider(new TaskPlannerLabelProvider(activityViewer, editorInput.getReportStartDate(),
				editorInput.getReportEndDate()));
		setSorters(activityColumnNames, activitySortConstants, activityViewer.getTree(), activityViewer, false);
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
		final TreeViewer planViewer = createTableSection(sashForm, toolkit, LABEL_PLANNED_ACTIVITY,
				activityColumnNames, activityColumnWidths, activitySortConstants);
		planViewer.setContentProvider(planContentProvider);
		planViewer.setLabelProvider(new TaskPlannerLabelProvider(planViewer, editorInput.getReportStartDate(),
				editorInput.getReportEndDate()));
		// planViewer.setLabelProvider(new TaskPlanLabelProvider());
		// createPlanCellEditorListener(planViewer.getTable(), planViewer);
		// planViewer.setCellModifier(new PlannedTasksCellModifier(planViewer));
		// initDrop(planViewer, planContentProvider);
		setSorters(activityColumnNames, activitySortConstants, planViewer.getTree(), planViewer, true);
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

	private void fillContextMenu(TreeViewer viewer, IMenuManager manager) {
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

	private void createSummarySection(Composite parent, FormToolkit toolkit, Date startDate, Date endDate) {
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
		SimpleDateFormat formater = new SimpleDateFormat(formatString, Locale.ENGLISH);

		if (startDate != null) {
			String dateLabel = "Date start: " + formater.format(startDate);
			toolkit.createLabel(summaryContainer, dateLabel, SWT.NULL);
		}

		if (endDate != null) {
			String dateLabel = "Date end: " + formater.format(endDate);
			toolkit.createLabel(summaryContainer, dateLabel, SWT.NULL);
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
		String totalEstimated = "Total estimated time: " + editorInput.getTotalTimeEstimated() + " hours" + spacer;
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
		String totalEstimated = "Total estimated time: " + editorInput.getTotalTimeEstimated() + " hours" + spacer;
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

	private TreeViewer createTableSection(Composite parent, FormToolkit toolkit, String title, String[] columnNames,
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

	private TreeViewer createTable(Composite parent, FormToolkit toolkit, String[] columnNames, int[] columnWidths,
			int[] sortConstants) {
		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		Tree tree = toolkit.createTree(parent, style);

		tree.setLayout(new GridLayout());
		GridData tableGridData = new GridData(GridData.FILL_BOTH);
		tableGridData.heightHint = 100;
		tree.setLayoutData(tableGridData);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setEnabled(true);

		for (int i = 0; i < columnNames.length; i++) {
			TreeColumn column = new TreeColumn(tree, SWT.LEFT, i);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}

		TreeViewer treeViewer = new TreeViewer(tree);
		treeViewer.setUseHashlookup(true);
		treeViewer.setColumnProperties(columnNames);

		final OpenTaskEditorAction openAction = new OpenTaskEditorAction(treeViewer);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openAction.run();
			}
		});

		return treeViewer;
	}

	private void setSorters(String[] columnNames, int[] sortConstants, Tree tree, TreeViewer treeViewer, boolean plan) {
		for (int i = 0; i < columnNames.length; i++) {
			TreeColumn column = tree.getColumn(i);
			addColumnSelectionListener(treeViewer, column, sortConstants[i], plan);
		}
	}

	private void addColumnSelectionListener(final TreeViewer tableViewer, TreeColumn column, final int sorterConstant,
			final boolean plan) {
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				if (plan) { // TODO: bad modularity
//					tableViewer.setSorter(new TaskPlanSorter(sorterConstant));
//				} else {
//					tableViewer.setSorter(new TaskActivitySorter(sorterConstant));
//				}
			}
		});
	}

	private void createButtons(Composite parent, FormToolkit toolkit, final TreeViewer viewer,
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

			if (filename == null || filename.equals("")) {
				return;
			}

			if (!filename.endsWith(".html")) {
				filename += ".html";
			}
			outputFile = new File(filename);
			// outputStream = new FileOutputStream(outputFile, true);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("<html><head></head><body>");

			exportSummarySection(writer);

			exportActivitySection(writer);

			exportPlanSection(writer);

			writer.write("</body></html>");
			writer.close();
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not write to file", e));
		}
	}

	private void exportPlanSection(BufferedWriter writer) throws IOException {

		writer.write("<H2>" + LABEL_PLANNED_ACTIVITY + "</H2>");

		writer.write("<table border=\"1\" width=\"100%\" id=\"plannedActivityTable\">");
		writer.write("<tr>");
		writer.write("<td width=\"59\"><b>Type</b></td><td width=\"55\"><b>Priority</b></td><td width=\"495\"><b>Description</b></td>");
		writer.write("<td><b>Elapsed</b></td><td><b>Estimated</b></td><td><b>Reminder</b></td>");
		writer.write("</tr>");

		for (Object element : planContentProvider.getElements(null)) {
			if (element instanceof AbstractTask) {
				AbstractTask currentTask = (AbstractTask) element;

				String formatString = "dd-MM-yyyy";
				SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

				String elapsedTimeString = DateUtil.getFormattedDuration(TasksUiPlugin.getTaskActivityManager()
						.getElapsedTime(currentTask), false);
				String estimatedTimeString = currentTask.getEstimatedTimeHours() + " hours";
				if (elapsedTimeString.equals("")) {
					elapsedTimeString = BLANK_CELL;
				}

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
		writer.write("<td width=\"59\"><b>Type</b></td><td width=\"55\"><b>Priority</b></td><td width=\"495\"><b>Description</b></td>");
		writer.write("<td><b>Created</b></td><td><b>Completed</b></td><td><b>Elapsed</b></td><td><b>Estimated</b></td>");
		writer.write("</tr>");

		for (Object element : activityContentProvider.getElements(null)) {
			if (element instanceof AbstractTask) {
				AbstractTask currentTask = (AbstractTask) element;

				String formatString = "dd-MM-yyyy";
				SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

				String elapsedTimeString = DateUtil.getFormattedDuration(TasksUiPlugin.getTaskActivityManager()
						.getElapsedTime(currentTask), false);
				String estimatedTimeString = currentTask.getEstimatedTimeHours() + " hours";
				if (elapsedTimeString.equals("")) {
					elapsedTimeString = NO_TIME_ELAPSED;
				}

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
		Date endDate = editorInput.getReportEndDate();
		writer.write("<H2>" + LABEL_DIALOG + "</H2>");

		String formatString = "yyyy-MM-dd, h:mm a";
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

		writer.write("<table border=\"0\" width=\"75%\" id=\"table1\">\n<tr>\n");
		writer.write("<td width=\"138\">Date start:</td> ");
		String dateLabel = "Not Available";
		if (startDate != null) {
			dateLabel = format.format(startDate);
		}
		writer.write("<td>" + dateLabel + "</td>");

		writer.write("<td width=\"138\">Date end:</td> ");
		String endLabel = "Not Available";
		if (endDate != null) {
			endLabel = format.format(endDate);
		}
		writer.write("<td>" + endLabel + "</td>");
		//writer.write("<td width=\"169\">&nbsp;</td></tr>");

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

		writer.write("<tr><td width=\"138\">Outstanding estimated hours:</td><td>"
				+ editorInput.getTotalTimeEstimated() + "</td>");
		writer.write("<td width=\"169\">Total time:</td><td width=\"376\">" + getTotalTime() + "</td>");
		writer.write("</tr>");

		writer.write("</table>");
	}
}
