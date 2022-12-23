/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import java.io.File;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.XmlMemento;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

/**
 * @author Frank Becker
 * @since 3.22
 */
public class ConfigurableColumnTableViewerSupport extends TableViewerSupport {
	private static final String xmlChildName = "TableColumnDescriptors"; //$NON-NLS-1$

	private static final String xmlFieldAlignment = "alignment"; //$NON-NLS-1$

	private static final String xmlFieldSortDirection = "sortDirection"; //$NON-NLS-1$

	private static final String xmlFieldWidth = "width"; //$NON-NLS-1$

	private static final String xmlFieldAutoSize = "autoSize"; //$NON-NLS-1$

	private static final String xmlFieldDefaultSortColumn = "defaultSortColumn"; //$NON-NLS-1$

	private final TableViewer viewer;

	private final TableColumnDescriptor[] columnDescriptors;

	public ConfigurableColumnTableViewerSupport(TableViewer viewer, TableColumnDescriptor[] columnDescriptors,
			File stateFile) {
		super(viewer, stateFile);
		this.columnDescriptors = columnDescriptors;
		this.viewer = viewer;
		createConfigureColumnsAction(getHeaderMenu());
	}

	public ConfigurableColumnTableViewerSupport(TableViewer viewer, TableColumnDescriptor[] columnDescriptors,
			File stateFile, boolean[] defaultVisibilities) {
		super(viewer, stateFile, defaultVisibilities);
		this.viewer = viewer;
		this.columnDescriptors = columnDescriptors;
		createConfigureColumnsAction(getHeaderMenu());
	}

	private void createConfigureColumnsAction(Menu parent) {

		new MenuItem(parent, SWT.SEPARATOR);

		MenuItem configureColumns = new MenuItem(parent, SWT.PUSH);
		configureColumns.setText(Messages.ConfigurableColumnTableViewerSupport_Configure_Columns);
		configureColumns.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TableColumnDescriptor[] tempColumnDescriptors = new TableColumnDescriptor[columnDescriptors.length];
				for (int i = tempColumnDescriptors.length - 1; i >= 0; --i) {
					tempColumnDescriptors[i] = new TableColumnDescriptor(columnDescriptors[i]);
				}
				final TableColumnDescriptorDialog selectionDialog = new TableColumnDescriptorDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), tempColumnDescriptors);
				selectionDialog.create();
				int resultCode = selectionDialog.open();
				if (resultCode == Window.OK) {
					for (int i = 0; i < tempColumnDescriptors.length; i++) {
						TableColumnDescriptor tempTableColumnDescriptor = tempColumnDescriptors[i];
						TableColumnDescriptor orgTableColumnDescriptor = columnDescriptors[i];
						if (!orgTableColumnDescriptor.equals(tempTableColumnDescriptor)) {
							TableColumn tableColumn = viewer.getTable().getColumn(i);
							orgTableColumnDescriptor.setAlignment(tempTableColumnDescriptor.getAlignment());
							tableColumn.setAlignment(tempTableColumnDescriptor.getAlignment());

							orgTableColumnDescriptor.setWidth(tempTableColumnDescriptor.getWidth());
							tableColumn.setWidth(tempTableColumnDescriptor.getWidth());

							orgTableColumnDescriptor.setSortDirection(tempTableColumnDescriptor.getSortDirection());
							orgTableColumnDescriptor
									.setDefaultSortColumn(tempTableColumnDescriptor.isDefaultSortColumn());
							if (tempTableColumnDescriptor.isDefaultSortColumn()) {
								viewer.getTable().setSortColumn(tableColumn);
								viewer.getTable().setSortDirection(tempTableColumnDescriptor.getSortDirection());
							}
							orgTableColumnDescriptor.setAutoSize(tempTableColumnDescriptor.isAutoSize());
						}
					}
				}
			}
		});
	}

	@Override
	protected void saveAdditionalChildInfo(XmlMemento child, TableColumn column) {
		TableColumnDescriptor desc = (TableColumnDescriptor) column
				.getData(TableColumnDescriptor.TABLE_COLUMN_DESCRIPTOR_KEY);
		XmlMemento descriptor = child.createChild(xmlChildName);
		descriptor.putInteger(xmlFieldAlignment, desc.getAlignment());
		descriptor.putInteger(xmlFieldSortDirection, desc.getSortDirection());
		descriptor.putInteger(xmlFieldWidth, desc.getWidth());
		descriptor.putBoolean(xmlFieldAutoSize, desc.isAutoSize());
		descriptor.putBoolean(xmlFieldDefaultSortColumn, desc.isDefaultSortColumn());
	}

	@Override
	protected void restoreAdditionalChildInfo(XmlMemento xmlMemento, TableColumn column) {
		TableColumnDescriptor desc = (TableColumnDescriptor) column
				.getData(TableColumnDescriptor.TABLE_COLUMN_DESCRIPTOR_KEY);
		XmlMemento mementoDesc = xmlMemento.getChild(xmlChildName);
		desc.setAlignment(mementoDesc.getInteger(xmlFieldAlignment));
		desc.setSortDirection(mementoDesc.getInteger(xmlFieldSortDirection));
		desc.setWidth(mementoDesc.getInteger(xmlFieldWidth));
		desc.setAutoSize(mementoDesc.getBoolean(xmlFieldAutoSize));
		desc.setDefaultSortColumn(mementoDesc.getBoolean(xmlFieldDefaultSortColumn));
	}

}
