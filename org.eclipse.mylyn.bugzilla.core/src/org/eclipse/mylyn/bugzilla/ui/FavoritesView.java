/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.favorites.Favorite;
import org.eclipse.mylar.bugzilla.favorites.FavoritesFile;
import org.eclipse.mylar.bugzilla.favorites.actions.AbstractFavoritesAction;
import org.eclipse.mylar.bugzilla.favorites.actions.DeleteFavoriteAction;
import org.eclipse.mylar.bugzilla.favorites.actions.ViewFavoriteAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;


/**
 * A view that shows any bug marked as favorites.
 */
public class FavoritesView extends ViewPart {
	
	private static Composite savedParent;	
	
	private IMemento savedMemento;
	
	private static DeleteFavoriteAction remove;

	public static DeleteFavoriteAction removeAll;

	public static SelectAllAction selectAll;

	private static ViewFavoriteAction open;
	
	private Table table;

	private MenuManager contextMenu;
	
	private static TableViewer viewer;
	
	private String[] columnHeaders = {
		"Bug",
		"Query",
		"Date"
	};
	
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(10),
		new ColumnWeightData(3),
		new ColumnWeightData(5)
	};
	
	/**
	 * Constructor initializes favorites' source file initializes actions
	 */
	public FavoritesView() {
		super();
		open = new ViewFavoriteAction(this);
		selectAll = new SelectAllAction();
		remove = new DeleteFavoriteAction(this, false);
		removeAll = new DeleteFavoriteAction(this, true);
	}
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	/**
	 * Initializes this view with the given view site. A memento is passed to
	 * the view which contains a snapshot of the views state from a previous
	 * session.
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.savedMemento = memento;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FavoritesView.savedParent = parent;
		setPartName("Bugzilla Favorites");
		createTable();
	
		viewer = new TableViewer(table);
		viewer.setUseHashlookup(true);
		createColumns();

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = 20;
		viewer.getTable().setLayoutData(gd);
	
		viewer.setContentProvider(new FavoritesViewContentProvider(this));
		viewer.setLabelProvider(new FavoritesViewLabelProvider());
		viewer.setInput(BugzillaPlugin.getDefault().getFavorites().elements());
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				FavoritesView.this.widgetSelected(event);
			}
		});
		
		fillToolbar();
		createContextMenu();
		
		Menu menu = contextMenu.createContextMenu(table);
		table.setMenu(menu);
		
		hookGlobalActions();
		parent.layout();
						
		// Restore state from the previous session.
		restoreState();
	}
	
	@Override
	public void setFocus() {
		// don't need to do anything when the focus is set
	}

	private void createColumns() {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE, i);
	 
			tc.setText(columnHeaders[i]);
			tc.pack();
			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
		}
	}
	
	private void createTable() {
		
		table = new Table(savedParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		
		// Add action support for a double-click
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				open.run();
			}
		});
	}
	
	private void fillToolbar() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolbar = actionBars.getToolBarManager();
		
		remove.setEnabled(false);
		toolbar.add(remove);
		toolbar.add(removeAll);
		toolbar.add(new Separator());
		toolbar.add(selectAll);
	
		// create actions to handle the sorting of the favorites
		sortByIDAction = new SortByAction(FavoritesFile.ID_SORT);
		sortByIDAction.setText("by &Bug ID");
		sortByIDAction.setToolTipText("Sorts by Bug number");
		
		sortByPriorityAction = new SortByAction(FavoritesFile.PRIORITY_SORT);
		sortByPriorityAction.setText("by &Priority");
		sortByPriorityAction.setToolTipText("Sorts by priority of the bug");
		
		sortBySeverityAction = new SortByAction(FavoritesFile.SEVERITY_SORT);
		sortBySeverityAction.setText("by &Severity");
		sortBySeverityAction.setToolTipText("Sorts by severity of the bug");
		
		sortByStatusAction = new SortByAction(FavoritesFile.STATE_SORT);
		sortByStatusAction.setText("by S&tatus");
		sortByStatusAction.setToolTipText("Sorts by status of the bug");
	
		// get the menu manager and create a submenu to contain sorting
		IMenuManager menu = actionBars.getMenuManager();
		IMenuManager submenu = new MenuManager("&Sort");

		// add the sorting actions to the menu bar
		menu.add(submenu);
		submenu.add(sortByIDAction);
		submenu.add(sortBySeverityAction);
		submenu.add(sortByPriorityAction);
		submenu.add(sortByStatusAction);
		
		updateSortingState();
	}
	
	/**
	 * Function to make sure that the appropriate sort is checked
	 */
	void updateSortingState() {
		int curCriterion = FavoritesFile.lastSel;
		
		sortByIDAction.setChecked(curCriterion == FavoritesFile.ID_SORT);
		sortBySeverityAction.setChecked(curCriterion == FavoritesFile.SEVERITY_SORT);
		sortByPriorityAction.setChecked(curCriterion == FavoritesFile.PRIORITY_SORT);
		sortByStatusAction.setChecked(curCriterion == FavoritesFile.STATE_SORT);
		viewer.setInput(viewer.getInput());
	}
	
	// Sorting actions for the favorites view
	SortByAction sortByIDAction, sortBySeverityAction, sortByPriorityAction, sortByStatusAction;
	
	/**
	 * Inner class to handle sorting
	 * @author Shawn Minto
	 */
	class SortByAction extends Action {
		/** The criteria to sort the favorites menu based on */
		private int criterion;
		
		/**
		 * Constructor
		 * @param criteria The criteria to sort the favorites menu based on
		 */
		public SortByAction(int criteria) {
			this.criterion = criteria;
		}

		/**
		 * Perform the sort
		 */
		@Override
		public void run() {
			BugzillaPlugin.getDefault().getFavorites().sort(criterion);
			updateSortingState();
		}
	}
	
	/**
	 * Create context menu.
	 */
	private void createContextMenu() {
		contextMenu = new MenuManager("#FavoritesView");
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
				updateActionEnablement();
			}
		});
	   
		// Register menu for extension.
		getSite().registerContextMenu("#FavoritesView", contextMenu, viewer);
	}
	
	/**
	 * Hook global actions
	 */
	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), remove);
		table.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0 && 
						remove.isEnabled()) {
					remove.run();
				}
			}
		});
	}
	
	/**
	 * Populate context menu
	 */
	private void fillContextMenu(IMenuManager mgr) {
		mgr.add(open);
		mgr.add(new Separator());
		mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		mgr.add(new Separator());
		mgr.add(remove);
		mgr.add(new DeleteFavoriteAction(this, true));
		mgr.add(new SelectAllAction());
	}
	
	/**
	 * Update action enablement depending on whether or not any items are selected.
	 * Displays name of current item in status bar.
	 */
	public static void updateActionEnablement() {
		
		boolean hasSelected = viewer.getTable().getSelectionCount() > 0;
		remove.setEnabled(hasSelected);
		open.setEnabled(hasSelected);
		
		boolean hasItems = viewer.getTable().getItemCount() > 0;
		removeAll.setEnabled(hasItems);
		selectAll.setEnabled(hasItems);
	}
	
	@Override
	public void saveState(IMemento memento) {
		TableItem[] sel = table.getSelection();
		if (sel.length == 0)
			return;
		memento = memento.createChild("selection");
		for (int i = 0; i < sel.length; i++) {
			memento.createChild("descriptor", new Integer(table.indexOf(sel[i])).toString());
		}
	}
	
	private void restoreState() {
		if (savedMemento == null)
			return;
		savedMemento = savedMemento.getChild("selection");
		if (savedMemento != null) {
			IMemento descriptors[] = savedMemento.getChildren("descriptor");
			if (descriptors.length > 0) {
				int[] objList = new int[descriptors.length];
				for (int nX = 0; nX < descriptors.length; nX++) {
					String id = descriptors[nX].getID();
					objList[nX] = BugzillaPlugin.getDefault().getFavorites().find(Integer.valueOf(id).intValue());		
				}
				table.setSelection(objList);
			}
		}
		viewer.setSelection(viewer.getSelection(), true);
		savedMemento = null;
		updateActionEnablement();
	}

	/**
	 * Returns list of names of selected items.
	 */
	public List<BugzillaOpenStructure> getBugIdsOfSelected() {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();//TableItem[] sel = table.getSelection();
		List<?> sel = selection.toList();
		List<BugzillaOpenStructure> Ids = new ArrayList<BugzillaOpenStructure>();

		Iterator<?> itr = sel.iterator();
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof Favorite) {
				Favorite entry = (Favorite) o;
				Integer id = (Integer) entry.getAttributes().get(IBugzillaConstants.HIT_MARKER_ATTR_ID);
				Ids.add(new BugzillaOpenStructure(entry.getServer(), id, -1));
			}
		}
		
		return Ids;
	}
	
	/**
	 * Calls remove function in FavoritesFile
	 */
    @SuppressWarnings("unchecked")
	public void deleteSelectedFavorites() {
		List<Favorite> selection = ((IStructuredSelection)viewer.getSelection()).toList();
		BugzillaPlugin.getDefault().getFavorites().remove(selection);
		viewer.setInput(viewer.getInput());
	}
	
	/**
	 * Removes all of the favorites in the FavoritesFile.
	 */
	public void deleteAllFavorites() {
		BugzillaPlugin.getDefault().getFavorites().removeAll();
		viewer.setInput(viewer.getInput());
	}
	
	/**
	 * Refreshes the view.
	 */
	public static void add() {
		if (viewer != null)
			viewer.setInput(viewer.getInput());
	}
	

	/**
	 * @see SelectionListener#widgetSelected(SelectionEvent)
	 */
    @SuppressWarnings("unchecked")
	public void widgetSelected(SelectionChangedEvent e) {

		IStructuredSelection selection =
					(IStructuredSelection) e.getSelection();

		boolean enable = selection.size() > 0;
		selectAll.setEnabled(enable);
		remove.setEnabled(enable);
		open.setEnabled(enable);
		
		IStructuredSelection viewerSelection = (IStructuredSelection)viewer.getSelection();//TableItem[] sel = table.getSelection();
		List<Favorite> sel = viewerSelection.toList();
		if (sel.size() > 0) {
			IStatusLineManager manager = this.getViewSite().getActionBars().getStatusLineManager();
			manager.setMessage(sel.get(0).toString());// table.getItem(selected).getText(0));
		}

		updateActionEnablement();
	}
	
	/**
	 * Attempts to display this view on the workbench.
	 */
	public static void checkWindow() {
		if (savedParent == null || savedParent.isDisposed()) {
			IWorkbenchWindow w = BugzillaPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow();
			if (w != null) {
				IWorkbenchPage page = w.getActivePage();
				if (page != null) {
					try {
						page.showView(IBugzillaConstants.PLUGIN_ID + ".ui.favoritesView");
					} catch (PartInitException pie) {
						BugzillaPlugin.log(pie.getStatus());
					}
				}
			}
		}
	}
	
	/**
	 * Action class - "Select All"
	 */
	public class SelectAllAction extends AbstractFavoritesAction {
		
		public SelectAllAction() {
			setToolTipText("Select all favorites");
			setText("Select all");
			setIcon("Icons/selectAll.gif");
		}
		
		@Override
		public void run() {
			checkWindow();
			table.selectAll();
			viewer.setSelection(viewer.getSelection(), true);
			updateActionEnablement();
		}
	}		
	
	private class FavoritesViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Returns the label text for the given column of a recommendation in the table.
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Favorite) {
				Favorite f = (Favorite) element;
				switch (columnIndex) {
					case 0:
						return f.toString();
					case 1:
						return f.getQuery();
					case 2:
						return f.getDate().toString();
					default:
						return "Undefined column text";
				}
			}
			return ""; //$NON-NLS-1$
		}

		/*
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}
	}
	
	public void refresh() {
		// don't need to do anything to refresh
	}
	
	private class FavoritesViewContentProvider implements IStructuredContentProvider {

		private List results;
	
		/**
		 * The constructor.
		 */
		public FavoritesViewContentProvider(FavoritesView taskList) {
			// no setup to do
		}

		/**
		 * Returns the elements to display in the viewer 
		 * when its input is set to the given element. 
		 * These elements can be presented as rows in a table, items in a list, etc.
		 * The result is not modified by the viewer.
		 *
		 * @param inputElement the input element
		 * @return the array of elements to display in the viewer
		 */
		public Object[] getElements(Object inputElement) {
			if (results != null) {
				return results.toArray();
			}
			else return null;
		}

		/**
		 * Notifies this content provider that a given viewer's input has been changed.
		 * 
		 * @see IContentProvider#inputChanged
		 */
		public void inputChanged(Viewer viewerChanged, Object oldInput, Object newInput) {
			this.results = (List) newInput;
		
			if (viewerChanged.getInput() != null) {
				viewerChanged.getControl().getDisplay().syncExec(new Runnable() {		
					public void run() {
						FavoritesView.this.refresh();
					}
				});
			}
		}

		public void dispose() {
			if (results != null)
				results = null;
		}
	}
}
