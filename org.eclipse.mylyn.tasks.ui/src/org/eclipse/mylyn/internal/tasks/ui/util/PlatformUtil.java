/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 247182
 *     Frank Becker - fixes for bug 259877
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

/**
 * @author Steffen Pingel
 * @deprecated use {@link PlatformUiUtil} instead.
 */
@Deprecated
public class PlatformUtil {

	private static class Eclipse36Checker {
		public static final boolean result;
		static {
			boolean methodAvailable = false;
			try {
				StyledText.class.getMethod("setTabStops", int[].class); //$NON-NLS-1$
				methodAvailable = true;
			} catch (NoSuchMethodException e) {
			}
			result = methodAvailable;
		}
	}

	/**
	 * bug 247182: file import dialog doesn't work on Mac OS X if the file extension has more than one dot.
	 */
	public static String[] getFilterExtensions(String... extensions) {
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Platform.OS_MACOSX.equals(Platform.getOS())) {
				int j = extension.lastIndexOf('.');
				if (j != -1) {
					extension = extension.substring(j);
				}
			}
			extensions[i] = "*" + extension; //$NON-NLS-1$
		}
		return extensions;
	}

	public static int getToolTipXShift() {
		if ("gtk".equals(SWT.getPlatform()) || "carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return -26;
		} else {
			return -23;
		}
	}

	public static int getTreeImageOffset() {
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 16;
		} else if ("cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 13;
		} else {
			return 20;
		}
	}

	public static int getIncomingImageOffset() {
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 5;
		} else if ("cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 2;
		} else {
			return 6;
		}
	}

	public static int getTreeItemSquish() {
		if ("gtk".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 8;
		} else if (isMac()) {
			return 3;
		} else {
			return 0;
		}
	}

	private static boolean isMac() {
		return "carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean isPaintItemClippingRequired() {
		return "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$
	}

	public static boolean spinnerHasNativeBorder() {
		return isMac() && !isEclipse36orLater();
	}

	private static boolean isEclipse36orLater() {
		return Eclipse36Checker.result;
	}

	public static boolean hasNarrowToolBar() {
		return Platform.WS_WIN32.equals(SWT.getPlatform());
	}

	/**
	 * Returns the width of the view menu drop-down button.
	 */
	public static int getViewMenuWidth() {
		return 32;
	}

}
