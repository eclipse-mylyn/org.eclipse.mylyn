/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 247182
 *     Frank Becker - fixes for bug 259877
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;

/**
 * @author Steffen Pingel
 */
public class PlatformUtil {

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
		return "cocoa".equals(SWT.getPlatform()) ? 15 : 20; //$NON-NLS-1$
	}

	public static int getTreeItemSquish() {
		if ("gtk".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return 8;
		} else if ("carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$ //$NON-NLS-2$
			return 3;
		} else {
			return 0;
		}
	}

	public static boolean isPaintItemClippingRequired() {
		return "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$
	}

}
