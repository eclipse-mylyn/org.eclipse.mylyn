/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Benjamin Pasero - initial API and implementation
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

import org.eclipse.swt.widgets.Display;

/**
 * A popup window with a title bar and message area for displaying notifications.
 *
 * @author Benjamin Pasero
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.7
 * @Deprecated Use {@link org.eclipse.jface.notifications.AbstractNotificationPopup} instead.
 */
@Deprecated(since = "4.13", forRemoval = true)
public abstract class AbstractNotificationPopup extends org.eclipse.jface.notifications.AbstractNotificationPopup {

	@Deprecated(since = "4.13", forRemoval = true)
	public AbstractNotificationPopup(Display display) {
		super(display);
	}

	@Deprecated(since = "4.13", forRemoval = true)
	public AbstractNotificationPopup(Display display, int style) {
		super(display, style);
	}

}
