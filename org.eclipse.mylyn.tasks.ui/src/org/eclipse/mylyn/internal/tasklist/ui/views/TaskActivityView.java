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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskActivityListener;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Rob Elves
 */
public class TaskActivityView extends ViewPart {

	public static final String ID = "org.eclipse.mylar.tasklist.activity";

	private static TaskActivityView INSTANCE;

	private OpenTaskListElementAction openTaskEditor;

	// private OpenTaskInExternalBrowserAction openUrlInExternal;

	private String[] columnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated", "Reminder" };

	private int[] columnWidths = new int[] { 60, 30, 340, 90, 90, 100 };

	private TreeColumn[] columns;

	private static final int DEFAULT_SORT_DIRECTION = -1;

	private int sortIndex = 2;

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private TaskActivityLabelProvider taskHistoryTreeLabelProvider;

	private TreeViewer treeViewer;

	private final ITaskActivityListener TASK_LISTENER = new ITaskActivityListener() {

		public void taskActivated(ITask task) {
			// ignore
			
		}

		public void tasksActivated(List<ITask> tasks) {
			// ignore
			
		}

		public void taskDeactivated(ITask task) {
			// ignore
			
		}

		public void localInfoChanged(ITask task) {
			// ignore
			
		}

		public void repositoryInfoChanged(ITask task) {
			// ignore
			
		}

		public void tasklistRead() {
			getViewer().setInput(getViewSite());
		}

		public void taskListModified() {
			// ignore	
		}
	};
	
	private final IMylarContextListener CONTEXT_LISTENER = new IMylarContextListener() {

		public void contextActivated(IMylarContext context) {
			getViewer().setInput(getViewSite());
		}

		public void contextDeactivated(IMylarContext context) {
			
		}

		public void interestChanged(IMylarElement element) {
//			String taskHandle = element.getHandleIdentifier();
//			List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext().getInteractionHistory();
//			InteractionEvent event = events.get(events.size()-1);
		}
		
		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
		}


		public void interestChanged(List<IMylarElement> elements) {
			// ignore		
		}

		public void nodeDeleted(IMylarElement element) {
			// ignore	
		}

		public void landmarkAdded(IMylarElement element) {
			// ignore	
		}

		public void landmarkRemoved(IMylarElement element) {
			// ignore	
		}

		public void edgesChanged(IMylarElement element) {
			// ignore	
		}
	};
	
	public static TaskActivityView openInActivePerspective() {
		try {
			return (TaskActivityView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (Exception e) {
			return null;
		}
	}

	public TaskActivityView() {
		INSTANCE = this;
		MylarTaskListPlugin.getTaskListManager().addListener(TASK_LISTENER);
		MylarPlugin.getContextManager().addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_LISTENER);
		MylarPlugin.getContextManager().removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	private class TaskHistoryTableSorter extends ViewerSorter {

		private String column;

		public TaskHistoryTableSorter(String column) {
			super();
			this.column = column;
		}

		/**
		 * compare - invoked when column is selected calls the actual comparison
		 * method for particular criteria
		 */
		@Override
		public int compare(Viewer compareViewer, Object o1, Object o2) {
			if (o1 instanceof ITaskContainer && o2 instanceof ITaskContainer && ((ITaskContainer) o2).isArchive()) {
				return -1;
			} else if (o2 instanceof ITaskContainer && o1 instanceof ITaskContainer
					&& ((ITaskContainer) o2).isArchive()) {
				return 1;
			}

			if (o1 instanceof ITaskContainer && o2 instanceof ITask) {
				return 1;
			}
			if (o1 instanceof ITaskContainer || o1 instanceof AbstractRepositoryQuery) {
				if (o2 instanceof ITaskContainer || o2 instanceof AbstractRepositoryQuery) {
					return sortDirection
							* ((ITaskListElement) o1).getDescription().compareTo(
									((ITaskListElement) o2).getDescription());
				} else {
					return -1;
				}
			} else if (o1 instanceof ITaskListElement) {
				if (o2 instanceof ITaskContainer || o2 instanceof AbstractRepositoryQuery) {
					return -1;
				} else if (o2 instanceof ITaskListElement) {
					ITaskListElement element1 = (ITaskListElement) o1;
					ITaskListElement element2 = (ITaskListElement) o2;

					if (column != null && column.equals(columnNames[1])) {
						return 0;
					} else if (column == columnNames[2]) {
						return sortDirection * element1.getPriority().compareTo(element2.getPriority());
					} else if (column == columnNames[3]) {
						String c1 = element1.getDescription();
						String c2 = element2.getDescription();
						try {
							return new Integer(c1).compareTo(new Integer(c2));
						} catch (Exception e) {
						}

						return sortDirection * c1.compareTo(c2);

					} else {
						return 0;
					}
				}
			} else {
				return 0;
			}
			return 0;
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		getViewer().getTree().setHeaderVisible(true);
		getViewer().getTree().setLinesVisible(true);
		getViewer().setColumnProperties(columnNames);
		getViewer().setUseHashlookup(true);

		columns = new TreeColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new TreeColumn(getViewer().getTree(), SWT.LEFT);
			columns[i].setText(columnNames[i]);
			columns[i].setWidth(columnWidths[i]);
			final int index = i;
			columns[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					sortIndex = index;
					sortDirection *= DEFAULT_SORT_DIRECTION;
					getViewer().setSorter(new TaskHistoryTableSorter(columnNames[sortIndex]));
				}
			});
			columns[i].addControlListener(new ControlListener() {
				public void controlResized(ControlEvent e) {
					for (int j = 0; j < columnWidths.length; j++) {
						if (columns[j].equals(e.getSource())) {
							columnWidths[j] = columns[j].getWidth();
						}
					}
				}

				public void controlMoved(ControlEvent e) {
					// don't care if the control is moved
				}
			});
		}

		// taskListTableLabelProvider = new TaskListTableLabelProvider(new
		// TaskElementLabelProvider(), PlatformUI.getWorkbench()
		// .getDecoratorManager().getLabelDecorator(), parent.getBackground());
//		taskHistoryTreeLabelProvider = new TaskActivityLabelProvider();
		taskHistoryTreeLabelProvider = new TaskActivityLabelProvider(new TaskElementLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator(), parent.getBackground());


		getViewer().setSorter(new TaskHistoryTableSorter(columnNames[sortIndex]));
		getViewer().setContentProvider(new TaskActivityContentProvider(this));
		getViewer().setLabelProvider(taskHistoryTreeLabelProvider);
		getViewer().setInput(getViewSite());

		makeActions();
		hookOpenAction();

	}

	private void makeActions() {
		openTaskEditor = new OpenTaskListElementAction(this.getViewer());
		// openUrlInExternal = new OpenTaskInExternalBrowserAction();
	}

	private void hookOpenAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openTaskEditor.run();
			}
		});
	}

	public static TaskActivityView getDefault() {
		return INSTANCE;
	}

	public TreeViewer getViewer() {
		return treeViewer;
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public ITask getSelectedTask() {
		ISelection selection = getViewer().getSelection();
		if (selection.isEmpty())
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
				return (ITask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
		// ignore

	}

}
