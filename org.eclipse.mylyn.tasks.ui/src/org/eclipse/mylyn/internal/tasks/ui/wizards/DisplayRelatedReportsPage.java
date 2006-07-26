/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author Jeff Pound
 */
public class DisplayRelatedReportsPage extends WizardPage {

	private static final String PAGE_DESCRIPTION = "Select duplicate report candidates to open and comment on them.  Otherwise press finish to create new one.";

	static final String PAGE_NAME = "DisplayRelatedReportsPage";

	private static final String PAGE_TITLE = "Related Reports";

	private static final int LINES_PER_ITEM = 3;

	private List<AbstractRepositoryTask> relatedTasks;

	private String[] columnHeaders = { "Related Reports" };

	private Tree duplicatesTree;

	private int[] columnWidths = { 550 };

	protected DisplayRelatedReportsPage() {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		// Description doesn't show up without an image present TODO: proper
		// image.
		setImageDescriptor(TasksUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui",
				"icons/wizban/bug-wizard.gif"));
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

		duplicatesTree = new Tree(composite, SWT.MULTI | SWT.CHECK | SWT.FULL_SELECTION);
		duplicatesTree.setLayoutData(new GridData(GridData.FILL_BOTH));

		RowLayout gl = new RowLayout();
		gl.spacing = 30;
		duplicatesTree.setLayout(gl);

		duplicatesTree.setHeaderVisible(true);
		duplicatesTree.setLinesVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TreeColumn column = new TreeColumn(duplicatesTree, SWT.NONE);
			column.setText(columnHeaders[i]);
		}

		/*
		 * Adapted from snippet 227
		 * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet227.java?rev=HEAD&content-type=text/vnd.viewcvs-markup
		 */
		Listener paintListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MeasureItem: {
					String text = ((TreeItem) event.item).getText();
					Point size = event.gc.textExtent(text);
					event.width = size.x;
					event.height = Math.max(event.height, size.y);
					break;
				}
				case SWT.PaintItem: {
					String text = ((TreeItem) event.item).getText();
					Point size = event.gc.textExtent(text);
					int offset2 = event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0;
					event.gc.drawText(text, event.x, event.y + offset2, true);
					break;
				}
				case SWT.EraseItem: {
					event.detail &= ~SWT.FOREGROUND;
					break;
				}
				}
			}
		};
		duplicatesTree.addListener(SWT.MeasureItem, paintListener);
		duplicatesTree.addListener(SWT.PaintItem, paintListener);
		duplicatesTree.addListener(SWT.EraseItem, paintListener);
		duplicatesTree.addTreeListener(new TreeListener() {
			public void treeCollapsed(TreeEvent arg0) {
				// packTable();
			}

			public void treeExpanded(TreeEvent arg0) {
				// packTable();
			}
		});

		registerToolTipListeners();
	}

	/*
	 * Adapted from SWT snippet 125
	 * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet125.java?rev=HEAD&content-type=text/vnd.viewcvs-markup
	 */
	private void registerToolTipListeners() {
		final Shell shell = duplicatesTree.getShell();
		final Display display = duplicatesTree.getDisplay();

		// Disable native tooltip
		duplicatesTree.setToolTipText("");

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = (TableItem) label.getData("_TABLEITEM");
					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					duplicatesTree.setSelection(new TreeItem[] { (TreeItem) e.item });
					duplicatesTree.notifyListeners(SWT.Selection, e);
					// fall through
				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};

		Listener tableListener = new Listener() {
			Shell tip = null;

			Label label = null;

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					TreeItem item = duplicatesTree.getItem(new Point(event.x, event.y));
					if (item != null) {
						if (tip != null && !tip.isDisposed())
							tip.dispose();
						tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
						tip.setLayout(new FillLayout());
						label = new Label(tip, SWT.NONE);
						label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						label.setData("_TABLEITEM", item);
						label.setText((String) item.getData());
						label.addListener(SWT.MouseExit, labelListener);
						label.addListener(SWT.MouseDown, labelListener);
						Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						Rectangle rect = item.getBounds(0);
						Point pt = duplicatesTree.toDisplay(rect.x, rect.y);
						tip.setBounds(pt.x, pt.y, size.x, size.y);
						tip.setVisible(true);
					}
				}
				}
			}
		};
		duplicatesTree.addListener(SWT.Dispose, tableListener);
		duplicatesTree.addListener(SWT.KeyDown, tableListener);
		duplicatesTree.addListener(SWT.MouseMove, tableListener);
		duplicatesTree.addListener(SWT.MouseHover, tableListener);

	}

	private void packTable() {
		for (int i = 0; i < columnHeaders.length; i++) {
			// duplicatesTree.getColumn(i).pack();
			duplicatesTree.getColumn(i).setWidth(columnWidths[i]);
		}
		duplicatesTree.redraw();
	}

	public List<AbstractRepositoryTask> getRelatedTasks() {
		return relatedTasks;
	}

	public void setRelatedTasks(List<AbstractRepositoryTask> relatedTasks) {
		duplicatesTree.removeAll();
		this.relatedTasks = relatedTasks;
		if (duplicatesTree == null || this.relatedTasks == null) {
			return;
		}

		SelectionListener selectAllListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}

			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem) event.item;
				if (item.getParentItem() != null) {
					item.getParentItem().setChecked(item.getChecked());
				}
				if (item.getItems() != null && item.getItems().length > 0) {
					item.getItems()[0].setChecked(item.getChecked());
				}
			}
		};
		duplicatesTree.addSelectionListener(selectAllListener);

		// update the table
		Iterator<AbstractRepositoryTask> iter = this.relatedTasks.iterator();
		while (iter.hasNext()) {
			AbstractRepositoryTask task = iter.next();
			RepositoryTaskData taskData = task.getTaskData();
			if (taskData == null) {
				iter.remove();
				continue;
			}
			TreeItem item = new TreeItem(duplicatesTree, SWT.NONE);
			item.setText(new String[] { formatTreeText(taskData.getSummary(), LINES_PER_ITEM - 1) });
			TreeItem descItem = new TreeItem(item, SWT.NONE);
			descItem.setText(formatTreeText(taskData.getDescription()));
			descItem.setGrayed(true);

			String toolTip = "Report Id: " + taskData.getId() + "\nCreated on: " + taskData.getCreated()
					+ "\nCreated by: " + taskData.getReporter() + "\nAssigned to: " + taskData.getAssignedTo();
			item.setData(toolTip);
			descItem.setData(toolTip);
		}
		packTable();
	}

	private String formatTreeText(String text) {
		return formatTreeText(text, LINES_PER_ITEM);
	}

	private String formatTreeText(String text, int maxLines) {
		GC gc = new GC(duplicatesTree);
		int avgCharWidth = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();

		text = text.replace("\n", " ");
		StringTokenizer strtok = new StringTokenizer(text);
		StringBuffer formatText = new StringBuffer();
		int charsPerLine = columnWidths[0] / avgCharWidth;
		int lines = 0;

		// character wrap
		// while (strtok.hasMoreTokens() && lines < maxLines) {
		// String line = strtok.nextToken();
		// while (line.length() > charsPerLine && lines < maxLines) {
		// String trimmedLine = line.substring(0, charsPerLine);
		// formatText.append(trimmedLine + "\n");
		// lines++;
		// line = line.substring(charsPerLine);
		// }
		// formatText.append(line + "\n");
		// lines++;
		// }

		// word wrap
		StringBuffer line = new StringBuffer();
		while (strtok.hasMoreTokens() && lines < maxLines) {
			String word = strtok.nextToken();
			while (strtok.hasMoreTokens() && line.length() + word.length() < charsPerLine) {
				line.append(word + ((line.length() + word.length() + 1 > charsPerLine) ? "" : " "));
				word = strtok.nextToken();
			}
			if (!strtok.hasMoreTokens()) {
				line.append(word);
			}
			formatText.append(((formatText.length() == 0) ? "" : "\n") + line.toString());
			lines++;
			line.delete(0, line.length());
			line.append(word + " ");
		}

		// pad elements to ensure no weird repaint artifacts
		// this also centres text in the element (aligned with tree arrow)
		if (lines < LINES_PER_ITEM) {
			int diff = LINES_PER_ITEM - lines;
			for (int i = 0; i < diff / 2; i++) {
				formatText.append("\n ");
				lines++;
			}
			while (lines < LINES_PER_ITEM) {
				formatText.insert(0, " \n");
				lines++;
			}
			lines++;
		}

		// add "..." if we're cutting off the text
		if (strtok.hasMoreTokens()) {
			formatText.replace(formatText.length() - 4, formatText.length(), "...");
		}

		return formatText.toString();
	}

	public List<AbstractRepositoryTask> getSelectedReports() {
		List<AbstractRepositoryTask> selected = new LinkedList<AbstractRepositoryTask>();
		TreeItem[] items = duplicatesTree.getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				selected.add(relatedTasks.get(i));
			}
		}

		return selected;
	}
}
