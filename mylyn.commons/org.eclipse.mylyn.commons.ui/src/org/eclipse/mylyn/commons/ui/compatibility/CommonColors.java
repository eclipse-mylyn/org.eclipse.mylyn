/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.compatibility;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Provides common colors. Class must be referenced from the SWT UI thread only.
 * <p>
 * NOTE: Use of this class is discouraged. Colors are not theme dependent.
 * </p>
 *
 * @author Mik Kersten
 * @since 3.7
 * @deprecated
 */
@Deprecated
public class CommonColors {

	@Deprecated
	public static final Color HYPERLINK_WIDGET = new Color(Display.getDefault(), 12, 81, 172);

	@Deprecated
	public static final Color TEXT_QUOTED = new Color(Display.getDefault(), 38, 86, 145);

	/**
	 * NOTE: disposal of JFaceResources fonts handled by registry.
	 */
	@Deprecated
	public static void dispose() {
		HYPERLINK_WIDGET.dispose();
		TEXT_QUOTED.dispose();
	}

}
