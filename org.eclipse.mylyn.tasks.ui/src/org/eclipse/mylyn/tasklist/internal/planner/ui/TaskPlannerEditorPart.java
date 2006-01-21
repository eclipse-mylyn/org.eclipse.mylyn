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

package org.eclipse.mylar.tasklist.internal.planner.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.core.internal.dt.MylarWebRef;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.ComboSelectionDialog;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.FormColors;
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
public class TaskPlannerEditorPart extends EditorPart {

	private static final String LABEL_DIALOG = "Summary";

	private static final String LABEL_TASK_ACTIVITY = "Task Activity";

	private TaskPlannerEditorInput editorInput = null;

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

	private static final String[] ESTIMATE_TIMES = new String[] { "0 Hours", "1 Hours", "2 Hours", "3 Hours",
			"4 Hours", "5 Hours", "6 Hours", "7 Hours", "8 Hours", "9 Hours", "10 Hours" };

	private static final String LABEL_ESTIMATED = "Total estimated: ";

	private Label totalEstimatedHoursLabel;

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
		editorInput = (TaskPlannerEditorInput) input;
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
		String label = LABEL_TASK_ACTIVITY;

		List<ITask> allTasks = new ArrayList<ITask>();
		allTasks.addAll(editorInput.getCompletedTasks());
		allTasks.addAll(editorInput.getInProgressTasks());

		SashForm sashForm = new SashForm(editorComposite, SWT.VERTICAL);

		sashForm.setLayout(new GridLayout());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			
		TaskPlannerContentProvider activityContentProvider = new TaskPlannerContentProvider(allTasks);
		final TableViewer activityViewer = createTableSection(sashForm, toolkit, label, activityColumnNames,
				activityColumnWidths, activitySortConstants);		
		activityViewer.setContentProvider(activityContentProvider);
		activityViewer.setLabelProvider(new TaskActivityLabelProvider());
		setSorters(activityColumnNames, activitySortConstants, activityViewer.getTable(), activityViewer, false);
		activityViewer.setInput(editorInput);
		
		
		
		MenuManager activityContextMenuMgr = new MenuManager("#ActivityPlannerPopupMenu");
		activityContextMenuMgr.setRemoveAllWhenShown(true);
		activityContextMenuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskPlannerEditorPart.this.fillContextMenu(activityViewer, manager);
				
			}
		});
		Menu menu = activityContextMenuMgr.createContextMenu(activityViewer.getControl());
		activityViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(activityContextMenuMgr, activityViewer);
		

		Composite planContainer = toolkit.createComposite(sashForm);
		GridLayout planLayout = new GridLayout();
		planLayout.marginTop = 10;
		planContainer.setLayout(planLayout);

	
		
		TaskPlannerContentProvider planContentProvider = new TaskPlannerContentProvider();
		final TableViewer planViewer = createTableSection(planContainer, toolkit, "Task Plan", planColumnNames,
				planColumnWidths, planSortConstants);
		planViewer.setContentProvider(planContentProvider);
		planViewer.setLabelProvider(new TaskPlanLabelProvider());
		createPlanCellEditorListener(planViewer.getTable(), planViewer, planContentProvider);
		planViewer.setCellModifier(new PlannedTasksCellModifier(planViewer));
		initDrop(planViewer, planContentProvider);
		setSorters(planColumnNames, planSortConstants, planViewer.getTable(), planViewer, true);
		planViewer.setInput(editorInput);
		
		MenuManager  planContextMenuMgr = new MenuManager("#PlanPlannerPopupMenu");
		planContextMenuMgr.setRemoveAllWhenShown(true);
		planContextMenuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskPlannerEditorPart.this.fillContextMenu(planViewer, manager);
			}
		});
		Menu planMenu = planContextMenuMgr.createContextMenu(planViewer.getControl());
		planViewer.getControl().setMenu(planMenu);
		getSite().registerContextMenu(planContextMenuMgr, planViewer);

		totalEstimatedHoursLabel = toolkit.createLabel(editorComposite, LABEL_ESTIMATED + "0 hours  ", SWT.NULL);
		createButtons(editorComposite, toolkit, planViewer, planContentProvider);
	}

	

	private void fillContextMenu(TableViewer viewer, IMenuManager manager) {
		if(!viewer.getSelection().isEmpty()) {			
			manager.add(new OpenTaskEditorAction(viewer));
			manager.add(new RemoveTaskAction(viewer));
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}
	
	
	@Override
	public void setFocus() {
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit, Date startDate) {
		Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		summarySection.setText(LABEL_DIALOG);
//		summarySection.setLayout(new TableWrapLayout());
//		summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		summarySection.setLayout(new GridLayout());
		summarySection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite summaryContainer = toolkit.createComposite(summarySection);
		summarySection.setClient(summaryContainer);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		summaryContainer.setLayout(layout);

		String fomratString = "yyyy-MM-dd, h:mm a";
		SimpleDateFormat format = new SimpleDateFormat(fomratString, Locale.ENGLISH);

		if (startDate != null) {
			String dateLabel = "Activity since " + format.format(startDate);
			// DateFormat.getDateInstance(DateFormat.MEDIUM).format(reportStartDate)
			Label startLabel = toolkit.createLabel(summaryContainer, dateLabel, SWT.NULL);
			startLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
			toolkit.createLabel(summaryContainer, "", SWT.NULL);
		}

		String numComplete = "Number completed: " + editorInput.getCompletedTasks().size();
		Label label = toolkit.createLabel(summaryContainer, numComplete, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String totalCompletedTaskTime = "Total time on completed: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks(), false);
		label = toolkit.createLabel(summaryContainer, totalCompletedTaskTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String numInProgress = "Number in progress: " + editorInput.getInProgressTasks().size();
		label = toolkit.createLabel(summaryContainer, numInProgress, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String totalInProgressTaskTime = "Total time on incomplete: "
				+ DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnInProgressTasks(), false);
		label = toolkit.createLabel(summaryContainer, totalInProgressTaskTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		// int length = editorInput.getCompletedTasks().size();
		// String avgTime = "Average completion time: ";
		// if (length > 0) {
		// avgTime = avgTime
		// +
		// DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks()
		// / editorInput.getCompletedTasks().size());
		// } else {
		// avgTime = avgTime + 0;
		// }
		// label = toolkit.createLabel(summaryContainer, avgTime, SWT.NULL);
		// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String spacer = "        ";
		String totalEstimated = "Total estimated time: " + editorInput.getTotalTimeEstimated() + " hours" + spacer;
		label = toolkit.createLabel(summaryContainer, totalEstimated, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		String grandTotalTime = "Total time: " + getTotalTime();
		label = toolkit.createLabel(summaryContainer, grandTotalTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

	}

	private void createPlanCellEditorListener(final Table planTable, final TableViewer planTableViewer,
			final TaskPlannerContentProvider contentProvider) {
		CellEditor[] editors = new CellEditor[planColumnNames.length + 1];
		final ComboBoxCellEditor estimateEditor = new ComboBoxCellEditor(planTable, ESTIMATE_TIMES, SWT.READ_ONLY);
		final ReminderCellEditor reminderEditor = new ReminderCellEditor(planTable);
		editors[0] = null; // not used
		editors[1] = null;// not used
		editors[2] = null;// not used
		editors[3] = null;// not used
		editors[4] = estimateEditor;
		editors[5] = reminderEditor;
		reminderEditor.addListener(new ICellEditorListener() {
			public void applyEditorValue() {
				Object selection = ((IStructuredSelection) planTableViewer.getSelection()).getFirstElement();
				if (selection instanceof ITask) {
					((ITask) selection).setReminderDate(reminderEditor.getReminderDate());
					planTableViewer.refresh();
				}
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}

		});
		estimateEditor.addListener(new ICellEditorListener() {
			public void applyEditorValue() {
				Object selection = ((IStructuredSelection) planTableViewer.getSelection()).getFirstElement();
				if (selection instanceof ITask) {
					ITask task = (ITask) selection;
					int estimate = (Integer) estimateEditor.getValue();
					if (estimate == -1) {
						estimate = 0;
					}
					task.setEstimatedTimeHours(estimate);
					updateEstimatedHours(contentProvider);
					planTableViewer.refresh();
				}
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}

		});
		planTableViewer.setCellEditors(editors);
	}

	private String getTotalTime() {
		return DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks()
				+ editorInput.getTotalTimeSpentOnInProgressTasks(), false);
	}

	private TableViewer createTableSection(Composite parent, FormToolkit toolkit, String title, String[] columnNames,
			int[] columnWidths, int[] sortConstants) {
		Section tableSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		tableSection.setText(title);
		tableSection.setExpanded(true);
		tableSection.setLayout(new GridLayout());
		tableSection.setLayoutData(new GridData(GridData.FILL_BOTH));

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
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(true);

		// TableColumn firstColumn = new TableColumn(table, SWT.LEFT, 0);
		// firstColumn.setText(" ");
		// firstColumn.setWidth(30);

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

	/**
	 * TODO: refactor into seperate actions?
	 */
	private void createButtons(Composite parent, FormToolkit toolkit, final TableViewer viewer,
			final TaskPlannerContentProvider contentProvider) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(parent.getBackground());
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;

		Button addIncomplete = toolkit.createButton(container, "Add Incomplete", SWT.PUSH | SWT.CENTER);
		addIncomplete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<ITask> incompleteTasks = editorInput.getInProgressTasks();
				for (ITask task : incompleteTasks) {
					contentProvider.addTask(task);
					viewer.refresh();
					updateEstimatedHours(contentProvider);
				}
			}
		});

		Button addToCategory = toolkit.createButton(container, "Add Planned to Category...", SWT.PUSH | SWT.CENTER);
		addToCategory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addPlannedTasksToCategory(contentProvider);
			}
		});

//		Button delete = toolkit.createButton(container, "Remove Selected", SWT.PUSH | SWT.CENTER);
//		delete.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				for (Object object : ((IStructuredSelection) viewer.getSelection()).toList()) {
//					if (object instanceof ITask) {
//						ITask task = (ITask) object;
//						if (task != null) {
//							contentProvider.removeTask(task);
//						}
//					}
//				}
//				viewer.refresh();
//			}
//		});
	}

	@MylarWebRef(name = "Drag and drop article", url = "http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
	private void initDrop(final TableViewer tableViewer, final TaskPlannerContentProvider contentProvider) {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		tableViewer.addDropSupport(DND.DROP_MOVE, types, new ViewerDropAdapter(tableViewer) {
			{
				setFeedbackEnabled(false);
			}

			@Override
			public boolean performDrop(Object data) {
				
				IStructuredSelection selection = ((IStructuredSelection) TaskListView
						.getDefault().getViewer().getSelection());

				for (Iterator iter = selection.iterator(); iter.hasNext();) {
					Object selectedObject = iter.next();
					if (selectedObject instanceof ITask) {
						contentProvider.addTask((ITask) selectedObject);
						updateEstimatedHours(contentProvider);
						continue;
					} else if (selectedObject instanceof ITaskListElement) {
						if (MylarTaskListPlugin.getDefault()
								.getHandlerForElement(
										(ITaskListElement) selectedObject) != null) {
							ITask task = null;
							if (selectedObject instanceof ITask) {
								task = (ITask) selectedObject;
							} else if (selectedObject instanceof IQueryHit) {
								task = ((IQueryHit) selectedObject)
										.getOrCreateCorrespondingTask();
							}
							if (task != null) {
								contentProvider.addTask(task);
								updateEstimatedHours(contentProvider);
								continue;
							}
						}
					} else {
						return false;
					}
				}
				tableViewer.refresh();
				return true;				
			}

			@Override
			public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
				Object selectedObject = ((IStructuredSelection) TaskListView.getDefault().getViewer().getSelection())
						.getFirstElement();
				if (selectedObject instanceof ITaskListElement
						&& ((ITaskListElement) selectedObject).isDragAndDropEnabled()) {
					return true;
				}
				return false;
			}
		});
	}

	private class PlannedTasksCellModifier implements ICellModifier {

		private TableViewer tableViewer;

		public PlannedTasksCellModifier(TableViewer tableViewer) {
			this.tableViewer = tableViewer;
		}

		public boolean canModify(Object element, String property) {
			int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
			if (columnIndex == 5 || columnIndex == 4) {
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (element instanceof ITask) {
				int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
				if (element instanceof ITask) {
					if (columnIndex == 5) {
						if (((ITask) element).getReminderDate() != null) {
							return DateFormat.getDateInstance(DateFormat.MEDIUM)
								.format(((ITask) element).getReminderDate());
						} else {
							return null;
						}
					} else if (columnIndex == 4) {
						return new Integer(Arrays.asList(ESTIMATE_TIMES).indexOf(
								((ITask) element).getEstimateTimeHours()));
					}

				}
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
			if (element instanceof ITask) {
				ITask task = (ITask) element;
				if (columnIndex == 4) {
					if (value instanceof Integer) {
						task.setEstimatedTimeHours(((Integer) value).intValue() * 10);
						tableViewer.refresh();
					}
				}
			}
		}
	}

	private void addPlannedTasksToCategory(TaskPlannerContentProvider contentProvider) {
		List<ITaskCategory> categories = MylarTaskListPlugin.getTaskListManager().getTaskList().getUserCategories();
		String[] categoryNames = new String[categories.size()];
		int i = 0;
		for (ITaskCategory category : categories) {
			categoryNames[i++] = category.getDescription();
		}
		if (categories.size() > 0) {
			ComboSelectionDialog dialog = new ComboSelectionDialog(Display.getCurrent().getActiveShell(), LABEL_DIALOG,
					"Select destination category: ", categoryNames, 0);
			int confirm = dialog.open();
			if (confirm == ComboSelectionDialog.OK) {
				String selected = dialog.getSelectedString();
				ITaskCategory destinationCategory = null;
				for (ITaskCategory category : categories) {
					if (category.getDescription().equals(selected)) {
						destinationCategory = category;
						break; // will go to the first one
					}
				}
				if (destinationCategory != null && destinationCategory instanceof TaskCategory) {
					TaskCategory taskCategory = (TaskCategory) destinationCategory;
					for (ITask task : contentProvider.getTasks()) {
						if (!taskCategory.getChildren().contains(task)) {
							MylarTaskListPlugin.getTaskListManager().moveToCategory(taskCategory, task);
						}
					}
					if (TaskListView.getDefault() != null)
						TaskListView.getDefault().getViewer().refresh();
				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), LABEL_DIALOG,
							"Can not add plan tasks into a query category.");
				}
			}
		} else {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), LABEL_DIALOG,
					"No categories in task list.");
		}
	}

	private void updateEstimatedHours(TaskPlannerContentProvider contentProvider) {
		int total = 0;
		for (ITask task : contentProvider.getTasks()) {
			total += task.getEstimateTimeHours();
		}
		totalEstimatedHoursLabel.setText(LABEL_ESTIMATED + total + " hours");
	}

//	public class OpenTaskEditorAction extends Action {
//
//		private TableViewer viewer;
//
//		public OpenTaskEditorAction(TableViewer viewer) {
//			this.viewer = viewer;
//		}
//
//		@Override
//		public void run() {
//			ISelection selection = viewer.getSelection();
//			Object obj = ((IStructuredSelection) selection).getFirstElement();
//			if (obj instanceof Task) {
//				((Task) obj).openTaskInEditor(false);
//			}
//			viewer.refresh(obj);
//		}
//	}
}
