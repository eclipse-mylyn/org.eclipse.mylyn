package org.eclipse.mylar.tasklist.repositories.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.ITaskRepositoryListener;
import org.eclipse.mylar.tasklist.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesView extends ViewPart {
	
	private static final String ID = "org.eclipse.mylar.tasklist.repositories";

	private TableViewer viewer;

	private Action addRepositoryAction = new AddTaskRepositoryAction();

	private Action deleteRepositoryAction = new DeleteTaskRepositoryAction(this);

	private Action repositoryPropertiesAction = new TaskRepositoryPropertiesAction(this);
	
	private final ITaskRepositoryListener REPOSITORY_LISTENER = new ITaskRepositoryListener() {

		public void repositorySetUpdated() {
			TaskRepositoriesView.this.getViewer().refresh();
		}
	};
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return MylarTaskListPlugin.getRepositoryManager().getAllRepositories().toArray();
		}
	}

	public TaskRepositoriesView() {
		MylarTaskListPlugin.getRepositoryManager().addListener(REPOSITORY_LISTENER);
	}

	public static TaskRepositoriesView getFromActivePerspective() {
		IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null)
			return null;
		IViewPart view= activePage.findView(ID);
		if (view instanceof TaskRepositoriesView)
			return (TaskRepositoriesView)view;
		return null;	
	}
	
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(getViewSite());
		
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskRepositoriesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(addRepositoryAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(addRepositoryAction);
		manager.add(deleteRepositoryAction);
		manager.add(new Separator());
		manager.add(repositoryPropertiesAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addRepositoryAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TableViewer getViewer() {
		return viewer;
	}
}