/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/**
 * An <code>Action</code> which copies the specified contents to the clipboard
 * obtained from the specified <code>Control</code>
 * 
 * @author Jeff Pound
 */
public class CopyToClipboardAction extends Action {
	
	public static final String TITLE = "Copy to Clipboard";

	private String contents = null;

	private Control control = null;

	public CopyToClipboardAction() {
		super(TITLE);
	}

	@Override
	public void run() {
		if (contents == null) {
			return;
		}
		
		// use system line endings
		contents = contents.replaceAll("\r\n|\n", System.getProperty("line.separator"));

		if (control != null && contents != null) {
			Clipboard clipboard = new Clipboard(control.getDisplay());
			clipboard.setContents(new Object[] { contents }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		}
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setControl(Control control) {
		this.control = control;
	}
}
