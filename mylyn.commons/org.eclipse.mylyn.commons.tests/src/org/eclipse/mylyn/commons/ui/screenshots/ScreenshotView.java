/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.screenshots;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Steffen
 */
public class ScreenshotView extends ViewPart {

	private ScreenshotViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new ScreenshotViewer(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
