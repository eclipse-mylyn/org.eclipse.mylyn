/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Tracks which control had last focus for all registered controls.
 * 
 * @author Steffen Pingel
 */
public class FocusTracker {

	private Control lastFocusControl;

	private final FocusListener listener = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			lastFocusControl = (Control) e.widget;
		}
	};

	public void track(Composite composite) {
		Control[] children = composite.getChildren();
		for (Control control : children) {
			if ((control instanceof Text) || (control instanceof Button) || (control instanceof Combo)
					|| (control instanceof CCombo) || (control instanceof Tree) || (control instanceof Table)
					|| (control instanceof Spinner) || (control instanceof Link)
					|| (control instanceof org.eclipse.swt.widgets.List) || (control instanceof TabFolder)
					|| (control instanceof CTabFolder) || (control instanceof Hyperlink)
					|| (control instanceof FilteredTree) || (control instanceof StyledText)) {
				control.addFocusListener(listener);
			}
			if (control instanceof Composite) {
				track((Composite) control);
			}
		}
	}

	public void reset() {
		lastFocusControl = null;
	}

	public boolean setFocus() {
		if (lastFocusControl != null && !lastFocusControl.isDisposed()) {
			return lastFocusControl.setFocus();
		}
		return false;
	}

}
