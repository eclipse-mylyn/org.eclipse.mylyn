/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class ClipboardCopier {

	public interface TextProvider {

		public abstract String getTextForElement(Object element);

	}

	private static ClipboardCopier instance = new ClipboardCopier();

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	public static ClipboardCopier getDefault() {
		return instance;
	}

	private Clipboard clipboard;

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
