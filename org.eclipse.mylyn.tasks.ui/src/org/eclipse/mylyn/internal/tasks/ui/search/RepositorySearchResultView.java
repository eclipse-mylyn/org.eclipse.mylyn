/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskSearchAction;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchResultTreeContentProvider.GroupBy;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListToolTip;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.progress.IProgressService;

/**
 * Displays the results of a Repository search.
 * 
 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage
 * 
 * @author Rob Elves
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class RepositorySearchResultView extends AbstractTextSearchViewPage implements IAdaptable {

	private class GroupingAction extends Action {

		private final GroupBy groupBy;

		public GroupingAction(String text, GroupBy groupBy) {
			super(text, IAction.AS_CHECK_BOX);
			this.groupBy = groupBy;
			groupingActions.add(this);
		}

		public GroupBy getGroupBy() {
			return groupBy;
		}

		@Override
		public void run() {
			for (GroupingAction action : groupingActions) {
				action.setChecked(false);
			}

			SearchResultTreeContentProvider contentProvider = (SearchResultTreeContentProvider) getViewer().getContentProvider();
			if (contentProvider.getSelectedGroup() == groupBy) {
				contentProvider.setSelectedGroup(GroupBy.NONE);
			} else {
				contentProvider.setSelectedGroup(groupBy);
				setChecked(true);
			}
			getViewer().refresh();
		}
	}

	private class FilteringAction extends Action {

		private final ViewerFilter filter;

		public FilteringAction(String text, ViewerFilter filter) {
			super(text, IAction.AS_CHECK_BOX);
			this.filter = filter;
			filterActions.add(this);
		}

		@Override
		public void runWithEvent(Event event) {
			if (isChecked()) {
				getViewer().addFilter(filter);
			} else {
				getViewer().removeFilter(filter);
			}
		}
	}

	public static final int ORDER_PRIORITY = 1;

	public static final int ORDER_DESCRIPTION = 2;

	public static final int ORDER_SEVERITY = 3;

	public static final int ORDER_STATUS = 4;

	public static final int ORDER_ID = 5;

	public static final int ORDER_DEFAULT = ORDER_PRIORITY;

	private static final String KEY_SORTING = TasksUiPlugin.ID_PLUGIN + ".search.resultpage.sorting"; //$NON-NLS-1$

	private SearchResultContentProvider searchResultProvider;

	private int currentSortOrder;

	private final SearchResultSortAction sortByPriorityAction;

	private final SearchResultSortAction sortByDescriptionAction;

	private final OpenSearchResultAction openInEditorAction;

	private final CreateQueryFromSearchAction addTaskListAction;

	private final Action refineSearchAction;

	private static final String[] SHOW_IN_TARGETS = new String[] { IPageLayout.ID_RES_NAV };

	private TaskListToolTip toolTip;

	private final List<GroupingAction> groupingActions;

	private final List<FilteringAction> filterActions;

	private static final IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};

	public RepositorySearchResultView() {
		// Only use the table layout.
		super(FLAG_LAYOUT_TREE);

		sortByPriorityAction = new SearchResultSortAction(Messages.RepositorySearchResultView_Task_Priority, this,
				ORDER_PRIORITY);
		sortByDescriptionAction = new SearchResultSortAction(Messages.RepositorySearchResultView_Task_Summary, this,
				ORDER_DESCRIPTION);
		currentSortOrder = ORDER_DEFAULT;

		openInEditorAction = new OpenSearchResultAction(Messages.RepositorySearchResultView_Open_in_Editor, this);
		addTaskListAction = new CreateQueryFromSearchAction(
				Messages.RepositorySearchResultView_Create_Query_from_Search_, this);
		refineSearchAction = new OpenTaskSearchAction();
		refineSearchAction.setText(Messages.RepositorySearchResultView_Refine_Search_);

		groupingActions = new ArrayList<GroupingAction>();
		new GroupingAction(Messages.RepositorySearchResultView_Group_By_Owner, GroupBy.OWNER);
		//new GroupingAction("Group By Complete", GroupBy.COMPLETION);

		filterActions = new ArrayList<FilteringAction>();
		new FilteringAction(Messages.RepositorySearchResultView_Filter_Completed_Tasks, new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof ITask) {
					return !((ITask) element).isCompleted();
				} else if (element instanceof TaskGroup) {
					TaskGroup taskGroup = (TaskGroup) element;
					return taskGroup.getHandleIdentifier().equals("group-incompleteIncomplete"); //$NON-NLS-1$
				}
				return true;
			}
		});
	}

	@Override
	protected void elementsChanged(Object[] objects) {
		if (searchResultProvider != null) {
			searchResultProvider.elementsChanged(objects);
			getViewer().refresh();
		}
	}

	@Override
	protected void clear() {
		if (searchResultProvider != null) {
			searchResultProvider.clear();
			getViewer().refresh();
		}
	}

	// Allows the inherited method "getViewer" to be accessed publicly.
	@Override
	public StructuredViewer getViewer() {
		return super.getViewer();
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setUseHashlookup(true);
		searchResultProvider = new SearchResultTreeContentProvider();
		viewer.setContentProvider(searchResultProvider);

		DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new SearchResultsLabelProvider(
				searchResultProvider, viewer), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
		viewer.setLabelProvider(labelProvider);

		// Set the order when the search view is loading so that the items are
		// sorted right away
		setSortOrder(currentSortOrder);

		toolTip = new TaskListToolTip(viewer.getControl());
	}

	@Override
	protected TreeViewer createTreeViewer(Composite parent) {
		// create a filtered tree
		Composite treeComposite = parent;
		Layout parentLayout = parent.getLayout();
		if (!(parentLayout instanceof GridLayout)) {
			treeComposite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			treeComposite.setLayout(layout);
		}

		FilteredTree searchTree = new FilteredTree(treeComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
				new SubstringPatternFilter());
		return searchTree.getViewer();
	}

	@Override
	protected void configureTableViewer(TableViewer viewer) {
//		viewer.setUseHashlookup(true);
//		String[] columnNames = new String[] { "Summary" };
//		TableColumn[] columns = new TableColumn[columnNames.length];
//		int[] columnWidths = new int[] { 500 };
//		viewer.setColumnProperties(columnNames);
//
//		viewer.getTable().setHeaderVisible(false);
//		for (int i = 0; i < columnNames.length; i++) {
//			columns[i] = new TableColumn(viewer.getTable(), 0, i); // SWT.LEFT
//			columns[i].setText(columnNames[i]);
//			columns[i].setWidth(columnWidths[i]);
//			columns[i].setData(new Integer(i));
//			columns[i].addSelectionListener(new SelectionAdapter() {
//
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					TableColumn col = (TableColumn) e.getSource();
//					Integer integer = (Integer) col.getData();
//					setSortOrder(integer.intValue());
//				}
//			});
//		}
//
//		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
//		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(
//				TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);
//
//		SearchViewTableLabelProvider taskListTableLabelProvider = new SearchViewTableLabelProvider(
//				new TaskElementLabelProvider(true),
//				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), categoryBackground);
//
//		viewer.setLabelProvider(taskListTableLabelProvider);
//		viewer.setContentProvider(new SearchResultTableContentProvider(this));
//
//		// Set the order when the search view is loading so that the items are
//		// sorted right away
//		setSortOrder(currentSortOrder);
//
//		taskContentProvider = (SearchResultContentProvider) viewer.getContentProvider();
	}

	@Override
	public void dispose() {
		toolTip.dispose();
		super.dispose();
	}

	/**
	 * Sets the new sorting category, and reorders all of the tasks.
	 * 
	 * @param sortOrder
	 *            The new category to sort by
	 */
	public void setSortOrder(int sortOrder) {
		StructuredViewer viewer = getViewer();

		switch (sortOrder) {
		case ORDER_ID:
			viewer.setSorter(new SearchResultSorterId());
			break;
		case ORDER_DESCRIPTION:
			viewer.setSorter(new SearchResultSorterDescription());
			break;
		case ORDER_PRIORITY:
			viewer.setSorter(new SearchResultSorterPriority());
			break;
		default:
			// If the setting is not one of the four valid ones,
			// use the default order setting.
			sortOrder = ORDER_DEFAULT;
			viewer.setSorter(new SearchResultSorterPriority());
			break;
		}
		currentSortOrder = sortOrder;
		getSettings().put(KEY_SORTING, currentSortOrder);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return getAdapterDelegate(adapter);
	}

	private Object getAdapterDelegate(Class<?> adapter) {
		if (IShowInTargetList.class.equals(adapter)) {
			return SHOW_IN_TARGET_LIST;
		}
		return null;
	}

	@Override
	protected void showMatch(Match match, int currentOffset, int currentLength, boolean activate)
			throws PartInitException {
		AbstractTask repositoryHit = (AbstractTask) match.getElement();
		TasksUiInternal.refreshAndOpenTaskListElement(repositoryHit);
	}

	@Override
	protected void fillContextMenu(IMenuManager menuManager) {
		super.fillContextMenu(menuManager);
		MenuManager sortMenuManager = new MenuManager(""); //$NON-NLS-1$
		sortMenuManager.add(sortByPriorityAction);
		sortMenuManager.add(sortByDescriptionAction);

		sortByPriorityAction.setChecked(currentSortOrder == sortByPriorityAction.getSortOrder());
		sortByDescriptionAction.setChecked(currentSortOrder == sortByDescriptionAction.getSortOrder());

		// Add the new context menu items
		menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenuManager);
		for (Action action : groupingActions) {
			menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, action);
		}
		for (Action action : filterActions) {
			menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, action);
		}

		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, openInEditorAction);
		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, addTaskListAction);
		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, refineSearchAction);

		// HACK: this should be a contribution
		final MenuManager subMenuManager = new MenuManager(MessageFormat.format(
				Messages.RepositorySearchResultView_Add_to_X_Category, TaskListView.LABEL_VIEW));
		List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(TasksUiInternal.getTaskList()
				.getCategories());

		Collections.sort(categories);
		for (final AbstractTaskCategory category : categories) {
			if (!(category instanceof UnmatchedTaskContainer)) {//.equals(TasksUiPlugin.getTaskList().getArchiveContainer())) {
				Action action = new Action() {
					@Override
					public void run() {
						moveToCategory(category);
					}
				};
				String text = category.getSummary();
				action.setText(text);
				action.setImageDescriptor(TasksUiImages.CATEGORY);
				subMenuManager.add(action);
			}
		}
		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, subMenuManager);
	}

	private void moveToCategory(AbstractTaskCategory category) {
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		StructuredSelection selection = (StructuredSelection) this.getViewer().getSelection();
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
			Object selectedObject = iterator.next();
			if (selectedObject instanceof ITask) {
				ITask task = (ITask) selectedObject;
				TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				final AddExistingTaskJob job = new AddExistingTaskJob(repository, task.getTaskId(), category);
				job.schedule();
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						progressService.showInDialog(RepositorySearchResultView.this.getSite().getShell(), job);
					}
				});
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		IMenuManager menuManager = getSite().getActionBars().getMenuManager();
		for (Action action : groupingActions) {
			menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, action);
		}
		menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, new Separator());
		for (Action action : filterActions) {
			menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, action);
		}
	}

	@Override
	public void setInput(ISearchResult newSearch, Object viewState) {
		super.setInput(newSearch, viewState);
	}

}
