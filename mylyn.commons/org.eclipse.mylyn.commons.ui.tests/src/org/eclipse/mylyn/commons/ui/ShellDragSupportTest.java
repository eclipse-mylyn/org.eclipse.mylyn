/*******************************************************************************
 * Copyright (c) 2016.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *******************************************************************************/
package org.eclipse.mylyn.commons.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ShellDragSupportTest {
	private final Composite composite = mock(Composite.class, Mockito.RETURNS_DEEP_STUBS);

	private final Shell shell = spy(new Shell());

	private ShellDragSupport support;

	@Before
	public void setUp() {
		shell.setLocation(100, 150);
		when(composite.getShell()).thenReturn(shell);
		when(composite.getDisplay()).thenReturn(Display.getDefault());
		support = new ShellDragSupport(composite);
	}

	@Test
	public void shellDragSupport() {
		verify(composite).getDisplay();
		verify(composite).getShell();
		verify(composite).addListener(SWT.MouseEnter, support);
		verify(composite).addListener(SWT.MouseExit, support);
		verify(composite).addListener(SWT.MouseMove, support);
		verify(composite).addListener(SWT.MouseDown, support);
		verify(composite).addListener(SWT.MouseUp, support);
		verify(composite).addListener(SWT.Dispose, support);
		verifyNoMoreInteractions(composite);
	}

	@Test
	public void handleDisposeEnter() {
		handleEvent(SWT.Dispose);
		assertTrue(support.getMoveCursor().isDisposed());
	}

	@Test
	public void handleMouseEnter() {
		handleEvent(SWT.MouseEnter);
		verify(shell).setCursor(eq(support.getMoveCursor()));
	}

	@Test
	public void handleMouseExit() {
		handleEvent(SWT.MouseExit);
		//	verify(shell).setCursor(notNull(nullValue(Cursor.class)));
	}

	@Test
	public void handleMouseMove() {
		handleEvent(SWT.MouseMove, 110, 160);
		assertEquals(new Point(100, 150), shell.getLocation());

		handleEvent(SWT.MouseDown, 105, 160);
		handleEvent(SWT.MouseMove, 109, 167);
		verify(shell).setLocation(104, 157);

		handleEvent(SWT.MouseMove, 111, 170);
		verify(shell).setLocation(106, 160);

		handleEvent(SWT.MouseMove, 110, 174);
		verify(shell).setLocation(105, 164);

		handleEvent(SWT.MouseUp);
		handleEvent(SWT.MouseMove, 112, 176);
		assertEquals(new Point(105, 164), shell.getLocation());
	}

	private void handleEvent(int type) {
		handleEvent(type, 0, 0);
	}

	/**
	 * x and y are relative to the display
	 */
	private void handleEvent(int type, int x, int y) {
		Event event = new Event();
		event.type = type;
		Point pt = shell.toControl(x, y);
		event.x = pt.x;
		event.y = pt.y;
		support.handleEvent(event);
	}

}
