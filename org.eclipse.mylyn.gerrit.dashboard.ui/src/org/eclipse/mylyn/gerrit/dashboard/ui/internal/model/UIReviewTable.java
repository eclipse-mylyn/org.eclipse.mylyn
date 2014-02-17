/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation of the review table view.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the table view
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;



import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.table.AdjustMyStarredHandler;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 * 
 */
public class UIReviewTable {

	private final int TABLE_STYLE = (SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------
	private TableViewer fViewer;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public UIReviewTable() {

	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public TableViewer createTableViewerSection(Composite aParent) {
		// Create a form to maintain the search data
		Composite viewerForm = UIUtils.createsGeneralComposite(aParent,
				SWT.BORDER | SWT.SHADOW_ETCHED_IN);

		GridData gribDataViewer = new GridData(GridData.FILL_BOTH);
		viewerForm.setLayoutData(gribDataViewer);

		// Add a listener when the view is resized
		GridLayout layout = new GridLayout();
		layout.numColumns = ReviewTableDefinition.values().length;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		viewerForm.setLayout(layout);

		// Create the table viewer to maintain the list of reviews
		fViewer = new TableViewer(viewerForm, TABLE_STYLE);
		fViewer = buildAndLayoutTable(fViewer);

		// Set the content provider and the Label provider and the sorter
		fViewer.setContentProvider(new ReviewTableContentProvider());

		// Set the viewer for the provider
		ReviewTableLabelProvider tableProvider = new ReviewTableLabelProvider();
		fViewer.setLabelProvider(tableProvider);
		ReviewTableSorter.bind(fViewer);

		// Create the help context id for the viewer's control
		// PlatformUI
		// .getWorkbench()
		// .getHelpSystem()
		// .setHelp(fViewer.getControl(),
		// "org.eclipse.mylyn.gerrit.dashboard.ui.viewer");

		//
		fViewer.getTable().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				GerritUi.Ftracer.traceInfo("Table selection: "
						+ e.toString());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// Add a Key event and mouse down listener
		fViewer.getTable().addListener(SWT.MouseDown, mouseButtonListener);
		// fViewer.getTable().addKeyListener(keyEventListener);

		return fViewer;

	}

	/**
	 * Create each column for the List of Reviews
	 * 
	 * @param aParent
	 * @param aViewer
	 */
	private TableViewer buildAndLayoutTable(final TableViewer aViewer) {
		final Table table = aViewer.getTable();
		
		//Get the review table definition
		ReviewTableDefinition[] tableInfo = ReviewTableDefinition.values();
		int size = tableInfo.length;
		GerritUi.Ftracer.traceInfo("Table	Name	Width	Resize Moveable");
		for  (int index = 0; index < size; index++) {
			GerritUi.Ftracer.traceInfo("index [ " + index + 
					" ] "  + tableInfo[index].getName() + 
					"\t: " + tableInfo[index].getWidth() +
					"\t: " + tableInfo[index].getResize() +
					"\t: " + tableInfo[index].getMoveable() );
			TableViewerColumn col = createTableViewerColumn(tableInfo[index]);
			
			GridData gribData = new GridData(GridData.FILL_BOTH);
			gribData.minimumWidth = tableInfo[index].getWidth();
			col.getColumn().getParent().setLayoutData(gribData);
		}

		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				table.setRedraw(false);
				Point tableSize = table.getSize();
				Point parentSize = table.getParent().getSize();
				//Adjust the width  according to its parent
				int minimumTableWidth = ReviewTableDefinition.getMinimumWidth();
				int mimimumSubjectWidth = ReviewTableDefinition.SUBJECT.getWidth();
				int minProjectWidth = ReviewTableDefinition.PROJECT.getWidth();
				int proAndSubjetWidth = mimimumSubjectWidth + minProjectWidth;
				
				//Adjust the subject and project column to take the remaining space
				int scrollWidth = table.getVerticalBar().getSize().x;
				//If not visible, take the extra space
				if (!table.getVerticalBar().isVisible()) {
					scrollWidth = 0;
				}

				int computeExtraWidth = parentSize.x - 10 - ( minimumTableWidth ) - scrollWidth ;
				int newSubjectWidth = mimimumSubjectWidth;
				int newProjectWidth = minProjectWidth;
				//If extra space, redistribute it to specific column
				if (computeExtraWidth > 0) {
					//Assign some to subject and some to Project
					int value = 2*computeExtraWidth  /3;
					newSubjectWidth = mimimumSubjectWidth + value; // 2/3 of the extra
					newProjectWidth = minProjectWidth + computeExtraWidth- value;       // 1/3 of the extra
				}
				//Subject column
				table.getColumn(2).setWidth(newSubjectWidth);
				//Project column
				table.getColumn(4).setWidth(newProjectWidth);

				table.setSize(parentSize.x - 10, tableSize.y);
				table.setRedraw(true);
				
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
			}
		});

		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return aViewer;
	}
	

	/**
	 * Create each column in the review table list
	 * 
	 * @param ReviewTableDefinition
	 * @return TableViewerColumn
	 */
	private TableViewerColumn createTableViewerColumn(
			ReviewTableDefinition aTableInfo) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(fViewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(aTableInfo.getName());
		column.setWidth(aTableInfo.getWidth());
		column.setAlignment(aTableInfo.getAlignment());
		column.setResizable(aTableInfo.getResize());
		column.setMoveable(aTableInfo.getMoveable());
		return viewerColumn;

	}

	private Listener mouseButtonListener = new Listener() {
		public void handleEvent(Event aEvent) {
			GerritUi.Ftracer.traceInfo("mouseButtonListener() for "
					+ aEvent.button);
			switch (aEvent.type) {
			case SWT.MouseDown:
				// Left Click
				if (aEvent.button == 1) {

					// Process the Item table handling
					processItemSelection();

					// singleClickFocus(tableIndex);

				}
				// For now, use button 2 to modify the starred value column 1
				if (aEvent.button == 2) {
					// Select the new item in the table
					Table table = fViewer.getTable();
					table.deselectAll();
					Point p = new Point(aEvent.x, aEvent.y);
					TableItem tbi = fViewer.getTable().getItem(p);
					if (tbi != null) {
						table.setSelection(tbi);						
					}

					// Execute the command to adjust the column: ID with the
					// starred information
					AdjustMyStarredHandler handler = new AdjustMyStarredHandler();
					try {
						handler.execute(new ExecutionEvent());
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// Right Click
				if (aEvent.button == 3) {
					// Process the Item table handling
					// processItemSelection();
				}
				break;
			default:
				break;
			}
		}

	};

	/**
	 * Key Listener to handle the Mouse down event on the ITEM and ANOMALY table
	 */
	private KeyListener keyEventListener = new KeyListener() {

		public void keyReleased(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			Table table = fViewer.getTable();
			int[] selecteditems = table.getSelectionIndices();
			int val = selecteditems[0];
			if (e.keyCode == SWT.ARROW_UP) {
				// So we need to reduce the selected item
				GerritUi.Ftracer
						.traceInfo("keyEventListener() for ARROW_UP "
								+ e.keyCode);
				if (val > 0) {
					val--;
					table.deselect(selecteditems[0]);
				}
			}

			if (e.keyCode == SWT.ARROW_DOWN) {
				// So we need to increase the selected item
				GerritUi.Ftracer
						.traceInfo("keyEventListener() for ARROW_DOWN "
								+ e.keyCode);
				if (val < table.getItemCount() - 1) {
					val++;
					table.deselect(selecteditems[0]);
				}
			}

			// Set the new selection
			table.select(val);

			// // Process the Item table handling
			// processItemSelection();
			//
			// // Open the file in the editor
			// singleClickFocus(tableIndex);

		}
	};

	/**
	 * Process the selected data from the item table
	 */
	private void processItemSelection() {
		ISelection tableSelection = fViewer.getSelection();
		GerritUi.Ftracer
				.traceInfo("Selected : " + tableSelection.getClass());
		if (tableSelection.isEmpty()) {
			GerritUi.Ftracer.traceInfo("Selected table selection is EMPTY ");

		} else {
			if (tableSelection instanceof IStructuredSelection ) {
				Object obj = ((IStructuredSelection) tableSelection).getFirstElement();
				GerritUi.Ftracer.traceInfo("Selected table selection class: " + obj.getClass() ); 
				if (obj instanceof  GerritTask) {
					GerritTask item = (GerritTask) obj;
					GerritUi.Ftracer.traceInfo("Selected table OBJECT selection ID: "  + item.getAttribute(GerritTask.SHORT_CHANGE_ID) + 
							"\t subject: " + item.getAttribute(GerritTask.SUBJECT)); 				
				}
			}
		}
		// if (tableSelection.length == 1) {
		// ReviewTableListItem selected = tableSelection[0];
		// }
		// if (GerritTableView.getActiveView() != null) {
		// ArrayList<IReviewEntityItem> itemlist = getSelectedItems();
		// // Number of item to set the check flag
		// if (itemlist.size() > 0) {
		// // Only display the first one
		// IReviewEntityItem item = itemlist.get(0);
		// ReviewItemNavigatorViewPart.getInstance().displayInfo(item);
		// } else {
		// ReviewItemNavigatorAction.updateItemNavigatorIcon();
		// }
		// }
		// // Set the review view toolbar
		// ReviewItemNavigatorAction.updateItemNavigatorIcon();
		//
		// ReviewTableCommonAction.setToolbarButtonsSensitivity();
	}

}
