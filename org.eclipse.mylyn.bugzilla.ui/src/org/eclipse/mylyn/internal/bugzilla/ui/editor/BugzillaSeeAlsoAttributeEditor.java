/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
* https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
*
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BugzillaSeeAlsoAttributeEditor extends AbstractAttributeEditor {

	private Table seeAlsoTable;

	private TaskAttribute attrRemoveSeeAlso;

	public BugzillaSeeAlsoAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		attrRemoveSeeAlso = getModel().getTaskData()
				.getRoot()
				.getMappedAttribute(BugzillaAttribute.REMOVE_SEE_ALSO.getKey());

		if (attrRemoveSeeAlso == null) {
			attrRemoveSeeAlso = BugzillaTaskDataHandler.createAttribute(getModel().getTaskData(),
					BugzillaAttribute.REMOVE_SEE_ALSO);
		}
		createSeeAlsoTable(toolkit, parent);
		setControl(seeAlsoTable);
	}

	private final String[] seeAlsoColumns = { "", Messages.BugzillaSeeAlsoAttributeEditor_Remove, //$NON-NLS-1$
			Messages.BugzillaSeeAlsoAttributeEditor_URL };

	private final int[] seeAlsoColumnWidths = { 25, 60, 100 };

	private TableViewer seeAlsoViewer;

	private void createSeeAlsoTable(FormToolkit toolkit, final Composite seeAlsoComposite) {

		seeAlsoTable = toolkit.createTable(seeAlsoComposite, SWT.MULTI | SWT.FULL_SELECTION);
		seeAlsoTable.setLinesVisible(true);
		seeAlsoTable.setHeaderVisible(true);
		seeAlsoTable.setLayout(new GridLayout());
		seeAlsoTable.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		for (int i = 0; i < seeAlsoColumns.length; i++) {
			TableColumn column = new TableColumn(seeAlsoTable, SWT.LEFT, i);
			column.setText(seeAlsoColumns[i]);
			column.setWidth(seeAlsoColumnWidths[i]);
			column.setMoveable(true);
		}

		seeAlsoViewer = new TableViewer(seeAlsoTable);
		seeAlsoViewer.setUseHashlookup(true);
		seeAlsoViewer.setColumnProperties(seeAlsoColumns);
		ColumnViewerToolTipSupport.enableFor(seeAlsoViewer, ToolTip.NO_RECREATE);

		seeAlsoViewer.setContentProvider(ArrayContentProvider.getInstance());
		seeAlsoViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				openseeAlso(event);
			}

			private void openseeAlso(OpenEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				for (String item : (List<String>) selection.toList()) {
					BrowserUtil.openUrl(item);
				}

			}
		});
		seeAlsoViewer.setLabelProvider(new ColumnLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				String value = (String) element;
				if (columnIndex == 0) {
					if (value.contains("/r/#/c/") || value.contains("git.eclipse.org/r/")) { //$NON-NLS-1$ //$NON-NLS-2$
						return CommonImages.getImage(BugzillaImages.GERRIT);
					} else if (value.contains("/commit/?id=")) { //$NON-NLS-1$
						return CommonImages.getImage(BugzillaImages.GIT);
					} else {
						return CommonImages.getImage(BugzillaImages.BUG);
					}
				}
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				String value = (String) element;
				switch (columnIndex) {
				case 0:
					return null;
				case 1:
					return attrRemoveSeeAlso.getValues().contains(value)
							? Messages.BugzillaSeeAlsoAttributeEditor_Yes
							: Messages.BugzillaSeeAlsoAttributeEditor_No;
				default:
					return value;
				}
			}

			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();
				cell.setText(getColumnText(element, cell.getColumnIndex()));
				Image image = getColumnImage(element, cell.getColumnIndex());
				cell.setImage(image);
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
				cell.setFont(getFont(element));
			}

		});
		seeAlsoViewer.setInput(getTaskAttribute().getValues().toArray());
		GC gc = new GC(seeAlsoComposite);
		int maxSize = 0;
		for (String string : getTaskAttribute().getValues()) {
			Point size = gc.textExtent(string);
			if (size.x > maxSize) {
				maxSize = size.x;
			}
		}
		if (maxSize == 0) {
			maxSize = 100;
		}
		seeAlsoTable.getColumn(2).setWidth(maxSize);
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(openAction);
				manager.add(copyURLToClipAction);
				manager.add(toggelRemoveStateAction);
			}
		});
		Menu menu = menuManager.createContextMenu(seeAlsoTable);
		seeAlsoTable.setMenu(menu);
	}

	final Action copyURLToClipAction = new Action(Messages.BugzillaSeeAlsoAttributeEditor_CopyURL) {
		@Override
		public void run() {
			StructuredSelection selection = ((StructuredSelection) seeAlsoViewer.getSelection());
			if (selection != null) {
				Object firstElement = selection.getFirstElement();
				if (firstElement != null) {
					Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					clip.setContents(new Object[] { (String) firstElement },
							new Transfer[] { TextTransfer.getInstance() });
					clip.dispose();
				}
			}
		}
	};

	final Action openAction = new Action(Messages.BugzillaSeeAlsoAttributeEditor_Open) {
		@Override
		public void run() {
			StructuredSelection selection = ((StructuredSelection) seeAlsoViewer.getSelection());
			if (selection != null) {
				for (String url : (List<String>) selection.toList()) {
					BrowserUtil.openUrl(url);
				}
			}
		}
	};

	final Action toggelRemoveStateAction = new Action(Messages.BugzillaSeeAlsoAttributeEditor_ToggelRemoveState) {
		@Override
		public void run() {
			StructuredSelection selection = ((StructuredSelection) seeAlsoViewer.getSelection());
			boolean changed = false;
			if (selection != null) {
				for (String url : (List<String>) selection.toList()) {
					if (attrRemoveSeeAlso.getValues().contains(url)) {
						attrRemoveSeeAlso.removeValue(url);
					} else {
						attrRemoveSeeAlso.addValue(url);
					}
					changed = true;
				}

				if (changed) {
					getModel().attributeChanged(attrRemoveSeeAlso);
					seeAlsoViewer.refresh();
				}
			}
		}
	};

}
