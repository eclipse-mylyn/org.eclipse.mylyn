/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class CommonColors {

	public static final Color CONTEXT_ACTIVE = new Color(Display.getDefault(), 36, 22, 50);

	public static final Color HYPERLINK_WIDGET = new Color(Display.getDefault(), 12, 81, 172);

	public static final Color TEXT_QUOTED = new Color(Display.getDefault(), 38, 86, 145);

	public static final Color TEXT_SPELLING_ERROR = new Color(Display.getDefault(), 255, 0, 0);

	public static final Color GRAY_MID = new Color(Display.getDefault(), 100, 100, 100);

	/**
	 * NOTE: disposal of JFaceResources fonts handled by registry.
	 */
	public static void dispose() {
		CONTEXT_ACTIVE.dispose();
		HYPERLINK_WIDGET.dispose();
		TEXT_QUOTED.dispose();
		TEXT_SPELLING_ERROR.dispose();
	}

}
