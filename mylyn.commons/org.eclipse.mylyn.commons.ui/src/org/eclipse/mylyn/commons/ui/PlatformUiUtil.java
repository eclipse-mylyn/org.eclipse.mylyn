/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 247182
 *     Frank Becker - fixes for bug 259877
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public class PlatformUiUtil {

	private static Boolean internalBrowserAvailable;

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
		if ("gtk".equals(SWT.getPlatform()) || "carbon".equals(SWT.getPlatform()) //$NON-NLS-1$//$NON-NLS-2$
				|| "cocoa".equals(SWT.getPlatform())) { //$NON-NLS-1$
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

	/**
	 * @deprecated no longer required as platform bug 272046 has been fixed
	 */
	@Deprecated
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
	 * If a a section does not use a toolbar as its text client the spacing between the section header and client will be different from
	 * other sections. This method returns the value to set as the vertical spacing on those sections to align the vertical position of
	 * section clients.
	 *
	 * @return value for {@link org.eclipse.ui.forms.widgets.Section#clientVerticalSpacing}
	 */
	public static int getToolbarSectionClientVerticalSpacing() {
		if (Platform.WS_WIN32.equals(SWT.getPlatform())) {
			return 5;
		}
		return 7;
	}

	/**
	 * Returns the width of the view menu drop-down button.
	 */
	public static int getViewMenuWidth() {
		return 32;
	}

	/**
	 * Because of bug# 322293 (NPE when select Hyperlink from MultipleHyperlinkPresenter List) for MacOS we enable this only if running on
	 * Eclipse >= "3.7.0.v201101192000"
	 */
	public static boolean supportsMultipleHyperlinkPresenter() {
		if (isMac()) {
			Bundle bundle = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
			if (bundle != null) {
				String versionString = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
				Version version = new Version(versionString);
				return version.compareTo(new Version("3.7.0.v201101192000")) >= 0; //$NON-NLS-1$
			} else {
				bundle = Platform.getBundle("org.eclipse.swt"); //$NON-NLS-1$
				if (bundle != null) {
					String versionString = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
					Version version = new Version(versionString);
					return version.compareTo(new Version("3.7.0.v3721")) >= 0; //$NON-NLS-1$
				} else {
					//TODO e3.7 change this to true when eclipse 3.6 reach end of live!
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Because of bug#175655: [context] provide an on-hover affordance to supplement Alt+click navigation Tooltips will show everyone on
	 * Linux unless they are balloons.
	 */
	public static int getSwtTooltipStyle() {
		if ("gtk".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return SWT.BALLOON;
		}
		return SWT.NONE;
	}

	public static boolean usesMouseWheelEventsForScrolling() {
		return "cocoa".equals(SWT.getPlatform()); //$NON-NLS-1$
	}

	public static boolean hasInternalBrowser() {
		if (internalBrowserAvailable == null) {
			try {
				Shell[] shells = Display.getDefault().getShells();
				if (shells.length > 0) {
					Browser browser = new Browser(shells[0], SWT.NONE);
					browser.dispose();
					internalBrowserAvailable = Boolean.TRUE;
				} else {
					// can't determine status
					return false;
				}
			} catch (SWTError er) {
				internalBrowserAvailable = Boolean.FALSE;
			}
		}
		return internalBrowserAvailable;
	}

	/**
	 * @since 3.18
	 */
	public static boolean isNeonOrLater() {
		Bundle bundle = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
		if (bundle != null) {
			String versionString = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			Version version = new Version(versionString);
			return version.compareTo(new Version("4.6.0.v20151020-0800")) >= 0; //$NON-NLS-1$
		}
		bundle = Platform.getBundle("org.eclipse.swt"); //$NON-NLS-1$
		if (bundle != null) {
			String versionString = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			Version version = new Version(versionString);
			return version.compareTo(new Version("3.105.0.v20151020-0634")) >= 0; //$NON-NLS-1$
		}
		return false;

	}

}
