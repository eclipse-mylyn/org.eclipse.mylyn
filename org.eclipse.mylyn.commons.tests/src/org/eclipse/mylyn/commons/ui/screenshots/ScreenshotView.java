/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
