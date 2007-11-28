/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskDetailLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListFilteredTree;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Willian Mitsuda
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class TaskSelectionDialog extends FilteredItemsSelectionDialog {

	private Button openInBrowserCheck;

	private static final String TASK_SELECTION_DIALOG_SECTION = "TaskSelectionDialogSection";

	private static final String OPEN_IN_BROWSER_SETTING = "OpenInBrowser";

	private static final String SHOW_COMPLETED_TASKS_SETTING = "ShowCompletedTasks";

	private boolean openInBrowser;

	public boolean getOpenInBrowser() {
		return openInBrowser;
	}

	public void setOpenInBrowser(boolean openInBrowser) {
		this.openInBrowser = openInBrowser;
	}

	private boolean insertInHistory;

	public void setInsertInHistory(boolean insertInHistory) {
		this.insertInHistory = insertInHistory;
	}

	public boolean getInsertInHistory() {
		return insertInHistory;
	}

	/**
	 * Integrates {@link FilteredItemsSelectionDialog} history management with Mylyn's task list activation history
	 * <p>
	 * Due to {@link SelectionHistory} use of memento-based history storage, many methods are overridden
	 */
	private class TaskSelectionHistory extends SelectionHistory {

		/**
		 * Mylyn's task activation history
		 */
		private TaskActivationHistory history = TasksUiPlugin.getTaskListManager().getTaskActivationHistory();

		@Override
		public synchronized void accessed(Object object) {
			if (insertInHistory) {
				history.addTask((AbstractTask) object);
			}
		}

		@Override
		public synchronized boolean contains(Object object) {
			return history.containsTask((AbstractTask) object);
		}

		@Override
		public synchronized boolean remove(Object object) {
			return history.removeTask((AbstractTask) object);
		}

		@Override
		public synchronized boolean isEmpty() {
			return !history.hasPrevious();
		}

		@Override
		public void load(IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		public void save(IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		protected Object restoreItemFromMemento(IMemento memento) {
			// do nothing because tasklist history handles this
			return null;
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		public synchronized Object[] getHistoryItems() {
			return history.getPreviousTasks().toArray();
		}
	}

	private TaskElementLabelProvider labelProvider;

	public TaskSelectionDialog(Shell parent) {
		super(parent);
		setSelectionHistory(new TaskSelectionHistory());

		labelProvider = new TaskElementLabelProvider(false);
		
		setListLabelProvider(labelProvider);
		
//		setListLabelProvider(new DecoratingLabelProvider(labelProvider, PlatformUI.getWorkbench()
//				.getDecoratorManager()
//				.getLabelDecorator()));
		setDetailsLabelProvider(new TaskDetailLabelProvider());
		setSeparatorLabel(TaskListView.LABEL_VIEW + " matches");

		// If there is a text selection, use it as the initial filter
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof ITextSelection) {
			// Get only get first line
			String text = ((ITextSelection) selection).getText();
			int n = text.indexOf('\n');
			if (n > -1) {
				text.substring(0, n);
			}
			setInitialPattern(text);
		}
	}

	private boolean showExtendedOpeningOptions;

	public void setShowExtendedOpeningOptions(boolean showExtendedOpeningOptions) {
		this.showExtendedOpeningOptions = showExtendedOpeningOptions;
	}

	public boolean getShowExtendedOpeningOptions() {
		return showExtendedOpeningOptions;
	}

	private ShowCompletedTasksAction showCompletedTasksAction = new ShowCompletedTasksAction();

	@Override
	protected void fillViewMenu(IMenuManager menuManager) {
		super.fillViewMenu(menuManager);
		menuManager.add(showCompletedTasksAction);
	}

	private boolean showCompletedTasks;

	private class ShowCompletedTasksAction extends Action {

		public ShowCompletedTasksAction() {
			super("Show &Completed Tasks", IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			showCompletedTasks = isChecked();
			applyFilter();
		}

	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		if (!showExtendedOpeningOptions) {
			return null;
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 5).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().create());

		openInBrowserCheck = new Button(composite, SWT.CHECK);
		openInBrowserCheck.setText("Open with &Browser");
		openInBrowserCheck.setSelection(openInBrowser);

		ImageHyperlink openHyperlink = new ImageHyperlink(composite, SWT.NONE);
		openHyperlink.setText(TaskListFilteredTree.LABEL_SEARCH);
		openHyperlink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		openHyperlink.setUnderlined(true);
		openHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				getShell().close();
				new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), TaskSearchPage.ID).open();
			}

		});

		return composite;
	}

	@Override
	public boolean close() {
		if (openInBrowserCheck != null) {
			openInBrowser = openInBrowserCheck.getSelection();
		}
		return super.close();
	}

	private class TasksFilter extends ItemsFilter {

		private boolean showCompletedTasks;

		public TasksFilter(boolean showCompletedTasks) {
			super(new SearchPattern());
			// Little hack to force always a match inside any part of task text
			patternMatcher.setPattern("*" + patternMatcher.getPattern());
			this.showCompletedTasks = showCompletedTasks;
		}

		@Override
		public boolean isSubFilter(ItemsFilter filter) {
			if (!super.isSubFilter(filter)) {
				return false;
			}
			if (filter instanceof TasksFilter) {
				return showCompletedTasks == ((TasksFilter) filter).showCompletedTasks;
			}
			return true;
		}

		@Override
		public boolean equalsFilter(ItemsFilter filter) {
			if (!super.equalsFilter(filter)) {
				return false;
			}
			if (filter instanceof TasksFilter) {
				return showCompletedTasks == ((TasksFilter) filter).showCompletedTasks;
			}
			return true;
		}

		@Override
		public boolean isConsistentItem(Object item) {
			return item instanceof AbstractTask;
		}

		@Override
		public boolean matchItem(Object item) {
			if (!(item instanceof AbstractTask)) {
				return false;
			}
			if (!showCompletedTasks && ((AbstractTask) item).isCompleted()) {
				return false;
			}
			return matches(labelProvider.getText(item));
		}
	}

	@Override
	protected ItemsFilter createFilter() {
		return new TasksFilter(showCompletedTasks);
	}

	/**
	 * Caches all tasks; populated at first access
	 */
	private Set<AbstractTask> allTasks;

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {
		progressMonitor.beginTask("Search for tasks", 100);

		if (allTasks == null) {
			allTasks = new HashSet<AbstractTask>();
			TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
			allTasks.addAll(taskList.getAllTasks());
		}
		progressMonitor.worked(10);

		SubProgressMonitor subMonitor = new SubProgressMonitor(progressMonitor, 90);
		subMonitor.beginTask("Scanning tasks", allTasks.size());
		for (AbstractTask task : allTasks) {
			contentProvider.add(task, itemsFilter);
			subMonitor.worked(1);
		}
		subMonitor.done();

		progressMonitor.done();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(TASK_SELECTION_DIALOG_SECTION);
		if (section == null) {
			section = settings.addNewSection(TASK_SELECTION_DIALOG_SECTION);
			section.put(OPEN_IN_BROWSER_SETTING, false);
			section.put(SHOW_COMPLETED_TASKS_SETTING, true);
		}
		return section;
	}

	@Override
	protected void restoreDialog(IDialogSettings settings) {
		openInBrowser = settings.getBoolean(OPEN_IN_BROWSER_SETTING);
		showCompletedTasks = settings.getBoolean(SHOW_COMPLETED_TASKS_SETTING);
		showCompletedTasksAction.setChecked(showCompletedTasks);
		super.restoreDialog(settings);
	}

	@Override
	protected void storeDialog(IDialogSettings settings) {
		settings.put(OPEN_IN_BROWSER_SETTING, openInBrowser);
		settings.put(SHOW_COMPLETED_TASKS_SETTING, showCompletedTasks);
		super.storeDialog(settings);
	}

	@Override
	public String getElementName(Object item) {
		return labelProvider.getText(item);
	}

	/**
	 * Sort tasks by summary
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {

			public int compare(Object o1, Object o2) {
				return labelProvider.getText(o1).compareTo(labelProvider.getText(o2));
			}

		};
	}

	@Override
	protected IStatus validateItem(Object item) {
		if (item instanceof AbstractTask) {
			return Status.OK_STATUS;
		}
		return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Selected item is not a task");
	}

}
