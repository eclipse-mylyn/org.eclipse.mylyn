/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * A popup window that uses the workbench shell image in the title.
 * 
 * @author Steffen Pingel
 */
public class AbstractWorkbenchNotificationPopup extends AbstractNotificationPopup {

	public AbstractWorkbenchNotificationPopup(Display display, int style) {
		super(display, style);
	}

	public AbstractWorkbenchNotificationPopup(Display display) {
		super(display);
	}

	@Override
	protected Image getPopupShellImage(int maximumHeight) {
		return WorkbenchUtil.getWorkbenchShellImage(maximumHeight);
	}

}
