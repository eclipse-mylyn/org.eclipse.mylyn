/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.progress.IProgressService;

/**
 * Displays the results of a Repository search.
 * 
 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage
 * 
 * @author Rob Elves
 * @author Mik Kersten
 */
public class RepositorySearchResultView extends AbstractTextSearchViewPage implements IAdaptable {

	// The categories to sort bug results by

	public static final int ORDER_PRIORITY = 1;

	public static final int ORDER_DESCRIPTION = 2;

	public static final int ORDER_SEVERITY = 3;

	public static final int ORDER_STATUS = 4;

	public static final int ORDER_ID = 5;

	public static final int ORDER_DEFAULT = ORDER_PRIORITY;

	private static final String KEY_SORTING = TasksUiPlugin.ID_PLUGIN + ".search.resultpage.sorting"; //$NON-NLS-1$

	private SearchResultContentProvider taskContentProvider;

	private int currentSortOrder;

	private SearchResultSortAction sortByPriorityAction;

	private SearchResultSortAction sortByDescriptionAction;

	private OpenSearchResultAction openInEditorAction;

	private CreateQueryFromSearchAction addTaskListAction;

	private static final String[] SHOW_IN_TARGETS = new String[] { IPageLayout.ID_RES_NAV };

	private static final IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};

	public RepositorySearchResultView() {
		// Only use the table layout.
		super(FLAG_LAYOUT_TREE);

		sortByPriorityAction = new SearchResultSortAction("Task Priority", this, ORDER_PRIORITY);
		sortByDescriptionAction = new SearchResultSortAction("Task Summary", this, ORDER_DESCRIPTION);
		currentSortOrder = ORDER_DEFAULT;

		openInEditorAction = new OpenSearchResultAction("Open in Editor", this);
		addTaskListAction = new CreateQueryFromSearchAction("Create Query from Search...", this);
	}

	@Override
	protected void elementsChanged(Object[] objects) {
		if (taskContentProvider != null) {
			taskContentProvider.elementsChanged(objects);
		}
	}

	@Override
	protected void clear() {
		if (taskContentProvider != null) {
			taskContentProvider.clear();
		}
	}

	// Allows the inherited method "getViewer" to be accessed publicly.
	@Override
	public StructuredViewer getViewer() {
		return super.getViewer();
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
//		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
//		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(
//				TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);
//
//		SearchViewTableLabelProvider taskListTableLabelProvider = new SearchViewTableLabelProvider(
//				new TaskElementLabelProvider(true),
//				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), categoryBackground);

		DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new SearchResultTreeContentProvider(this));

		// Set the order when the search view is loading so that the items are
		// sorted right away
		setSortOrder(currentSortOrder);
		taskContentProvider = (SearchResultContentProvider) viewer.getContentProvider();
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

		TasksUiUtil.openRepositoryTask(repositoryHit.getRepositoryUrl(), repositoryHit.getTaskId(),
				repositoryHit.getUrl());
	}

	@Override
	protected void fillContextMenu(IMenuManager menuManager) {
		super.fillContextMenu(menuManager);
		MenuManager sortMenuManager = new MenuManager(SearchMessages.SortDropDownAction_label);
		sortMenuManager.add(sortByPriorityAction);
		sortMenuManager.add(sortByDescriptionAction);

		sortByPriorityAction.setChecked(currentSortOrder == sortByPriorityAction.getSortOrder());
		sortByDescriptionAction.setChecked(currentSortOrder == sortByDescriptionAction.getSortOrder());

		// Add the new context menu items
		menuManager.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenuManager);
		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, openInEditorAction);
		menuManager.appendToGroup(IContextMenuConstants.GROUP_OPEN, addTaskListAction);

		// HACK: this should be a contribution
		final MenuManager subMenuManager = new MenuManager("Add to " + TaskListView.LABEL_VIEW + " Category");
		List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(TasksUiPlugin.getTaskListManager()
				.getTaskList()
				.getCategories());

		Collections.sort(categories);
		for (final AbstractTaskCategory category : categories) {
			if (!category.equals(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer())) {
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
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getRepositoryUrl());
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

//	class SearchViewTableLabelProvider extends TaskTableLabelProvider {
//
//		public SearchViewTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBackground) {
//			super(provider, decorator, parentBackground);
//		}
//
//		@Override
//		public Image getColumnImage(Object element, int columnIndex) {
//			switch (columnIndex) {
//			case 0:
//				return super.getColumnImage(element, columnIndex);
//			}
//			return null;
//		}
//
//		@Override
//		public String getColumnText(Object element, int columnIndex) {
//			switch (columnIndex) {
//			case 0:
//				return super.getColumnText(element, columnIndex);
//			}
//			return null;
//		}
//
//		@Override
//		public Color getBackground(Object element, int columnIndex) {
//			// Note: see bug 142889
//			return null;
//		}
//	}

}
