/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListTableSorter.SortByIndex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.SelectionDialog;

public class TaskListSortDialog extends SelectionDialog {
	private Combo[] priorityCombos;

	private Button[] ascendingButtons;

	private Button[] descendingButtons;

	private final String[] propertyText;

	private boolean dirty = false;

	private final TaskListView taskListView;

	public TaskListSortDialog(IShellProvider parentShell, TaskListView taskListView) {
		super(parentShell.getShell());
		propertyText = new String[3];
		propertyText[0] = "Priority";
		propertyText[1] = "Summary";
		propertyText[2] = "Date Created";
		this.taskListView = taskListView;
		setTitle(TaskListView.LABEL_VIEW + " Sorting");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(composite);

		Composite prioritiesArea = new Composite(composite, SWT.NULL);
		prioritiesArea.setLayout(new GridLayout(3, false));

		Label sortByLabel = new Label(prioritiesArea, SWT.NULL);
		sortByLabel.setText("Sort order:");
		GridData data = new GridData();
		data.horizontalSpan = 3;
		sortByLabel.setLayoutData(data);

		ascendingButtons = new Button[2];
		descendingButtons = new Button[2];
		priorityCombos = new Combo[2];

		for (int i = 0; i < 2; i++) {
			final int index = i;
			Label numberLabel = new Label(prioritiesArea, SWT.NULL);
			numberLabel.setText("" + (i + 1) + ".");
			priorityCombos[i] = new Combo(prioritiesArea, SWT.READ_ONLY);
			priorityCombos[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Composite directionGroup = new Composite(prioritiesArea, SWT.NONE);
			directionGroup.setLayout(new GridLayout(2, false));
			ascendingButtons[i] = new Button(directionGroup, SWT.RADIO);
			ascendingButtons[i].setText("Ascending");
			ascendingButtons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					markDirty();
				}
			});
			descendingButtons[i] = new Button(directionGroup, SWT.RADIO);
			descendingButtons[i].setText("Descending");
			descendingButtons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					markDirty();
				}
			});
			if (i < priorityCombos.length - 1) {
				priorityCombos[i].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int oldSelectionDirection = 1;
						if (descendingButtons[index].getSelection()) {
							oldSelectionDirection = -1;
						}
						ArrayList<String> oldSelectionList = new ArrayList<String>(
								Arrays.asList(priorityCombos[index].getItems()));
						oldSelectionList.removeAll(Arrays.asList(priorityCombos[index + 1].getItems()));
						if (oldSelectionList.size() != 1) {
							return;
						}
						String oldSelection = oldSelectionList.get(0);
						String newSelection = priorityCombos[index].getItem(priorityCombos[index].getSelectionIndex());
						if (oldSelection.equals(newSelection)) {
							return;
						}
						for (int j = index + 1; j < priorityCombos.length; j++) {
							int newSelectionIndex = priorityCombos[j].indexOf(newSelection);
							//this combo's current selection is equal to newSelection
							if (priorityCombos[j].getSelectionIndex() == newSelectionIndex) {
								priorityCombos[j].remove(newSelection);
								int insertionPoint = -1
										- Arrays.binarySearch(priorityCombos[j].getItems(), oldSelection,
												columnComparator);
								if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
									priorityCombos[j].add(oldSelection, insertionPoint);
								} else {
									priorityCombos[j].add(oldSelection);
								}
								priorityCombos[j].select(priorityCombos[j].indexOf(oldSelection));
								ascendingButtons[index].setSelection(ascendingButtons[j].getSelection());
								descendingButtons[index].setSelection(descendingButtons[j].getSelection());
								ascendingButtons[j].setSelection(oldSelectionDirection == 1);
								descendingButtons[j].setSelection(oldSelectionDirection == -1);
							}
							//this combo contains newSelection
							else if (newSelectionIndex >= 0) {
								String currentText = priorityCombos[j].getText();
								priorityCombos[j].remove(newSelection);
								int insertionPoint = -1
										- Arrays.binarySearch(priorityCombos[j].getItems(), oldSelection,
												columnComparator);
								if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
									priorityCombos[j].add(oldSelection, insertionPoint);
									priorityCombos[j].select(priorityCombos[j].indexOf(currentText));
								} else {
									priorityCombos[j].add(oldSelection);
								}
							}
						}
						markDirty();
					}
				});
			} else {
				priorityCombos[i].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						markDirty();
					}
				});
			}

		}
		int a[] = new int[2];
		int b[] = new int[2];
		switch (taskListView.getSorter().getSortByIndex()) {
		case PRIORITY:
			a[0] = 0;
			break;
		case SUMMARY:
			a[0] = 1;
			break;
		case DATE_CREATED:
			a[0] = 2;
			break;
		}

		switch (taskListView.getSorter().getSortByIndex2()) {
		case PRIORITY:
			a[1] = 0;
			break;
		case SUMMARY:
			a[1] = 1;
			break;
		case DATE_CREATED:
			a[1] = 2;
			break;
		}
		b[0] = taskListView.getSorter().getSortDirection();
		b[1] = taskListView.getSorter().getSortDirection2();
		updateUI(a, b);
		return composite;
	}

	@Override
	protected void okPressed() {
		if (isDirty()) {
			taskListView.getSorter().setSortByIndex(
					SortByIndex.valueOf(priorityCombos[0].getItem(priorityCombos[0].getSelectionIndex()).replace(' ',
							'_').toUpperCase()));
			taskListView.getSorter().setSortByIndex2(
					SortByIndex.valueOf(priorityCombos[1].getItem(priorityCombos[1].getSelectionIndex()).replace(' ',
							'_').toUpperCase()));
			if (descendingButtons[0].getSelection()) {
				taskListView.getSorter().setSortDirection(-1);
			} else {
				taskListView.getSorter().setSortDirection(1);
			}
			if (descendingButtons[1].getSelection()) {
				taskListView.getSorter().setSortDirection2(-1);
			} else {
				taskListView.getSorter().setSortDirection2(1);
			}

		}
		super.okPressed();
	}

	/**
	 * @return boolean
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty flag to true.
	 */
	public void markDirty() {
		dirty = true;
	}

	private final Comparator<String> columnComparator = new Comparator<String>() {
		public int compare(String arg0, String arg1) {
			int index0 = -1;
			int index1 = -1;
			for (int i = 0; i < propertyText.length; i++) {
				if (propertyText[i].equals(arg0)) {
					index0 = i;
				}
				if (propertyText[i].equals(arg1)) {
					index1 = i;
				}
			}
			return index0 - index1;
		}
	};

	private void updateUI(int[] priorities, int[] directions) {
		ArrayList<String> availablePriorities = new ArrayList<String>(Arrays.asList(propertyText));

		for (int i = 0; i < priorityCombos.length; i++) {
			priorityCombos[i].removeAll();
			for (int j = 0; j < availablePriorities.size(); j++) {
				priorityCombos[i].add(availablePriorities.get(j));
			}
			priorityCombos[i].select(priorityCombos[i].indexOf(propertyText[priorities[i]]));
			availablePriorities.remove(propertyText[priorities[i]]);

			ascendingButtons[i].setSelection(directions[i] == 1);
			descendingButtons[i].setSelection(directions[i] == -1);
		}
	}

}
