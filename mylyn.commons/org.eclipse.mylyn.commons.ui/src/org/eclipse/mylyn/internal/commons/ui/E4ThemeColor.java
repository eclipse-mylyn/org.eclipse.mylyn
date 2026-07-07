/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class E4ThemeColor {

	private static boolean loggedError = false;

	private static final BundleContext BUNDLE_CONTEXT = FrameworkUtil.getBundle(E4ThemeColor.class).getBundleContext();

	@SuppressWarnings("restriction")
	private static final ServiceTracker<IThemeManager, IThemeManager> THEME_MANAGER_TRACKER;

	static {
		THEME_MANAGER_TRACKER = new ServiceTracker<>(BUNDLE_CONTEXT, IThemeManager.class, null);
		THEME_MANAGER_TRACKER.open();
	}

	public static RGB getRGBFromCssString(String cssValue) {
		try {
			if (cssValue.startsWith("rgb(")) { //$NON-NLS-1$
				String rest = cssValue.substring(4);
				int idx = rest.indexOf("rgb("); //$NON-NLS-1$
				if (idx != -1) {
					rest = rest.substring(idx + 4);
				}
				idx = rest.indexOf(")"); //$NON-NLS-1$
				if (idx != -1) {
					rest = rest.substring(0, idx);
				}
				String[] rgbValues = rest.split(","); //$NON-NLS-1$
				if (rgbValues.length == 3) {
					return new RGB(Integer.parseInt(rgbValues[0].trim()), Integer.parseInt(rgbValues[1].trim()),
							Integer.parseInt(rgbValues[2].trim()));
				}
			} else if (cssValue.startsWith("#")) { //$NON-NLS-1$
				// because of Bug 566549 - Avoid hard code dark colors in the dark theme and find
				// (https://git.eclipse.org/c/platform/eclipse.platform.ui.git/commit/?id=c6ce5643aa4a95e49f5d4029bbe5502312ce8160)
				// we need do change our code to work with the new values
				if (cssValue.equals("#org-eclipse-ui-workbench-DARK_BACKGROUND")) { //$NON-NLS-1$
					cssValue = "#515658"; //$NON-NLS-1$
				}
				if (cssValue.equals("#org-eclipse-ui-workbench-DARK_FOREGROUND")) { //$NON-NLS-1$
					cssValue = "#eeeeee"; //$NON-NLS-1$
				}
				String rest = cssValue.substring(1);
				int idx = rest.indexOf("#"); //$NON-NLS-1$
				if (idx != -1) {
					rest = rest.substring(idx + 1);
				}
				if (rest.length() > 5) {
					return new RGB(Integer.parseInt(rest.substring(0, 2), 16),
							Integer.parseInt(rest.substring(2, 4), 16), Integer.parseInt(rest.substring(4, 6), 16));
				}
			}
			throw new E4CssParseException("RGB", cssValue); //$NON-NLS-1$
		} catch (NumberFormatException | E4CssParseException e) {
			logOnce(e);
			return null;
		}
	}

	@SuppressWarnings({ "restriction" })
	public static String getCssValueFromTheme(Display display, String value) {
		IThemeManager themeManager = THEME_MANAGER_TRACKER.getService();
		if (themeManager == null) {
			return null;
		}
		IThemeEngine themeEngine = themeManager.getEngineForDisplay(display);
		if (themeEngine != null) {
			CSSStyleDeclaration shellStyle = getStyleDeclaration(themeEngine, display);
			if (shellStyle != null) {
				CSSValue cssValue = shellStyle.getPropertyCSSValue(value);
				if (cssValue != null) {
					return cssValue.getCssText();
				}
			}
	}
		return null;
	}

	private static CSSStyleDeclaration getStyleDeclaration(@SuppressWarnings("restriction") IThemeEngine themeEngine,
			Display display) {
		Shell shell = display.getActiveShell();
		CSSStyleDeclaration shellStyle = null;
		if (shell != null) {
			shellStyle = retrieveStyleFromShell(themeEngine, shell);
		} else {
			for (Shell input : display.getShells()) {
				shellStyle = retrieveStyleFromShell(themeEngine, input);
				if (shellStyle != null) {
					break;
				}
			}
		}
		return shellStyle;
	}

	private static CSSStyleDeclaration retrieveStyleFromShell(@SuppressWarnings("restriction") IThemeEngine themeEngine,
			Shell shell) {
		@SuppressWarnings("restriction")
		CSSStyleDeclaration shellStyle = themeEngine.getStyle(shell);
		return (CSSStyleDeclaration) shellStyle;
	}

	private static void logOnce(Exception e) {
		if (!loggedError) {
			StatusHandler.log(new Status(IStatus.ERROR, CommonsUiConstants.ID_PLUGIN, e.getMessage(), e));
			loggedError = true;
		}
	}
}
