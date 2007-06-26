/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
///*******************************************************************************
// * Copyright (c) 2003 - 2006 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     University Of British Columbia - initial API and implementation
// *******************************************************************************/
//package org.eclipse.mylyn.internal.bugzilla.ui;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.jface.action.GroupMarker;
//import org.eclipse.jface.action.IMenuListener;
//import org.eclipse.jface.action.IMenuManager;
//import org.eclipse.jface.action.IStatusLineManager;
//import org.eclipse.jface.action.IToolBarManager;
//import org.eclipse.jface.action.MenuManager;
//import org.eclipse.jface.action.Separator;
//import org.eclipse.jface.viewers.ColumnLayoutData;
//import org.eclipse.jface.viewers.ColumnWeightData;
//import org.eclipse.jface.viewers.IContentProvider;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.ITableLabelProvider;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.TableLayout;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.mylyn.bugzilla.core.IBugzillaBug;
//import org.eclipse.mylyn.internal.bugzilla.core.BugzillaPlugin;
//import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
//import org.eclipse.mylyn.internal.bugzilla.core.IOfflineBugListener.BugzillaOfflineStaus;
//import org.eclipse.mylyn.internal.bugzilla.core.internal.OfflineReportsFile;
//import org.eclipse.mylyn.internal.bugzilla.ui.actions.AbstractOfflineReportsAction;
//import org.eclipse.mylyn.internal.bugzilla.ui.actions.DeleteOfflineReportAction;
//import org.eclipse.mylyn.internal.bugzilla.ui.actions.ViewOfflineReportAction;
//import org.eclipse.mylyn.internal.core.util.MylarStatusHandler;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.KeyAdapter;
//import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.events.MouseAdapter;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//import org.eclipse.ui.IActionBars;
//import org.eclipse.ui.IMemento;
//import org.eclipse.ui.IViewSite;
//import org.eclipse.ui.IWorkbenchActionConstants;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.actions.ActionFactory;
//import org.eclipse.ui.part.ViewPart;
//
///**
// * A view that displays any bugs that have been saved offline.
// */
//public class OfflineView extends ViewPart {
//
//	private static Composite parent;
//
//	private IMemento savedMemento;
//
//	private static DeleteOfflineReportAction remove;
//
//	public static DeleteOfflineReportAction removeAll;
//
//	public static SelectAllAction selectAll;
//
//	private static ViewOfflineReportAction open;
//
//	private Table table;
//
//	private MenuManager contextMenu;
//
//	private static TableViewer viewer;
//
//	private static OfflineReportsViewContentProvider contentProvider = new OfflineReportsViewContentProvider();
//
//	private String[] columnHeaders = { "Bug", "Summary", "Description" };
//
//	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(3), new ColumnWeightData(5),
//			new ColumnWeightData(10) };
//
//	/**
//	 * Constructor initializes OfflineReports' source file initializes actions
//	 */
//	public OfflineView() {
//		super();
//		open = new ViewOfflineReportAction(this);
//		selectAll = new SelectAllAction();
//		remove = new DeleteOfflineReportAction(this, false);
//		removeAll = new DeleteOfflineReportAction(this, true);
//	}
//
//	@Override
//	public void init(IViewSite site) throws PartInitException {
//		super.init(site);
//	}
//
//	/**
//	 * Initializes this view with the given view site. A memento is passed to
//	 * the view which contains a snapshot of the views state from a previous
//	 * session.
//	 */
//	@Override
//	public void init(IViewSite site, IMemento memento) throws PartInitException {
//		init(site);
//		this.savedMemento = memento;
//	}
//
//	@Override
//	public void createPartControl(Composite parentComposite) {
//		OfflineView.parent = parentComposite;
//		setPartName("Bugzilla Offline Reports");
//		createTable();
//
//		viewer = new TableViewer(table);
//		viewer.setUseHashlookup(true);
//		createColumns();
//
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.verticalSpan = 20;
//		viewer.getTable().setLayoutData(gd);
//
//		viewer.setContentProvider(contentProvider);
//		viewer.setLabelProvider(new OfflineReportsViewLabelProvider());
//		viewer.setInput(BugzillaPlugin.getDefault().getOfflineReports().elements());
//
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				OfflineView.this.widgetSelected(event);
//			}
//		});
//
//		fillToolbar();
//		createContextMenu();
//
//		Menu menu = contextMenu.createContextMenu(table);
//		table.setMenu(menu);
//
//		hookGlobalActions();
//		parentComposite.layout();
//
//		// Restore state from the previous session.
//		restoreState();
//	}
//
//	@Override
//	public void setFocus() {
//		// don't need to do anything when we get focus
//	}
//
//	private void createColumns() {
//		TableLayout layout = new TableLayout();
//		table.setLayout(layout);
//		table.setHeaderVisible(true);
//
//		for (int i = 0; i < columnHeaders.length; i++) {
//			TableColumn tc = new TableColumn(table, SWT.NONE, i);
//
//			tc.setText(columnHeaders[i]);
//			tc.pack();
//			tc.setResizable(columnLayouts[i].resizable);
//			layout.addColumnData(columnLayouts[i]);
//		}
//	}
//
//	private void createTable() {
//
//		table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
//		table.setLinesVisible(true);
//
//		// Add action support for a double-click
//		table.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDoubleClick(MouseEvent e) {
//				open.run();
//			}
//		});
//	}
//
//	private void fillToolbar() {
//		IActionBars actionBars = getViewSite().getActionBars();
//		IToolBarManager toolbar = actionBars.getToolBarManager();
//
//		remove.setEnabled(false);
//		toolbar.add(remove);
//		toolbar.add(removeAll);
//		toolbar.add(new Separator());
//		toolbar.add(selectAll);
//
//		// create actions to handle the sorting of the OfflineReports
//		// sortByIDAction = new SortByAction(OfflineReportManager.ID_SORT);
//		// sortByIDAction.setText("by &Bug ID");
//		// sortByIDAction.setToolTipText("Sorts by Bug number");
//		//		
//		// sortByTypeAction = new SortByAction(OfflineReportManager.TYPE_SORT);
//		// sortByTypeAction.setText("by &Bug Type");
//		// sortByTypeAction.setToolTipText("Sorts by locally created/from server
//		// status");
//
//		// get the menu manager and create a submenu to contain sorting
//		IMenuManager menu = actionBars.getMenuManager();
//		IMenuManager submenu = new MenuManager("&Sort");
//
//		// add the sorting actions to the menu bar
//		menu.add(submenu);
//		// submenu.add(sortByIDAction);
//		// submenu.add(sortByTypeAction);
//
//		// updateSortingState();
//	}
//
//	// /**
//	// * Function to make sure that the appropriate sort is checked
//	// */
//	// void updateSortingState() {
//	// int curCriterion = OfflineReportManager.lastSel;
//	//		
//	// sortByIDAction.setChecked(curCriterion == OfflineReportManager.ID_SORT);
//	// sortByTypeAction.setChecked(curCriterion ==
//	// OfflineReportManager.TYPE_SORT);
//	// viewer.setInput(viewer.getInput());
//	// }
//	//	
//	// // Sorting actions for the OfflineReports view
//	// SortByAction sortByIDAction, sortByTypeAction/*, sortBySeverityAction,
//	// sortByPriorityAction, sortByStatusAction*/;
//
//	// /**
//	// * Inner class to handle sorting
//	// * @author Shawn Minto
//	// */
//	// class SortByAction extends Action {
//	// /** The criteria to sort the OfflineReports menu based on */
//	// private int criterion;
//	//		
//	// /**
//	// * Constructor
//	// * @param criteria The criteria to sort the OfflineReports menu based on
//	// */
//	// public SortByAction(int criteria) {
//	// this.criterion = criteria;
//	// }
//	//
//	// /**
//	// * Perform the sort
//	// */
//	// @Override
//	// public void run() {
//	// BugzillaPlugin.getDefault().getOfflineReports().sort(criterion);
//	// updateSortingState();
//	// }
//	// }
//
//	/**
//	 * Create context menu.
//	 */
//	private void createContextMenu() {
//		contextMenu = new MenuManager("#OfflineReportsView");
//		contextMenu.setRemoveAllWhenShown(true);
//		contextMenu.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				fillContextMenu(manager);
//				updateActionEnablement();
//			}
//		});
//
//		// Register menu for extension.
//		getSite().registerContextMenu("#OfflineReportsView", contextMenu, viewer);
//	}
//
//	/**
//	 * Hook global actions
//	 */
//	private void hookGlobalActions() {
//		IActionBars bars = getViewSite().getActionBars();
//		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
//		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), remove);
//		table.addKeyListener(new KeyAdapter() {
//
//			@Override
//			public void keyPressed(KeyEvent event) {
//				if (event.character == SWT.DEL && event.stateMask == 0 && remove.isEnabled()) {
//					remove.run();
//				}
//			}
//		});
//	}
//
//	/**
//	 * Populate context menu
//	 */
//	private void fillContextMenu(IMenuManager mgr) {
//		mgr.add(open);
//		mgr.add(new Separator());
//		mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
//		mgr.add(new Separator());
//		mgr.add(remove);
//		mgr.add(new DeleteOfflineReportAction(this, true));
//		mgr.add(new SelectAllAction());
//	}
//
//	/**
//	 * Update action enablement depending on whether or not any items are
//	 * selected. Displays name of current item in status bar.
//	 */
//	public static void updateActionEnablement() {
//
//		boolean hasSelected = viewer.getTable().getSelectionCount() > 0;
//		remove.setEnabled(hasSelected);
//		open.setEnabled(hasSelected);
//
//		boolean hasItems = viewer.getTable().getItemCount() > 0;
//		removeAll.setEnabled(hasItems);
//		selectAll.setEnabled(hasItems);
//	}
//
//	@Override
//	public void saveState(IMemento memento) {
//		TableItem[] sel = table.getSelection();
//		if (sel.length == 0)
//			return;
//		memento = memento.createChild("selection");
//		for (int i = 0; i < sel.length; i++) {
//			memento.createChild("descriptor", new Integer(table.indexOf(sel[i])).toString());
//		}
//	}
//
//	private void restoreState() {
//		if (savedMemento == null)
//			return;
//		savedMemento = savedMemento.getChild("selection");
//		if (savedMemento != null) {
//			IMemento descriptors[] = savedMemento.getChildren("descriptor");
//			if (descriptors.length > 0) {
//				int[] objList = new int[descriptors.length];
//				for (int nX = 0; nX < descriptors.length; nX++) {
//					String taskId = descriptors[nX].getID();
//					objList[nX] = BugzillaPlugin.getDefault().getOfflineReports().find(Integer.valueOf(taskId).intValue());
//				}
//				table.setSelection(objList);
//			}
//		}
//		viewer.setSelection(viewer.getSelection(), true);
//		savedMemento = null;
//		updateActionEnablement();
//	}
//
//	/**
//	 * Returns list of names of selected items.
//	 */
//	@SuppressWarnings("unchecked")
//	public List<Integer> getBugIdsOfSelected() {
//		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();// TableItem[]
//																						// sel
//																						// =
//																						// table.getSelection();
//		List<Object> sel = selection.toList();
//		List<Integer> Ids = new ArrayList<Integer>();
//
//		Iterator<Object> itr = sel.iterator();
//		while (itr.hasNext()) {
//			Object o = itr.next();
//			if (o instanceof IBugzillaBug) {
//				IBugzillaBug entry = (IBugzillaBug) o;
//				Integer taskId = entry.getId();
//				if (!entry.isLocallyCreated()) {
//					Ids.add(taskId);
//				}
//			}
//		}
//
//		return Ids;
//	}
//
//	/**
//	 * @return List of selected offline bug reports.
//	 */
//	@SuppressWarnings("unchecked")
//	public List<IBugzillaBug> getSelectedBugs() {
//		List<Object> selection = ((IStructuredSelection) viewer.getSelection()).toList();
//		List<IBugzillaBug> bugs = new ArrayList<IBugzillaBug>();
//		for (Iterator<Object> iter = selection.iterator(); iter.hasNext();) {
//			Object obj = iter.next();
//			if (obj instanceof IBugzillaBug) {
//				bugs.add((IBugzillaBug) obj);
//			}
//		}
//		return bugs;
//	}
//
//	/**
//	 * Closes any open editors of the given offline reports.
//	 * 
//	 * @param reports
//	 *            The list of offline reports that need their editors closed.
//	 */
//	protected void closeOfflineReports(List<IBugzillaBug> reports) {
//		if (reports == null)
//			return;
//
//		IWorkbenchPage page = BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
//
//		// if we couldn't get the page, get out of here
//		if (page == null)
//			return;
//
//		for (Iterator<IBugzillaBug> iter = reports.iterator(); iter.hasNext();) {
//			IBugzillaBug bug = iter.next();
//			BugzillaUITools.closeEditor(page, bug);
//		}
//	}
//
//	/**
//	 * Refreshes the view.
//	 */
//	public static void refreshView() {
//		if (viewer != null && !viewer.getControl().isDisposed()) {
//			if (viewer.getContentProvider() == null)
//				viewer.setContentProvider(contentProvider);
//			if (viewer.getContentProvider() == null)
//				return;
//			viewer.setInput(viewer.getInput());
//		}
//	}
//
//	/**
//	 * Calls remove function in OfflineReportManager
//	 */
//	@SuppressWarnings("unchecked")
//	public void deleteSelectedOfflineReports() {
//		List<IBugzillaBug> selection = ((IStructuredSelection) viewer.getSelection()).toList();
//		closeOfflineReports(selection);
//		for (IBugzillaBug bug : selection) {
//			removeReport(bug);
//		}
//		viewer.setInput(viewer.getInput());
//	}
//
//	/**
//	 * Removes all of the offline reports in the OfflineReportManager.
//	 */
//	public void deleteAllOfflineReports() {
//		closeOfflineReports(BugzillaPlugin.getDefault().getOfflineReports().elements());
//		List<IBugzillaBug> reports = new ArrayList<IBugzillaBug>(OfflineReportManager.getOfflineBugs());
//		for (IBugzillaBug bug : reports) {
//			removeReport(bug);
//		}
//		viewer.setInput(viewer.getInput());
//	}
//
//	/**
//	 * Saves the given report to the offlineReportsFile, or, if it already
//	 * exists in the file, updates it.
//	 * 
//	 * @param bug
//	 *            The bug to add/update.
//	 * @param saveChosen
//	 *            This is used to determine a refresh from a user save
//	 */
//	public static void saveOffline(final IBugzillaBug bug, final boolean saveChosen) {
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//
//				OfflineReportManager file = BugzillaPlugin.getDefault().getOfflineReports();
//				// If there is already an offline report for this bug, update
//				// the file.
//				if (bug.isSavedOffline()) {
//					file.update();
//				}
//				// If this bug has not been saved offline before, add it to the
//				// file.
//				else {
//					// If there is already an offline report with the same taskId,
//					// don't save this report.
//					// int index = -1;
//					// if ((index = file.find(bug.getId())) >= 0) {
//					// removeReport(getOfflineBugs().get(index));
//					// MessageDialog.openInformation(null, "Bug's Id is already
//					// used.", "There is already a bug saved offline with an
//					// identical taskId.");
//					// return;
//					// }
//					try {
//						file.add(bug, saveChosen);
//						bug.setOfflineState(true);
//					} catch (CoreException e) {
//						MylarStatusHandler.fail(e, e.getMessage(), false);
//					}
//					// file.sort(OfflineReportManager.lastSel);
//				}
//				// OfflineView.checkWindow();
//				OfflineView.refreshView();
//			}
//		});
//	}
//
//	public static List<IBugzillaBug> getOfflineBugs() {
//		OfflineReportManager file = BugzillaPlugin.getDefault().getOfflineReports();
//		return file.elements();
//	}
//
//	/**
//	 * Removes the given report from the offlineReportsFile.
//	 * 
//	 * @param bug
//	 *            The report to remove.
//	 */
//	public static void removeReport(IBugzillaBug bug) {
//		BugzillaPlugin.getDefault().fireOfflineStatusChanged(bug, BugzillaOfflineStaus.DELETED);
//
//		ArrayList<IBugzillaBug> bugList = new ArrayList<IBugzillaBug>();
//		bugList.add(bug);
//		BugzillaPlugin.getDefault().getOfflineReports().remove(bugList);
//
//		if (viewer != null && !viewer.getControl().isDisposed()) {
//			if (viewer.getContentProvider() == null)
//				viewer.setContentProvider(contentProvider);
//			if (viewer.getContentProvider() == null)
//				return;
//			viewer.setInput(viewer.getInput());
//		}
//	}
//
////	public static IBugzillaBug find(int bugId) {
////		int location = BugzillaPlugin.getDefault().getOfflineReports().find(bugId);
////		if (location != -1) {
////			return BugzillaPlugin.getDefault().getOfflineReports().elements().get(location);
////		}
////		return null;
////	}
//
//	/**
//	 * @see SelectionListener#widgetSelected(SelectionEvent)
//	 */
//	@SuppressWarnings("unchecked")
//	public void widgetSelected(SelectionChangedEvent e) {
//
//		IStructuredSelection selection = (IStructuredSelection) e.getSelection();
//
//		boolean enable = selection.size() > 0;
//		selectAll.setEnabled(enable);
//		remove.setEnabled(enable);
//		open.setEnabled(enable);
//
//		IStructuredSelection viewerSelection = (IStructuredSelection) viewer.getSelection();// TableItem[]
//																							// sel
//																							// =
//																							// table.getSelection();
//		List<IBugzillaBug> sel = viewerSelection.toList();
//		if (sel.size() > 0) {
//			IStatusLineManager manager = this.getViewSite().getActionBars().getStatusLineManager();
//			manager.setMessage(sel.get(0).toString());
//		}
//
//		updateActionEnablement();
//	}
//
//	/**
//	 * Attempts to display this view on the workbench.
//	 */
//	public static void checkWindow() {
//		if (parent == null || parent.isDisposed()) {
//			IWorkbenchWindow w = BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
//			if (w != null) {
//				IWorkbenchPage page = w.getActivePage();
//				if (page != null) {
//					try {
//						page.showView(IBugzillaConstants.PLUGIN_ID + ".ui.offlineReportsView");
//					} catch (PartInitException pie) {
//						BugzillaPlugin.log(pie.getStatus());
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Action class - "Select All"
//	 */
//	public class SelectAllAction extends AbstractOfflineReportsAction {
//
//		public SelectAllAction() {
//			setToolTipText("Select all offline Bugzilla reports");
//			setText("Select all");
//			setImageDescriptor(BugzillaImages.SELECT_ALL);
//		}
//
//		@Override
//		public void run() {
//			checkWindow();
//			table.selectAll();
//			viewer.setSelection(viewer.getSelection(), true);
//			updateActionEnablement();
//		}
//	}
//
//	private class OfflineReportsViewLabelProvider extends LabelProvider implements ITableLabelProvider {
//
//		/**
//		 * Returns the label text for the given column of a recommendation in
//		 * the table.
//		 */
//		public String getColumnText(Object element, int columnIndex) {
//			if (element instanceof IBugzillaBug) {
//				IBugzillaBug f = (IBugzillaBug) element;
//				switch (columnIndex) {
//				case 0:
//					return f.getLabel();
//				case 1:
//					return f.getSummary();
//				case 2:
//					return f.getDescription();
//				default:
//					return "Undefined column text";
//				}
//			}
//			return ""; //$NON-NLS-1$
//		}
//
//		/*
//		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
//		 *      int)
//		 */
//		public Image getColumnImage(Object arg0, int arg1) {
//			return null;
//		}
//	}
//
//	public static void refresh() {
//		if (viewer != null && !viewer.getTable().isDisposed())
//			viewer.refresh();
//	}
//
//	private static class OfflineReportsViewContentProvider implements IStructuredContentProvider {
//
//		private List results;
//
//		/**
//		 * The constructor.
//		 */
//		public OfflineReportsViewContentProvider() {
//			// no setup needed
//		}
//
//		/**
//		 * Returns the elements to display in the viewer when its input is set
//		 * to the given element. These elements can be presented as rows in a
//		 * table, items in a list, etc. The result is not modified by the
//		 * viewer.
//		 * 
//		 * @param inputElement
//		 *            the input element
//		 * @return the array of elements to display in the viewer
//		 */
//		public Object[] getElements(Object inputElement) {
//			if (results != null) {
//				return results.toArray();
//			} else
//				return null;
//		}
//
//		/**
//		 * Notifies this content provider that a given viewer's input has been
//		 * changed.
//		 * 
//		 * @see IContentProvider#inputChanged
//		 */
//		public void inputChanged(Viewer inputViewer, Object oldInput, Object newInput) {
//			this.results = (List) newInput;
//		}
//
//		public void dispose() {
//			if (results != null)
//				results = null;
//		}
//	}
//}
