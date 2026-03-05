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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class ColorMap {

	// TODO: use themes?
	public static final Color LANDMARK = new Color(Display.getDefault(), 80, 140, 200);

	public static final Color GRAY_MEDIUM = new Color(Display.getDefault(), 105, 105, 105);

	public static final Color GRAY_LIGHT = new Color(Display.getDefault(), 145, 145, 145);

	public static final Color RELATIONSHIP = new Color(Display.getDefault(), 0, 254, 0);

	public void dispose() {
		LANDMARK.dispose();
		GRAY_LIGHT.dispose();
		GRAY_MEDIUM.dispose();
		RELATIONSHIP.dispose();

		// below disposed by registry
		// DEFAULT.dispose();
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
