/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI view column sorter.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the view sorter
 *   Francois Chouinard - Refined the sorting of 1) dates and 2) flags 
 *   Marc-Andre Laperle - Add Status to dashboard
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class ReviewTableSorter extends ViewerSorter {

	// ------------------------------------------------------------------------
	// Attributes
	// ------------------------------------------------------------------------

	// The target column
	private int columnIndex = 0;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	public ReviewTableSorter(int columnIndex) {
		super();
		this.columnIndex = columnIndex;
	}

	// ------------------------------------------------------------------------
	// ViewerSorter
	// ------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("restriction")
	public int compare(Viewer viewer, Object item1, Object item2) {

		int sortDirection = SWT.NONE;
		if (viewer instanceof TableViewer) {
			sortDirection = ((TableViewer) viewer).getTable().getSortDirection();
		} else if (viewer instanceof TreeViewer) {
			sortDirection = ((TreeViewer) viewer).getTree().getSortDirection();
		}

		// The comparison result (< 0, == 0, > 0)
		int result = 0;

		// We are dealing with GerritTask:s but just in case...
		if (viewer instanceof TableViewer && item1 instanceof GerritTask && item2 instanceof GerritTask) {

			GerritTask task1 = (GerritTask) item1;
			GerritTask task2 = (GerritTask) item2;

			String val1 = null;
			String val2 = null;

			switch (columnIndex) {
			case 0: // Star
				val1 = task1.getAttribute(GerritTask.IS_STARRED);
				val2 = task2.getAttribute(GerritTask.IS_STARRED);
				if (val1 != null && val2 != null) {
					result = val1.compareTo(val2);
				}
				break;
			case 7: // Updated
				val1 = task1.getAttribute(GerritTask.DATE_MODIFICATION);
				val2 = task2.getAttribute(GerritTask.DATE_MODIFICATION);
				if (val1 != null && val2 != null) {
					result = val1.compareTo(val2);
				}
				break;
			case 8: // Code Review
				val1 = task1.getAttribute(GerritTask.REVIEW_STATE);
				val2 = task2.getAttribute(GerritTask.REVIEW_STATE);
				if (val1 != null && val2 != null) {
					Integer v1 = new Integer(val1);
					Integer v2 = new Integer(val2);
					result = v2 - v1;
				}
				break;
			case 9: // Verify
				val1 = task1.getAttribute(GerritTask.VERIFY_STATE);
				val2 = task2.getAttribute(GerritTask.VERIFY_STATE);
				if (val1 != null && val2 != null) {
					Integer v1 = new Integer(val1);
					Integer v2 = new Integer(val2);
					result = v2 - v1;
				}
				break;
			case 10: // IPLog Clean
			default:
				result = defaultCompare(viewer, item1, item2);
				break;
			}
		} else {
			result = defaultCompare(viewer, item1, item2);
		}

		if (sortDirection != SWT.UP) {
			result = -result;
		}

		return result;
	}

	private int defaultCompare(Viewer aViewer, Object aE1, Object aE2) {

		if (aViewer instanceof TableViewer) {

			// We are in a table
			TableViewer tv = (TableViewer) aViewer;
			tv.getTable().setSortColumn(tv.getTable().getColumn(columnIndex));

			// Lookup aE1 and aE2
			int idx1 = -1, idx2 = -1;
			for (int i = 0; i < tv.getTable().getItemCount(); i++) {
				Object obj = tv.getElementAt(i);
				if (obj.equals(aE1)) {
					idx1 = i;
				} else if (obj.equals(aE2)) {
					idx2 = i;
				}
				if (idx1 != -1 && idx2 != -1) {
					break;
				}
			}

			// Compare the respective fields
			int order = 0;

			if (idx1 > -1 && idx2 > -1) {
				String str1 = tv.getTable().getItems()[idx1].getText(this.columnIndex);
				String str2 = tv.getTable().getItems()[idx2].getText(this.columnIndex);
				order = str1.compareTo(str2);
			}
			return order;
		}

		else if (aViewer instanceof TreeViewer) {

			TreeViewer tv = (TreeViewer) aViewer;
			tv.getTree().setSortColumn(tv.getTree().getColumn(columnIndex));
			int idx1 = -1, idx2 = -1;

			Object[] listObj = tv.getTree().getItems();

			for (int i = 0; i < listObj.length; i++) {
				Object obj = null;
				if (listObj[i] instanceof TreeItem) {

					obj = ((TreeItem) listObj[i]).getData();
					((TreeItem) listObj[i]).setExpanded(true);

				} else {
				}

				if (obj != null) {
					if (obj.equals(aE1)) {
						idx1 = i;
					} else if (obj.equals(aE2)) {
						idx2 = i;
					}
					if (idx1 > 0 && idx2 > 0) {
						break;
					}
				}
			}

			int order = 0;
			if (idx1 > -1 && idx2 > -1) {
				String str1 = tv.getTree().getItems()[idx1].getText(this.columnIndex);
				String str2 = tv.getTree().getItems()[idx2].getText(this.columnIndex);
				order = str1.compareTo(str2);
			}
			return order;
		}
		return 0;
	}

	// ------------------------------------------------------------------------
	// Static methods 
	// ------------------------------------------------------------------------

	/**
	 * Bind a sorter to each table column
	 * 
	 * @param aTableViewer
	 */
	public static void bind(final TableViewer aTableViewer) {
		for (int i = 0; i < aTableViewer.getTable().getColumnCount(); i++) {
			final int columnNum = i;
			TableColumn column = aTableViewer.getTable().getColumn(i);
			column.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					ReviewTableSorter sorter = new ReviewTableSorter(columnNum);
					Table table = aTableViewer.getTable();
					table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
					aTableViewer.setComparator(sorter);
				}
			});
		}
	}

}
