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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Steffen Pingel
 */
public class EditorUtilTest extends TestCase {

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

	public void testSetEnabledStateDisableStateRemoved() {
		Shell shell = new Shell();
		Composite composite = new Composite(shell, SWT.NONE);
		Label label = new Label(composite, SWT.NONE);
		label.setEnabled(false);

		CommonUiUtil.setEnabled(composite, false);
		CommonUiUtil.setEnabled(composite, true);
		// the second time all state information should have been removed and all controls enabled
		CommonUiUtil.setEnabled(composite, true);
		assertTrue(composite.getEnabled());
		assertTrue(label.getEnabled());
	}

}
