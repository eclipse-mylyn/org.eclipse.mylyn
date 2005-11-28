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

package org.eclipse.mylar.tasklist.planner.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 * Note: Some methods have been generalized to remove duplicate code but the design
 * still isn't right (long parameter lists, inflexible table creation). Needs refactoring.
 * (Planned tasks section is currently disabled but should also use the new common methods)
 * 
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (added tasks in progress section, refactored-out similar code)
 */
public class MylarTaskPlannerEditorPart extends EditorPart {

	
	private TasksEditorInput editorInput = null;

	//Completed Table Information
	private String[] completedColumnNames = new String[] {"Description", "Priority", "Date Created", "Date Completed", "Duration"};
	private int[] completedColumnWidths = new int[] {300, 50, 90, 90, 70};
	private int[] completedSorterConstants = new int[] {TaskSorter.DESCRIPTION, TaskSorter.PRIORITY, TaskSorter.CREATION_DATE, TaskSorter.COMPLETED_DATE, TaskSorter.DURATION};
	
	//In Progress Table Information
	private String[] inProgressColumnNames = new String[] {"Description", "Priority", "Date Created", "Duration"};
	private int[] inProgressColumnWidths = new int[] {300, 50, 90, 70};
	private int[] inProgressSorterConstants = new int[] {TaskSorter.DESCRIPTION, TaskSorter.PRIORITY, TaskSorter.CREATION_DATE, TaskSorter.DURATION};
		
	private List<TableViewer> tableViewers = new ArrayList<TableViewer>();
	
	private Table planTable;
	private TableViewer planTableViewer;
	private PlannedTasksContentProvider contentProvider = new PlannedTasksContentProvider();
	private ReminderCellEditor reminderEditor = null;
	private String[] planColumnNames = new String[] {"Description", "Priority", "Estimated Time", "Reminder Date"};
	private static final String[] ESTIMATE_TIMES = new String[] {"0 Hours", "1 Hours", "2 Hours", "3 Hours","4 Hours","5 Hours","6 Hours","7 Hours","8 Hours","9 Hours","10 Hours"};
	private OpenTaskEditorAction doubleClickAction = new OpenTaskEditorAction();
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		editorInput = (TasksEditorInput)input;
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
		sform.getBody().setLayout(new TableWrapLayout());
		Composite editorComposite = sform.getBody();
		
		createSummarySection(editorComposite, toolkit);
		
		IStructuredContentProvider completedTasksProvider = new TasksContentProvider(editorInput.getCompletedTasks());
		createTableSection(editorComposite, toolkit, "Completed Tasks Details", completedColumnNames, completedColumnWidths, completedSorterConstants, completedTasksProvider, new CompletedTasksLabelProvider());
		
		IStructuredContentProvider inProgressTasksProvider = new TasksContentProvider(editorInput.getInProgressTasks());
		createTableSection(editorComposite, toolkit, "Tasks in Progress Details", inProgressColumnNames, inProgressColumnWidths, inProgressSorterConstants, inProgressTasksProvider, new InProgressTasksLabelProvider());
		
		//createPlannedTasksSection(editorComposite, toolkit);
	}

	@Override
	public void setFocus() {		
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit) {
		Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		summarySection.setText("Mylar Task Planner");			
		summarySection.setLayout(new TableWrapLayout());
		summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
		Composite summaryContainer = toolkit.createComposite(summarySection);
		summarySection.setClient(summaryContainer);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;						
		summaryContainer.setLayout(layout);
		
		int length = editorInput.getCompletedTasks().size();
		String numComplete = "Number of completed tasks: " + editorInput.getCompletedTasks().size();
		Label label = toolkit.createLabel(summaryContainer, numComplete, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		
		String avgTime = "Average time spent per completed task: ";
		if (length > 0) {
			avgTime =  avgTime + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks() / editorInput.getCompletedTasks().size());		
		} else {
			avgTime =  avgTime + 0;
		}
		label = toolkit.createLabel(summaryContainer, avgTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		
		String totalCompletedTaskTime = "Total time spent on completed tasks: " + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks());
		label = toolkit.createLabel(summaryContainer, totalCompletedTaskTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	
		String numInProgress = "Number of tasks in progress: " + editorInput.getInProgressTasks().size();
		label = toolkit.createLabel(summaryContainer, numInProgress, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		
		String totalInProgressTaskTime = "Total time spent on tasks in progress: " + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnInProgressTasks());
		label = toolkit.createLabel(summaryContainer, totalInProgressTaskTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));	
		
		String grandTotalTime = "Total time spent on all tasks: " + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpentOnCompletedTasks() + editorInput.getTotalTimeSpentOnInProgressTasks());
		label = toolkit.createLabel(summaryContainer,  grandTotalTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));		
	
	}
	
	private void createTableSection(Composite parent, FormToolkit toolkit, String title, String[] columnNames, int[] columnWidths, int[] sorterConstants, IStructuredContentProvider contentProvider, LabelProvider labelProvider) {
		Section detailSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		detailSection.setText(title);			
		detailSection.setLayout(new TableWrapLayout());
		detailSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
		Composite detailContainer = toolkit.createComposite(detailSection);
		detailSection.setClient(detailContainer);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;						
		detailContainer.setLayout(layout);
		
		createTable(detailContainer, toolkit, columnNames, columnWidths, sorterConstants, contentProvider, labelProvider);
	}
	
	/** TODO: Comment parameters */
	private Table createTable(Composite parent, FormToolkit toolkit, String[] columnNames, int[] columnWidths, final int[] sorterConstants, IStructuredContentProvider contentProvider, LabelProvider labelProvider) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		Table table = toolkit.createTable(parent, style );		
		TableLayout tlayout = new TableLayout();
		table.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 300;
		wd.grabVertical = true;
		table.setLayoutData(wd);
				
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(true);
		
		TableColumn firstColumn = new TableColumn(table, SWT.LEFT, 0);
		firstColumn.setText(" ");
		firstColumn.setWidth(30);	
		
		for (int i = 0; i < columnNames.length; i++){
			TableColumn column = new TableColumn(table, SWT.LEFT, i+1);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}
		
		TableViewer tableViewer = createTableViewer(table, contentProvider, labelProvider);
		
		for (int i = 0; i < columnNames.length; i++){
			TableColumn column = table.getColumn(i + 1);
			addColumnSelectionListener(tableViewer, column, sorterConstants[i]);
		}	

		colorRows(table, contentProvider);
		
		return table;
	}

	private void addColumnSelectionListener(final TableViewer tableViewer, TableColumn column, final int sorterConstant){
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TaskSorter(sorterConstant));
			}
		});
	}
	
	private TableViewer createTableViewer(Table table, IStructuredContentProvider contentProvider, LabelProvider labelProvider) {
		TableViewer tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(completedColumnNames);
		
		tableViewer.setContentProvider( contentProvider );
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setInput(editorInput);

		tableViewers.add(tableViewer);
		
		
//		tableViewer.addFilter(new ViewerFilter(){
//
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				// TODO Auto-generated method stub
//				
//				//((TableViewer)viewer).
//				
//				
//				return true;
//			}
//			
//			
//			
//		});
		
		
		return tableViewer;
	}	
	
	/**
	 * TODO: make private
	 */
	public void createPlannedTasksSection(Composite parent, FormToolkit toolkit) {
		Section planSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		planSection.setText("Planned Tasks");			
		planSection.setLayout(new TableWrapLayout());
		planSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
		Composite planContainer = toolkit.createComposite(planSection);
		planSection.setClient(planContainer);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;						
		planContainer.setLayout(layout);
		
		createPlanTable(planContainer, toolkit);
		createPlanTableViewer();
		createRemoveButton(planContainer, toolkit);
		hookDoubleClickAction();
	}
	
	private void createPlanTable(Composite parent, FormToolkit toolkit) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		planTable = toolkit.createTable(parent, style );		
		TableLayout tlayout = new TableLayout();
		planTable.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 300;
		wd.grabVertical = true;
		planTable.setLayoutData(wd);
				
		planTable.setLinesVisible(true);
		planTable.setHeaderVisible(true);
		planTable.setEnabled(true);

		TableColumn column = new TableColumn(planTable, SWT.LEFT, 0);
		column.setText(planColumnNames[0]);
		column.setWidth(30);

		column = new TableColumn(planTable, SWT.LEFT, 1);
		column.setText(planColumnNames[1]);
		column.setWidth(300);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				planTableViewer.setSorter(new TaskSorter(PlannedTasksSorter.DESCRIPTION));
			}
		});

		column = new TableColumn(planTable, SWT.LEFT, 2);
		column.setText(planColumnNames[2]);
		column.setWidth(50);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				planTableViewer.setSorter(new TaskSorter(PlannedTasksSorter.PRIORITY));
			}
		});
		
		column = new TableColumn(planTable, SWT.LEFT, 3);
		column.setText(planColumnNames[3]);
		column.setWidth(170);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				planTableViewer.setSorter(new TaskSorter(PlannedTasksSorter.ESTIMATE));
			}
		});

		
		column = new TableColumn(planTable, SWT.LEFT, 4);
		column.setText(planColumnNames[4]);
		column.setWidth(170);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				planTableViewer.setSorter(new TaskSorter(PlannedTasksSorter.REMINDER));
			}
		});
		
	}
	
	private void createRemoveButton(Composite parent, FormToolkit toolkit) {
//		Composite composite = toolkit.createComposite(parent);
		Button delete = toolkit.createButton(parent, "Remove Task", SWT.PUSH | SWT.CENTER);
		
		delete.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ITask task = (ITask) ((IStructuredSelection) planTableViewer.getSelection()).getFirstElement();
				if (task != null) {
					contentProvider.removeTask(task);
					planTableViewer.refresh();
				}
			}
		});
	}
	private void createPlanTableViewer() {
		CellEditor[] editors = new CellEditor[planColumnNames.length];

		reminderEditor = new ReminderCellEditor(planTable);
		editors[0] = reminderEditor; // not used
		editors[1] = reminderEditor;// not used
		editors[2] = reminderEditor;// not used
		editors[3] = new ComboBoxCellEditor(planTable, ESTIMATE_TIMES,SWT.READ_ONLY);
		editors[4] = reminderEditor;
		reminderEditor.addListener(new ICellEditorListener() {
			public void applyEditorValue() {
				Object selection = ((IStructuredSelection)planTableViewer.getSelection()).getFirstElement();
				if (selection instanceof ITask) {
					((ITask)selection).setReminderDate(reminderEditor.getReminderDate());
					planTableViewer.refresh();
				}
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}
			
		});		
		planTableViewer = new TableViewer(planTable);
		planTableViewer.setCellEditors(editors);
		planTableViewer.setUseHashlookup(true);
		planTableViewer.setColumnProperties(planColumnNames);
		
		planTableViewer.setContentProvider(contentProvider);
		planTableViewer.setLabelProvider(new PlannedTasksLabelProvider());
		planTableViewer.setCellModifier(new PlannedTasksCellModifier());
		planTableViewer.setInput(editorInput);
		
		initDrop();
	}
	
	@MylarWebRef(name="Drag and drop article", url="http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
    private void initDrop() {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		planTableViewer.addDropSupport(DND.DROP_MOVE, types, new ViewerDropAdapter(planTableViewer) {
			{
				setFeedbackEnabled(false);
			}
			@Override
			public boolean performDrop(Object data) {
				Object selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
				if (selectedObject instanceof ITask) {
					contentProvider.addTask((ITask)selectedObject);
					planTableViewer.refresh();
					return true;
				} else if (selectedObject instanceof ITaskListElement) {
					if (MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement)selectedObject) != null) {
						ITask t = MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement)selectedObject).dropItemToPlan((ITaskListElement)selectedObject);
						contentProvider.addTask(t);
						planTableViewer.refresh();
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public boolean validateDrop(Object targetObject,int operation, TransferData transferType) {
				Object selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
                if (selectedObject instanceof ITaskListElement && ((ITaskListElement)selectedObject).isDragAndDropEnabled()) {
                    return true;                    
                }                
                return TextTransfer.getInstance().isSupportedType(transferType);
			}
		});
	}
	
	private class PlannedTasksCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			int columnIndex = Arrays.asList(planColumnNames).indexOf(property);			
			if (columnIndex == 4 || columnIndex == 3) {				
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (element instanceof ITask) {
				int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
				if (element instanceof ITask) {
					if (columnIndex == 4) {
						return ((ITask)element).getReminderDateString(true);
					} else if (columnIndex == 3) {
						return new Integer(Arrays.asList(ESTIMATE_TIMES).indexOf(((ITask)element).getEstimateTimeForDisplay()));
					}
					
				}
			}			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			int columnIndex = Arrays.asList(planColumnNames).indexOf(property);
			if (element instanceof ITask) {
				ITask task = (ITask) element;
				if (columnIndex == 3) {
					if (value instanceof Integer) {
						task.setEstimatedTime(((Integer) value).intValue() * 10);					
						planTableViewer.refresh();
					}
				}
			}			
		}
	}
	
	private void hookDoubleClickAction() {
		planTableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }
	public class OpenTaskEditorAction extends Action {
		@Override
		public void run() {
		    ISelection selection = planTableViewer.getSelection();
		    Object obj = ((IStructuredSelection)selection).getFirstElement();
		    if (obj instanceof Task) {
		    	((Task)obj).openTaskInEditor(false);
		    }
		    planTableViewer.refresh(obj);
		}
	}
	
	
	/**
	 * Sets the font color of a row blue if the task was created during the period
	 */
	private void colorRows(Table table, IStructuredContentProvider provider){
		Object[] tasks = provider.getElements(null);
		for(int i = 0; i < tasks.length; i++){
			if(editorInput.createdDuringReportPeriod((ITask)tasks[i])){
				table.getItem(i).setForeground(new Color(null, new RGB(0,0,255)));
			}
		}
	}
	
}
