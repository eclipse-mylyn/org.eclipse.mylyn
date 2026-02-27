/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
public class EditorUtilTest {

	@Test
	public void testSetEnabledState() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		Label label = new Label(composite, SWT.NONE);

		CommonUiUtil.setEnabled(composite, false);
		assertFalse(composite.getEnabled());
		assertFalse(label.getEnabled());

		CommonUiUtil.setEnabled(composite, true);
		assertTrue(composite.getEnabled());
		assertTrue(label.getEnabled());

		CommonUiUtil.setEnabled(composite, true);
		assertTrue(composite.getEnabled());
		assertTrue(label.getEnabled());
	}

	@Test
	public void testSetEnabledStateDisabledChild() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		Label label = new Label(composite, SWT.NONE);
		label.setEnabled(false);

		CommonUiUtil.setEnabled(composite, false);
		assertFalse(composite.getEnabled());
		assertFalse(label.getEnabled());

		CommonUiUtil.setEnabled(composite, true);
		assertTrue(composite.getEnabled());
		assertFalse(label.getEnabled());
	}

	@Test
	public void testSetEnabledStateDisabledParent() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setEnabled(false);
		Label label = new Label(composite, SWT.NONE);

		CommonUiUtil.setEnabled(composite, false);
		assertFalse(composite.getEnabled());
		assertFalse(label.getEnabled());

		CommonUiUtil.setEnabled(composite, true);
		assertFalse(composite.getEnabled());
		assertTrue(label.getEnabled());
	}

	@Test
	public void testSetEnabledStateDisabledChildAndComposite() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setEnabled(false);
		Label label = new Label(composite, SWT.NONE);
		label.setEnabled(false);

		CommonUiUtil.setEnabled(composite, false);
		assertFalse(composite.getEnabled());
		assertFalse(label.getEnabled());

		CommonUiUtil.setEnabled(composite, true);
		assertFalse(composite.getEnabled());
		assertFalse(label.getEnabled());
	}

	@Test
	public void testSetEnabledStateDisableStateRemoved() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		Label label = new Label(composite, SWT.NONE);
		label.setEnabled(false);

		CommonUiUtil.setEnabled(composite, false);
		CommonUiUtil.setEnabled(composite, true);
		// the second call should have not changed anything
		CommonUiUtil.setEnabled(composite, true);
		assertTrue(composite.getEnabled());
		assertFalse(label.getEnabled());
	}

	@Test
	public void testSetEnabledWithoutDisabling() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setEnabled(false);

		CommonUiUtil.setEnabled(composite, true);
		assertFalse(composite.getEnabled());
		CommonUiUtil.setEnabled(composite, true);
		assertFalse(composite.getEnabled());
	}

	@Test
	public void testAddScrollListener() {
		Scrollable textWidget = new Composite(WorkbenchUtil.getShell(), SWT.V_SCROLL);

		assertNotNull(textWidget.getVerticalBar());
		assertEquals(0, textWidget.getListeners(SWT.MouseVerticalWheel).length);

		EditorUtil.addScrollListener(textWidget);
		assertEquals(1, textWidget.getListeners(SWT.MouseVerticalWheel).length);

		// test when there is no vertical bar
		textWidget = new Composite(WorkbenchUtil.getShell(), SWT.NONE);
		assertNull(textWidget.getVerticalBar());

		assertEquals(0, textWidget.getListeners(SWT.MouseVerticalWheel).length);

		EditorUtil.addScrollListener(textWidget);
		assertEquals(1, textWidget.getListeners(SWT.MouseVerticalWheel).length);
	}

}
