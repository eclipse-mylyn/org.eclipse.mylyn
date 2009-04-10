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

import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;

/**
 * @author Steffen Pingel
 */
public class PlatformUtil {

	private static ByteArrayTransfer urlTransfer;
	static {
		// TODO e3.4 use URLTransfer directly and not through reflection
		// URLTransfer is package protected in Eclipse 3.3 (bug 100095)
		// use reflection to access instance for now
		try {
			Class<?> clazz = Class.forName("org.eclipse.swt.dnd.URLTransfer"); //$NON-NLS-1$
			Method method = clazz.getMethod("getInstance"); //$NON-NLS-1$
			if (method != null) {
				urlTransfer = (ByteArrayTransfer) method.invoke(null);
			}
		} catch (Throwable e) {
			// ignore
		}
		if (urlTransfer == null) {
			urlTransfer = new ByteArrayTransfer() {

				private static final String TYPE = "dummy"; //$NON-NLS-1$

				private final int TYPE_ID = registerType(TYPE);

				@Override
				protected int[] getTypeIds() {
					return new int[] { TYPE_ID };
				}

				@Override
				protected String[] getTypeNames() {
					return new String[] { TYPE };
				}

			};
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
		} else if ("carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$ //$NON-NLS-2$
			return 3;
		} else {
			return 0;
		}
	}

	public static boolean isPaintItemClippingRequired() {
		return "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$
	}

	public static ByteArrayTransfer getUrlTransfer() {
		return urlTransfer;
	}

}
