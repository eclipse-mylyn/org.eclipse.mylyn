/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.search;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.actions.BugzillaSortAction;
import org.eclipse.mylar.internal.bugzilla.ui.actions.OpenBugsAction;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.tasklist.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskElementLabelProvider;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListTableLabelProvider;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.SearchPreferencePage;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Displays the results of a Bugzilla search.
 * 
 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage
 */
public class BugzillaSearchResultView extends AbstractTextSearchViewPage implements IAdaptable {

	// The categories to sort bug results by
	public static final int ORDER_ID = 1;

	public static final int ORDER_SEVERITY = 2;

	public static final int ORDER_PRIORITY = 3;

	public static final int ORDER_STATUS = 4;

	public static final int ORDER_DEFAULT = ORDER_ID;

	private static final String KEY_SORTING = BugzillaUiPlugin.PLUGIN_ID + ".search.resultpage.sorting"; //$NON-NLS-1$

	private BugzillaContentProvider bugContentProvider;

	private int bugCurrentSortOrder;

	private BugzillaSortAction bugSortByIDAction;

	private BugzillaSortAction bugSortBySeverityAction;

	private BugzillaSortAction bugSortByPriorityAction;

	private BugzillaSortAction bugSortByStatusAction;

	// private AddFavoriteAction addToFavoritesAction;

	private OpenBugsAction openInEditorAction;

	private static final String[] SHOW_IN_TARGETS = new String[] { IPageLayout.ID_RES_NAV };

	private static final IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};

	private IPropertyChangeListener bugPropertyChangeListener;

	/**
	 * Constructor
	 */
	public BugzillaSearchResultView() {
		// Only use the table layout.
		super(FLAG_LAYOUT_FLAT);

		bugSortByIDAction = new BugzillaSortAction("Bug ID", this, ORDER_ID);
		bugSortBySeverityAction = new BugzillaSortAction("Bug severity", this, ORDER_SEVERITY);
		bugSortByPriorityAction = new BugzillaSortAction("Bug priority", this, ORDER_PRIORITY);
		bugSortByStatusAction = new BugzillaSortAction("Bug status", this, ORDER_STATUS);
		bugCurrentSortOrder = ORDER_DEFAULT;

		// addToFavoritesAction = new AddFavoriteAction("Mark Result as
		// Favorite", this);
		openInEditorAction = new OpenBugsAction("Open Bug in Editor", this);

		bugPropertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (SearchPreferencePage.LIMIT_TABLE.equals(event.getProperty())
						|| SearchPreferencePage.LIMIT_TABLE_TO.equals(event.getProperty()))
					if (getViewer() instanceof TableViewer) {
						getViewPart().updateLabel();
						getViewer().refresh();
					}
			}
		};
		SearchPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(bugPropertyChangeListener);
	}

	@Override
	protected void elementsChanged(Object[] objects) {
		if (bugContentProvider != null) {
			bugContentProvider.elementsChanged(objects);
		}
	}

	@Override
	protected void clear() {
		if (bugContentProvider != null) {
			bugContentProvider.clear();
		}
	}

	// Allows the inherited method "getViewer" to be accessed publicly.
	@Override
	public StructuredViewer getViewer() {
		return super.getViewer();
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
		// The tree layout is not used, so this function does not need to do
		// anything.

	}

	@Override
	protected void configureTableViewer(TableViewer viewer) {
		viewer.setUseHashlookup(true);
		String[] columnNames = new String[] { "", "!", "Description" };
		TableColumn[] columns = new TableColumn[columnNames.length];
		int[] columnWidths = new int[] { 20, 20, 500 };
		viewer.setColumnProperties(columnNames);
		
		viewer.getTable().setHeaderVisible(true);
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new TableColumn(viewer.getTable(), 0, i); // SWT.LEFT
			columns[i].setText(columnNames[i]);
			columns[i].setWidth(columnWidths[i]);
		}

		// TaskElementLabelProvider BugzillaLabelProvider
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);

		SearchViewTableLabelProvider taskListTableLabelProvider = new SearchViewTableLabelProvider(
				new TaskElementLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(),
				categoryBackground);

		viewer.setLabelProvider(taskListTableLabelProvider);
		viewer.setContentProvider(new BugzillaTableContentProvider(this));

		// Set the order when the search view is loading so that the items are
		// sorted right away
		setSortOrder(bugCurrentSortOrder);

		bugContentProvider = (BugzillaContentProvider) viewer.getContentProvider();
	}

	/**
	 * Sets the new sorting category, and reorders all of the bug reports.
	 * 
	 * @param sortOrder
	 *            The new category to sort bug reports by
	 */
	public void setSortOrder(int sortOrder) {
		bugCurrentSortOrder = sortOrder;
		StructuredViewer viewer = getViewer();

		switch (sortOrder) {
		case ORDER_ID:
			viewer.setSorter(new BugzillaIdSearchSorter());
			break;
		case ORDER_PRIORITY:
			viewer.setSorter(new BugzillaPrioritySearchSorter());
			break;
		case ORDER_SEVERITY:
			viewer.setSorter(new BugzillaSeveritySearchSorter());
			break;
		case ORDER_STATUS:
			viewer.setSorter(new BugzillaStateSearchSorter());
			break;
		default:
			// If the setting is not one of the four valid ones,
			// use the default order setting.
			sortOrder = ORDER_DEFAULT;
			viewer.setSorter(new BugzillaIdSearchSorter());
			break;
		}

		getSettings().put(KEY_SORTING, bugCurrentSortOrder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (IShowInTargetList.class.equals(adapter)) {
			return SHOW_IN_TARGET_LIST;
		}
		return null;
	}

	@Override
	protected void showMatch(Match match, int currentOffset, int currentLength, boolean activate)
			throws PartInitException {
		// try {
		BugzillaQueryHit repositoryHit = (BugzillaQueryHit) match.getElement();
		String bugUrl = BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryHit.getRepositoryUrl(), repositoryHit
				.getId());
		TaskUiUtil.openRepositoryTask(repositoryHit.getRepositoryUrl(), "" + repositoryHit.getId(), bugUrl);
		// Object element = getCurrentMatch().getElement();
		// if (element instanceof IMarker) {
		//
		// String repositoryUrl = (String) ((IMarker) element)
		// .getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_REPOSITORY);
		// Integer id = (Integer) ((IMarker)
		// element).getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_ID);
		// BugzillaUITools.show(repositoryUrl, id.intValue());
		// }
		// } catch (CoreException e) {
		// // if an error occurs, handle and log it
		// ExceptionHandler.handle(e, SearchMessages.Search_Error_search_title,
		// SearchMessages.Search_Error_search_message); //$NON-NLS-2$
		// //$NON-NLS-1$
		// BugzillaPlugin.log(e.getStatus());
		// }
	}

	@Override
	protected void fillContextMenu(IMenuManager mgr) {
		super.fillContextMenu(mgr);

		// Create the submenu for sorting
		MenuManager sortMenu = new MenuManager(SearchMessages.SortDropDownAction_label); //$NON-NLS-1$
		sortMenu.add(bugSortByIDAction);
		sortMenu.add(bugSortByPriorityAction);
		sortMenu.add(bugSortBySeverityAction);
		sortMenu.add(bugSortByStatusAction);

		// Check the right sort option
		bugSortByIDAction.setChecked(bugCurrentSortOrder == bugSortByIDAction.getSortOrder());
		bugSortByPriorityAction.setChecked(bugCurrentSortOrder == bugSortByPriorityAction.getSortOrder());
		bugSortBySeverityAction.setChecked(bugCurrentSortOrder == bugSortBySeverityAction.getSortOrder());
		bugSortByStatusAction.setChecked(bugCurrentSortOrder == bugSortByStatusAction.getSortOrder());

		// Add the new context menu items
		mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
		// mgr.appendToGroup(IContextMenuConstants.GROUP_ADDITIONS,
		// addToFavoritesAction);
		mgr.appendToGroup(IContextMenuConstants.GROUP_OPEN, openInEditorAction);
	}

	class SearchViewTableLabelProvider extends TaskListTableLabelProvider {

		public SearchViewTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBacground) {
			super(provider, decorator, parentBacground);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				++columnIndex;
				break;
			case 1:
				++columnIndex;
				break;
			case 2:
				columnIndex = 2 + columnIndex;
				break;
			}
			return super.getColumnImage(element, columnIndex);
		}

		@Override
		public String getColumnText(Object obj, int columnIndex) {
			switch (columnIndex) {
			case 0:
				++columnIndex;
				break;
			case 1:
				++columnIndex;
				break;
			case 2:
				columnIndex = 2 + columnIndex;
				break;
			}
			return super.getColumnText(obj, columnIndex);
		}

	}

}
