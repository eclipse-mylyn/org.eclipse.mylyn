/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Allows users to move a shell by dragging the given composite.
 * 
 * @since 3.17
 */
public final class ShellDragSupport implements Listener {
	private int x = -1, y;

	private final Cursor moveCursor;

	private final Shell shell;

	public ShellDragSupport(Composite composite) {
		moveCursor = new Cursor(composite.getDisplay(), SWT.CURSOR_SIZEALL);
		shell = composite.getShell();
		composite.addListener(SWT.MouseEnter, this);
		composite.addListener(SWT.MouseExit, this);
		composite.addListener(SWT.MouseMove, this);
		composite.addListener(SWT.MouseDown, this);
		composite.addListener(SWT.MouseUp, this);
		composite.addListener(SWT.Dispose, this);
	}

	@Override
	public void handleEvent(Event event) {
		Point pt = shell.toDisplay(event.x, event.y);
		switch (event.type) {
		case SWT.MouseEnter:
			shell.setCursor(moveCursor);
			break;
		case SWT.MouseExit:
			shell.setCursor(null);
			break;
		case SWT.MouseMove:
			if (x == -1) {
				break;
			}
			Point location = shell.getLocation();
			shell.setLocation(location.x + pt.x - x, location.y + pt.y - y);
			// fall through
		case SWT.MouseDown:
			x = pt.x;
			y = pt.y;
			break;
		case SWT.MouseUp:
			x = -1;
			break;
		case SWT.Dispose:
			moveCursor.dispose();
		}
	}

	Cursor getMoveCursor() {
		return moveCursor;
	}
}