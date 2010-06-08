/*******************************************************************************
 * Copyright (c) 2009, 2010 Hiroyuki Inaba and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Hiroyuki Inaba - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A {@link FilteredTree} that uses the new look on Eclipse 3.5 and later.
 * 
 * @author Hiroyuki Inaba
 */
// TODO e3.5 remove this class and replace with FilteredTree
public class EnhancedFilteredTree extends FilteredTree {

	protected boolean useNewLook;

	private TextSearchControl searchControl;

	public EnhancedFilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
		super(parent, treeStyle, filter);
	}

	public EnhancedFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
	}

	public EnhancedFilteredTree(Composite parent) {
		super(parent);
	}

	@Override
	protected void createControl(Composite parent, int treeStyle) {
		super.createControl(parent, treeStyle);

		// set this after so that there isn't a double border created around the search
		useNewLook = setNewLook(this);
	}

	@Override
	protected Composite createFilterControls(Composite parent) {
		createFilterText(parent);
		return parent;
	}

	public static boolean setNewLook(FilteredTree tree) {
		try {
			Field newStyleField = FilteredTree.class.getDeclaredField("useNewLook"); //$NON-NLS-1$
			newStyleField.setAccessible(true);
			newStyleField.setBoolean(tree, true);
			return newStyleField.getBoolean(tree);
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	@Override
	protected void createFilterText(Composite parent) {
		super.createFilterText(parent);

		// This code is here to make it so that the key listener for the down arrow listens to the KeyEvent.doit 
		// flag so that the history popup dialog can be keyboard accessible and the down arrow works to select items 
		// from the history 
		if (searchControl != null && searchControl.hasHistorySupport()) {
			Text textControl = searchControl.getTextControl();
			KeyListener downArrowListener = null;
			Listener[] listeners = textControl.getListeners(SWT.KeyDown);
			if (listeners != null && listeners.length > 0) {
				for (Listener listener : listeners) {
					if (listener instanceof TypedListener
							&& ((TypedListener) listener).getEventListener().getClass().getName().startsWith(
									"org.eclipse.ui.dialogs.FilteredTree$") //$NON-NLS-1$
							&& ((TypedListener) listener).getEventListener() instanceof KeyListener) {
						downArrowListener = (KeyListener) ((TypedListener) listener).getEventListener();
						break;
					}
				}
			}
			if (downArrowListener != null) {
				final KeyListener oldKeyListener = downArrowListener;
				textControl.removeKeyListener(downArrowListener);
				textControl.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.doit) {
							oldKeyListener.keyPressed(e);
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if (e.doit) {
							oldKeyListener.keyReleased(e);
						}
					}
				});
			}
		}

	}

	@Override
	protected Text doCreateFilterText(Composite parent) {
		searchControl = new TextSearchControl(parent, true, getHistoryPopupDialog());

		searchControl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == TextSearchControl.ICON_CANCEL) {
					clearText();
				}
				if (e.detail == TextSearchControl.ICON_SEARCH) {
					textChanged();
				}
			}
		});
		searchControl.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		return searchControl.getTextControl();
	}

	public TextSearchControl getTextSearchControl() {
		return searchControl;
	}

	protected SearchHistoryPopUpDialog getHistoryPopupDialog() {
		return new SearchHistoryPopUpDialog(getShell(), SWT.TOP);
	}
}
