/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public abstract class AbstractColumnViewerSupport<T extends Item> {

	private static class ColumnState {
		// this represents the width of the column or the weight if it was weight data
		int width;
	}

	public static final String KEY_COLUMN_CAN_HIDE = "org.eclipse.mylyn.column.viewer.support.column.can.hide"; //$NON-NLS-1$

	// from AbstractColumnLayout
	private static final String KEY_LAYOUT_DATA = Policy.JFACE + ".LAYOUT_DATA"; //$NON-NLS-1$

	public static final String KEY_SUPPORTS_SORTING = "org.eclipse.mylyn.column.viewer.support.sorting"; //$NON-NLS-1$

	private Menu contextMenu;

	private final Control control;

	private int[] defaultOrder;

	private ColumnState[] defaults;

	private int defaultSortColumnIndex;

	private int defaultSortDirection;

	private final boolean[] defaultVisibilities;

	private final Menu headerMenu;

	private ColumnState[] lastStates;

	private final File stateFile;

	private boolean supportsSorting;

	private final ColumnViewer viewer;

	public AbstractColumnViewerSupport(ColumnViewer viewer, File stateFile) {
		this(viewer, stateFile, new boolean[0]);
	}

	public AbstractColumnViewerSupport(ColumnViewer viewer, File stateFile, boolean[] defaultVisibilities) {
		Assert.isNotNull(viewer);
		Assert.isNotNull(stateFile);
		Assert.isNotNull(defaultVisibilities);
		Object supportSort = viewer.getControl().getData(KEY_SUPPORTS_SORTING);
		if (supportSort instanceof Boolean) {
			supportsSorting = (Boolean) supportSort;
		} else {
			supportsSorting = true;
		}
		this.defaultVisibilities = defaultVisibilities;
		this.viewer = viewer;
		this.stateFile = stateFile;

		control = viewer.getControl();

		Composite parent = viewer.getControl().getParent();
		headerMenu = new Menu(parent);
	}

	abstract void addColumnSelectionListener(T column, SelectionListener selectionListener);

	private MenuItem createMenuItem(Menu parent, final T column, final int i) {
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.setText(column.getText());
		itemName.setSelection(getWidth(column) > 0);
		itemName.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int lastWidth = getWidth(column);
				if (lastWidth != 0) {
					lastStates[i].width = lastWidth;
				}
				if (lastStates[i].width == 0) {
					// if the user shrunk it to 0, use the default
					lastStates[i].width = defaults[i].width;
				}
				if (lastStates[i].width == 0) {
					// if the default and the last width was 0, then set to 150 pixels
					lastStates[i].width = 150;
				}
				if (itemName.getSelection()) {
					setWidth(column, lastStates[i].width);
				} else {
					setWidth(column, 0);
				}
			}
		});
		return itemName;
	}

	private void createRestoreDefaults(Menu parent) {

		new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem restoreDefaults = new MenuItem(parent, SWT.PUSH);
		restoreDefaults.setText(Messages.AbstractColumnViewerSupport_Restore_defaults);
		restoreDefaults.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				restoreDefaults();
			}
		});
	}

	abstract Rectangle getClientArea();

	abstract T getColumn(int index);

	abstract int getColumnIndexOf(T column);

	abstract AbstractColumnLayout getColumnLayout();

	private ColumnLayoutData getColumnLayoutData(Item column) {
		Object data = column.getData(KEY_LAYOUT_DATA);
		if (data instanceof ColumnLayoutData) {
			return (ColumnLayoutData) data;
		} else {
			return null;
		}
	}

	abstract int[] getColumnOrder();

	abstract T[] getColumns();

	abstract int getColumnWidth(T column);

	abstract int getHeaderHeight();

	abstract T getSortColumn();

	abstract int getSortDirection();

	private int getWidth(T column) {
		ColumnLayoutData data = getColumnLayoutData(column);
		AbstractColumnLayout columnLayout = getColumnLayout();
		if (data != null && columnLayout != null) {
			if (data instanceof ColumnWeightData) {
				return ((ColumnWeightData) data).weight;
			} else if (data instanceof ColumnPixelData) {
				// turn this into a weighted width
				int width = ((ColumnPixelData) data).width;
				int totalWidth = control.getSize().x;
				if (totalWidth == 0) {
					return width;
				} else {
					return (width * 100) / totalWidth;
				}
			} else {
				// we dont know
				return getColumnWidth(column);
			}
		} else {
			// if has column data, use that (pixel or weight)
			return getColumnWidth(column);
		}
	}

	private void initialize() {
		T[] columns = getColumns();
		defaults = new ColumnState[columns.length];
		defaultSortColumnIndex = -1;
		for (int i = 0; i < columns.length; i++) {
			final T column = columns[i];

			if (supportsSorting) {
				addColumnSelectionListener(column, new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int direction = getSortDirection();
						if (getSortColumn() == column && direction != SWT.NONE) {
							direction = (direction == SWT.DOWN) ? SWT.UP : SWT.NONE;
						} else {
							direction = SWT.DOWN;
						}

						setSortDirection(direction);
						if (direction == SWT.NONE) {
							setSortColumn(null);
						} else {
							setSortColumn(column);
						}
						viewer.refresh();
					}
				});
				if (column == getSortColumn()) {
					defaultSortColumnIndex = i;
				}
			}

			Object canHide = column.getData(KEY_COLUMN_CAN_HIDE);
			MenuItem item = createMenuItem(headerMenu, column, i);
			if (canHide != null && canHide instanceof Boolean && item != null) {
				item.setEnabled((Boolean) canHide);
			}
			defaults[i] = new ColumnState();
			defaults[i].width = getWidth(column);

		}

		createRestoreDefaults(headerMenu);

		defaultOrder = getColumnOrder();
		defaultSortDirection = getSortDirection();

		control.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = control.getMenu();
				if (menu != null && menu != headerMenu) {
					contextMenu = menu;
				}

				Display display = control.getDisplay();
				Point pt = display.map(null, control, new Point(event.x, event.y));
				Rectangle clientArea = getClientArea();
				boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + getHeaderHeight());

				control.setMenu(header ? headerMenu : contextMenu);
			}
		});

		control.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				save();
			}
		});
	}

	void initializeViewerSupport() {
		initialize();
		restore();

		T[] columns = getColumns();
		lastStates = new ColumnState[columns.length];
		for (int i = 0; i < columns.length; i++) {
			final T column = columns[i];
			lastStates[i] = new ColumnState();
			lastStates[i].width = getWidth(column);
		}
	}

	private void restore() {
		if (stateFile.exists()) {
			try {
				FileReader reader = new FileReader(stateFile);
				try {
					XMLMemento memento = XMLMemento.createReadRoot(reader);

					IMemento[] children = memento.getChildren("Column"); //$NON-NLS-1$
					int[] order = new int[children.length];
					for (int i = 0; i < children.length; i++) {
						T column = getColumn(i);
						setWidth(column, children[i].getInteger("width")); //$NON-NLS-1$ 
						headerMenu.getItem(i).setSelection(getWidth(column) > 0);
						order[i] = children[i].getInteger("order"); //$NON-NLS-1$
					}
					try {
						setColumnOrder(order);
					} catch (IllegalArgumentException e) {
						// ignore
					}

					IMemento child = memento.getChild("Sort"); //$NON-NLS-1$
					if (child != null) {
						int columnIndex = child.getInteger("column"); //$NON-NLS-1$
						T column = getColumn(columnIndex);
						setSortColumn(column);
						setSortDirection(child.getInteger("direction")); //$NON-NLS-1$
					}
				} catch (Exception e) {
					// ignore
				} finally {
					reader.close();
				}
			} catch (IOException e) {
				// ignore
			}

			viewer.refresh();
		} else {
			T[] columns = getColumns();
			for (int i = 0; i < columns.length; i++) {
				T column = columns[i];
				if (i < defaultVisibilities.length && !defaultVisibilities[i]) {
					setWidth(column, 0);
					headerMenu.getItem(i).setSelection(false);
				}
			}
		}
	}

	private void restoreDefaults() {
		for (int index = 0; index < defaults.length; index++) {
			T column = getColumn(index);
			if (index < defaultVisibilities.length && !defaultVisibilities[index]) {
				setWidth(column, 0);
			} else {
				setWidth(column, defaults[index].width);
			}
			// update the menu
			headerMenu.getItem(index).setSelection(getWidth(column) > 0);
		}
		setColumnOrder(defaultOrder);
		if (defaultSortColumnIndex != -1) {
			setSortColumn(getColumn(defaultSortColumnIndex));
			setSortDirection(defaultSortDirection);
		} else {
			setSortColumn(null);
		}
		viewer.refresh();
	}

	private void save() {
		XMLMemento memento = XMLMemento.createWriteRoot("Viewer"); //$NON-NLS-1$

		int[] order = getColumnOrder();
		T[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			T column = columns[i];
			IMemento child = memento.createChild("Column"); //$NON-NLS-1$
			child.putInteger("width", getWidth(column)); //$NON-NLS-1$
			child.putInteger("order", order[i]); //$NON-NLS-1$
		}

		T sortColumn = getSortColumn();
		if (sortColumn != null) {
			IMemento child = memento.createChild("Sort"); //$NON-NLS-1$
			child.putInteger("column", getColumnIndexOf(sortColumn)); //$NON-NLS-1$
			child.putInteger("direction", getSortDirection()); //$NON-NLS-1$
		}

		try {
			FileWriter writer = new FileWriter(stateFile);
			try {
				memento.save(writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	abstract void setColumnOrder(int[] order);

	abstract void setColumnResizable(T column, boolean resizable);

	abstract void setColumnWidth(T column, int width);

	abstract void setSortColumn(T column);

	abstract void setSortDirection(int direction);

	private void setWidth(T column, int width) {
		// if has column data, set that (pixel or weight)
		ColumnLayoutData data = getColumnLayoutData(column);
		AbstractColumnLayout columnLayout = getColumnLayout();
		if (data != null && columnLayout != null) {
			if (width == 0) {
				columnLayout.setColumnData(column, new ColumnPixelData(width, data.resizable));
			} else {
				columnLayout.setColumnData(column, new ColumnWeightData(width, data.resizable));
			}
			control.getParent().layout();
		} else {
			setColumnWidth(column, width);
		}
		setColumnResizable(column, width > 0);
	}

}
