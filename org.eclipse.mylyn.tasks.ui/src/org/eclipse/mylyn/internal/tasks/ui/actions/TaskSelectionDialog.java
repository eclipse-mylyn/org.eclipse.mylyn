/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.getAllCategories;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * @author Willian Mitsuda
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class TaskSelectionDialog extends SelectionStatusDialog {

	/**
	 * Implements a {@link ViewFilter} based on content typed in the filter
	 * field
	 */
	private static class TaskFilter extends ViewerFilter {

		private Pattern pattern;

		public void setFilterText(String filterText) {
			if (filterText.trim().equals("")) {
				pattern = null;
			} else {
				filterText = filterText.replace("\\", "\\\\");
				filterText = filterText.replace(".", "\\.");
				filterText = filterText.replace("*", ".*");
				filterText = filterText.replace("?", ".?");
				pattern = Pattern.compile(filterText, Pattern.CASE_INSENSITIVE);
			}
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (pattern == null) {
				return TasksUiPlugin.getTaskListManager().getTaskActivationHistory().getPreviousTasks().contains(
						element);
			}
			if (element instanceof AbstractTask) {
				AbstractTask repositoryTask = (AbstractTask) element;
				String taskString = repositoryTask.getTaskKey() + ": "
						+ repositoryTask.getSummary();
				return pattern.matcher(taskString).find();
			} else if (element instanceof AbstractTask) {
				String taskString = ((AbstractTask) element).getSummary();
				return pattern.matcher(taskString).find();
			} 
			return false;
		}
	}

	private TableViewer viewer;

	private Button openInBrowserCheck;

	private boolean openInBrowser;

	public boolean getOpenInBrowser() {
		return openInBrowser;
	}

	public void setOpenInBrowser(boolean openInBrowser) {
		this.openInBrowser = openInBrowser;
	}

	public TaskSelectionDialog(Shell parent) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private boolean showOpenInBrowserOption;

	public void setShowOpenInBrowserOption(boolean showOpenInBrowserOption) {
		this.showOpenInBrowserOption = showOpenInBrowserOption;
	}

	public boolean getShowOpenInBrowserOption() {
		return showOpenInBrowserOption;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Label message = new Label(area, SWT.NONE);
		message.setText("&Select a task to open (? = any character, * = any String):");
		final Text filterText = new Text(area, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		Label matches = new Label(area, SWT.NONE);
		matches.setText("&Matching tasks:");
		viewer = new TableViewer(area, SWT.SINGLE | SWT.BORDER);
		Control control = viewer.getControl();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		control.setLayoutData(gd);
		gd.widthHint = 400;
		gd.heightHint = 200;

		if (showOpenInBrowserOption) {
			openInBrowserCheck = new Button(area, SWT.CHECK);
			openInBrowserCheck.setText("Open with &Browser");
			openInBrowserCheck.setSelection(openInBrowser);
		}

		final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new ArrayContentProvider());

		// Compute all existing tasks or query hits (if corresponding task does
		// not exist yet...)
		Set<AbstractTaskListElement> allTasks = new HashSet<AbstractTaskListElement>();
		getAllCategories taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		allTasks.addAll(taskList.getAllTasks());
		for (AbstractRepositoryQuery query : taskList.getQueries()) {
			allTasks.addAll(query.getChildren());
			// TODO: should not need to do this
			for (AbstractTask hit : query.getHits()) {
					allTasks.add(hit);
			}
		}

		// Compute the task navigation history (in recent-to-older order)
		final List<AbstractTask> taskHistory = new ArrayList<AbstractTask>(TasksUiPlugin.getTaskListManager()
				.getTaskActivationHistory().getPreviousTasks());
		Collections.reverse(taskHistory);

		// Compute the task set who will be presented on dialog; the trick to
		// make the task history appear first on the list is to add them before
		// all other tasks; being a LinkedHashSet, it will not be duplicated
		// (this is VERY IMPORTANT)
		Set<AbstractTaskListElement> taskSet = new LinkedHashSet<AbstractTaskListElement>(taskHistory);
		taskSet.addAll(allTasks);
		viewer.setInput(taskSet);

		final TaskSelectionDialog.TaskFilter filter = new TaskSelectionDialog.TaskFilter();
		viewer.addFilter(filter);
		viewer.setComparator(new ViewerComparator() {

			private AbstractTask getCorrespondingTask(Object o) {
				if (o instanceof AbstractTask) {
					return (AbstractTask) o;
				}
				return null;
			}

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				AbstractTask t1 = getCorrespondingTask(e1);
				AbstractTask t2 = getCorrespondingTask(e2);
				boolean isInHistory1 = taskHistory.contains(t1);
				boolean isInHistory2 = taskHistory.contains(t2);

				// Being on task history takes precedence...
				if (isInHistory1 && !isInHistory2) {
					return -1;
				}
				if (!isInHistory1 && isInHistory2) {
					return 1;
				}

				// Both are in task history; who is more recent?
				if (isInHistory1 && isInHistory2) {
					return taskHistory.indexOf(t1) - taskHistory.indexOf(t2);
				}

				// Both are not in task history; sort by summary...
				return labelProvider.getText(e1).compareTo(labelProvider.getText(e2));
			}

		});
		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				if (getOkButton().getEnabled()) {
					okPressed();
				}
			}

		});

		filterText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					viewer.getControl().setFocus();
				}
			}

		});
		filterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				filter.setFilterText(filterText.getText());
				viewer.refresh(false);
				Object first = viewer.getElementAt(0);
				if (first != null) {
					viewer.setSelection(new StructuredSelection(first));
				}
			}

		});
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof ITextSelection) {
			String text = ((ITextSelection) selection).getText();
			int n = text.indexOf('\n');
			if (n > -1) {
				text.substring(0, n);
			}
			filterText.setText(text);
			filterText.setSelection(0, text.length());
		}

		return area;
	}

	@Override
	protected void computeResult() {
		setResult(((IStructuredSelection) viewer.getSelection()).toList());
	}

	@Override
	public boolean close() {
		if (openInBrowserCheck != null) {
			openInBrowser = openInBrowserCheck.getSelection();
		}
		return super.close();
	}

}
