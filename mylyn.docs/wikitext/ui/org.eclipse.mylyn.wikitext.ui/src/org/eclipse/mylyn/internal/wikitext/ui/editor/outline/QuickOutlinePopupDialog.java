/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Marc-Andre Laperle (Ericsson) - Fix background color on Ubuntu
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.outline;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

/**
 * A quick outline popup. Design based on PDE class by the same name.
 *
 * @author David Green
 */
public class QuickOutlinePopupDialog extends PopupDialog
		implements IInformationControl, IInformationControlExtension, IInformationControlExtension2 {

	private FilteredTree filteredTree;

	private final IShowInTarget showInTarget;

	private PatternFilter patternFilter;

	public QuickOutlinePopupDialog(Shell parent, IShowInTarget showInTarget) {
		super(parent, SWT.RESIZE, true, true, true, false, false, null, null);
		this.showInTarget = showInTarget;
		setInfoText(Messages.QuickOutlinePopupDialog_infoText);
		create();
	}

	@Override
	protected Control createTitleControl(Composite parent) {
		Text fileterText = new Text(parent, SWT.SEARCH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileterText);
		return fileterText;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		patternFilter = new PatternFilter();
		filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter,
				true, true);

		int heightHint = filteredTree.getViewer().getTree().getItemHeight() * 12
				+ Math.max(filteredTree.getFilterControl().getSize().y, 12);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, heightHint).applyTo(filteredTree);

		filteredTree.getViewer().setUseHashlookup(true);
		filteredTree.getViewer().setContentProvider(new BaseWorkbenchContentProvider());
		filteredTree.getViewer().setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		filteredTree.getViewer().setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		filteredTree.getViewer()
				.getTree()
				.addSelectionListener(SelectionListener.widgetDefaultSelectedAdapter(e -> handleSelection()));
		// dispose when escape is pressed
		filteredTree.getViewer().getTree().addKeyListener(KeyListener.keyPressedAdapter(e -> {
			if (e.character == 0x1B) {
				dispose();
			}
		}));
		// single mouse click causes selection
		filteredTree.getViewer().getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Tree tree = filteredTree.getViewer().getTree();
				if (tree.getSelectionCount() < 1 || e.button != 1) {
					return;
				}
				// Selection is made in the selection changed listener
				Object object = tree.getItem(new Point(e.x, e.y));
				TreeItem selection = tree.getSelection()[0];
				if (selection.equals(object)) {
					handleSelection();
				}
			}
		});
		return filteredTree;
	}

	@Override
	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		getShell().addFocusListener(listener);
	}

	@Override
	public Point computeSizeHint() {
		return getShell().getSize();
	}

	@Override
	public void dispose() {
		close();
	}

	@Override
	public boolean isFocusControl() {
		return filteredTree.isFocusControl() || filteredTree.getFilterControl().isFocusControl()
				|| filteredTree.getViewer().getTree().isFocusControl();
	}

	@Override
	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		getShell().removeFocusListener(listener);
	}

	@Override
	public void setFocus() {
		getShell().forceFocus();
		filteredTree.getFilterControl().setFocus();
	}

	@Override
	public void setBackgroundColor(Color background) {
		applyBackgroundColor(background, getContents());
	}

	@Override
	public void setForegroundColor(Color foreground) {
		applyForegroundColor(foreground, getContents());
	}

	@Override
	public void setInformation(String information) {
		// ignore
	}

	@Override
	public void setLocation(Point location) {
		getShell().setLocation(location);
	}

	@Override
	public void setSize(int width, int height) {
		getShell().setSize(width, height);
	}

	@Override
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// ignore

	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			open();
		} else {
			saveDialogBounds(getShell());
			getShell().setVisible(false);
		}
	}

	@Override
	public boolean hasContents() {
		if (filteredTree == null || filteredTree.getViewer().getInput() == null) {
			return false;
		}
		return true;
	}

	@Override
	public void setInput(Object input) {
		filteredTree.getViewer().setInput(input);
	}

	private void handleSelection() {
		if (showInTarget != null) {
			ISelection selection = filteredTree.getViewer().getSelection();
			if (!selection.isEmpty()) {
				showInTarget.show(new ShowInContext(null, selection));
			}
		}
		dispose();
	}

}
