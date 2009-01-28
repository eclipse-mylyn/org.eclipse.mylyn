/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public abstract class ClipboardCopier {

	private Clipboard clipboard;

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	public ClipboardCopier() {
	}

	public void copy(IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				Object item = it.next();
				String textForElement = getTextForElement(item);
				if (textForElement != null) {
					if (sb.length() > 0) {
						sb.append(LINE_SEPARATOR);
						sb.append(LINE_SEPARATOR);
					}
					sb.append(textForElement);
				}
			}
			copy(sb.toString());
		}
	}

	protected abstract String getTextForElement(Object element);

	public void copy(String text) {
		if (clipboard == null) {
			Display display = PlatformUI.getWorkbench().getDisplay();
			clipboard = new Clipboard(display);
		}

		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
	}

	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
	}

}
