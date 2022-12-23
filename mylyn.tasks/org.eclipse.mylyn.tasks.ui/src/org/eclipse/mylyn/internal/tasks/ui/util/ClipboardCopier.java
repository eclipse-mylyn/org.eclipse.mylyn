/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.ui.ClipboardCopier} instead
 */
@Deprecated
public class ClipboardCopier {

	public interface TextProvider {

		public abstract String getTextForElement(Object element);

	}

	private static ClipboardCopier instance = new ClipboardCopier();

	public static ClipboardCopier getDefault() {
		return instance;
	}

	private Clipboard clipboard;

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	public ClipboardCopier() {
	}

	public void copy(IStructuredSelection selection, TextProvider provider) {
		if (!selection.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				Object item = it.next();
				String textForElement = provider.getTextForElement(item);
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

	public void copy(String text) {
		Assert.isNotNull(text);

		// Gtk does support copying empty strings to the clipboard
		if (text.length() == 0) {
			return;
		}

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
