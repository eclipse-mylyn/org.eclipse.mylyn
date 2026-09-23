/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Colors used to decorate task context elements, defined in the workbench theme so they adapt to the dark theme.
 *
 * @author Mik Kersten
 */
public class ColorMap {

	public static final String LANDMARK = "org.eclipse.mylyn.context.ui.colors.foreground.landmark"; //$NON-NLS-1$

	public static final String PREDICTED = "org.eclipse.mylyn.context.ui.colors.foreground.predicted"; //$NON-NLS-1$

	public static final String UNINTERESTING = "org.eclipse.mylyn.context.ui.colors.foreground.uninteresting"; //$NON-NLS-1$

	public static final String RELATIONSHIP = "org.eclipse.mylyn.context.ui.colors.foreground.relationship"; //$NON-NLS-1$

	/**
	 * Returns the theme color for the given id, falling back to the default list foreground so it is never
	 * {@code null}.
	 */
	public static Color getColor(String id) {
		final Color color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(id);
		return color != null ? color : Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	private GammaSetting gammaSetting = GammaSetting.STANDARD;

	public enum GammaSetting {
		LIGHTEN, DARKEN, STANDARD;

		public static int compare(GammaSetting gs1, GammaSetting gs2) {
			if (gs1 == LIGHTEN) {
				if (gs2 == STANDARD) {
					return 1;
				} else if (gs2 == DARKEN) {
					return 2;
				} else {
					return 0;
				}
			} else if (gs1 == STANDARD) {
				if (gs2 == LIGHTEN) {
					return -1;
				} else if (gs2 == DARKEN) {
					return 1;
				} else {
					return 0;
				}
			} else if (gs2 == LIGHTEN) {
				return -2;
			} else if (gs2 == STANDARD) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public GammaSetting getGammaSetting() {
		return gammaSetting;
	}

	public void setGammaSetting(GammaSetting gammaSetting) {
		this.gammaSetting = gammaSetting;
	}

}
