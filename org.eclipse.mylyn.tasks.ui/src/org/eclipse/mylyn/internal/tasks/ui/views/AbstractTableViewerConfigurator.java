/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.ColumnState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class AbstractTableViewerConfigurator implements ISelectionProvider {
	protected TableViewer tableViewer;

	protected Table table;

	private final File stateFile;

	protected ArrayList<ColumnState> columnInfos;

	protected int[] orderArray;

	public AbstractTableViewerConfigurator(File stateFile) {
		super();
		this.stateFile = stateFile;
	}

	abstract protected void setDefaultColumnInfos();

	abstract protected void setupTableViewer();

	private void readStateFile() {
		if (stateFile.exists()) {
			try {
				FileReader reader = new FileReader(stateFile);
				try {
					XMLMemento memento = XMLMemento.createReadRoot(reader);
					IMemento child = memento.getChild("Columns"); //$NON-NLS-1$
					int size = child.getInteger("count"); //$NON-NLS-1$
					IMemento[] children = memento.getChildren("ColumnState"); //$NON-NLS-1$
					for (int i = 0; i < size; i++) {
						columnInfos.add(ColumnState.createState(children[i]));
					}
					String orderString = child.getString("order"); //$NON-NLS-1$
					String[] orderStringArray = orderString.split(","); //$NON-NLS-1$
					orderArray = new int[orderStringArray.length];
					for (int i = 0; i < orderStringArray.length; i++) {
						orderArray[i] = Integer.parseInt(orderStringArray[i]);
					}
				} catch (WorkbenchException e) {
					StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
							"The TableViewerState cache could not be read", e)); //$NON-NLS-1$
				} finally {
					reader.close();
				}
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						"The TableViewerState cache could not be read", e)); //$NON-NLS-1$
			}
		}

	}

	protected void writeStateFile() {

		if (stateFile == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot("TableViewerState"); //$NON-NLS-1$
		IMemento child = memento.createChild("Columns"); //$NON-NLS-1$
		child.putInteger("count", columnInfos.size()); //$NON-NLS-1$
		for (ColumnState col : columnInfos) {
			col.saveState(memento);
		}

		int[] colOrder = table.getColumnOrder();
		String orderString = ""; //$NON-NLS-1$
		for (int colPos : colOrder) {
			if (orderString.length() > 0) {
				orderString += ","; //$NON-NLS-1$
			}
			orderString += colPos;
		}
		child.putString("order", orderString); //$NON-NLS-1$

		try {
			FileWriter writer = new FileWriter(stateFile);
			try {
				memento.save(writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"The TaskEditorAttachment cache could not be written", e)); //$NON-NLS-1$
		}
	}

	protected void adjustColumInfos() {

	}

	public void create(FormToolkit toolkit, Composite parent, int initialColumnCount) {
		table = createTable(parent, toolkit);
		columnInfos = new ArrayList<ColumnState>(initialColumnCount);
		readStateFile();
		if (columnInfos.size() == 0) {
			setDefaultColumnInfos();
		}
		adjustColumInfos();
		for (int index = 0; index < columnInfos.size(); index++) {
			ColumnState colState = columnInfos.get(index);
			final TableColumn column = new TableColumn(table, colState.getAlignment(), index);
			column.setText(colState.getName());
			column.setWidth(colState.getWidths());
			column.setMoveable(true);
			column.addControlListener(createColumnControlListener(table, column, index));
		}

		tableViewer = new TableViewer(table);
		table.setColumnOrder(orderArray);
		setupTableViewer();
	}

	protected ControlListener createColumnControlListener(Table table, final TableColumn column, final int index) {
		return new ControlListener() {

			public void controlResized(ControlEvent e) {
				if (!TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(ITasksUiPreferenceConstants.ATTACHMENT_COLUMN_TO_STD)) {
					columnInfos.get(index).setWidths(column.getWidth());
					writeStateFile();
				}
			}

			public void controlMoved(ControlEvent e) {
				writeStateFile();
			}
		};

	}

	protected Table createTable(Composite parent, FormToolkit toolkit) {
		Table table = toolkit.createTable(parent, SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayout(new GridLayout());
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, false)
				.hint(500, SWT.DEFAULT)
				.applyTo(table);
		table.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		return table;
	}

	public Table getTable() {
		return table;
	}

	public void resetColumnInfosToDefault() {
		columnInfos.clear();
		setDefaultColumnInfos();
		if (!table.isDisposed()) {
			for (int index = 0; index < columnInfos.size(); index++) {
				TableColumn col = table.getColumn(index);
				ColumnState colState = columnInfos.get(index);
				col.setAlignment(colState.getAlignment());
				col.setWidth(colState.getWidths());
				col.setText(colState.getName());
			}
			table.setColumnOrder(orderArray);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		tableViewer.addSelectionChangedListener(listener);
	}

	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		tableViewer.removeSelectionChangedListener(listener);
	}

	public void setSelection(ISelection selection) {
		tableViewer.setSelection(selection);
	}

}
