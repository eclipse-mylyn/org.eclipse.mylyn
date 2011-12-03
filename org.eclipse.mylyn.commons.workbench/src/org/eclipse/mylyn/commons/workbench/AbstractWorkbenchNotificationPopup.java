/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
