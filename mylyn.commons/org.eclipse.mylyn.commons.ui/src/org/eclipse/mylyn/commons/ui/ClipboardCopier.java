/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * A helper class for copying text to the clipboard.
 *
 * @author Steffen Pingel
 * @since 3.7
 */
public class ClipboardCopier {

	/**
	 * Provides a textual representation when copying objects to the clipboard.
	 */
	public interface TextProvider {

		String getTextForElement(Object element);

	}

	private static ClipboardCopier instance = new ClipboardCopier();

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Returns the default instance.
	 */
	public static ClipboardCopier getDefault() {
		return instance;
	}

	private Clipboard clipboard;

	/**
	 * Constructs a new instance. Instances must be disposed by invoking {@link #dispose()} when they are no longer needed.
	 */
	public ClipboardCopier() {
	}

	public void copy(IStructuredSelection selection, TextProvider provider) {
		copy(selection.toList(), provider);
	}

	public void copy(List<?> selection, TextProvider provider) {
		if (!selection.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object item : selection) {
				String textForElement = provider.getTextForElement(item);
				if (textForElement != null) {
					if (sb.length() > 0) {
						sb.append(LINE_SEPARATOR);
						sb.append(LINE_SEPARATOR);
					}
					sb.append(textForElement);
				}
			}
			if (sb.length() > 0) {
				copy(sb.toString());
			}
		}
	}

	public void copy(String text) {
		Assert.isNotNull(text);
		Assert.isTrue(text.length() > 0);

		if (clipboard == null) {
			clipboard = new Clipboard(Display.getDefault());
		}

		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
	}

	/**
	 * Frees resources.
	 */
	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
	}

}
