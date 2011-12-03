/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.mylyn.internal.commons.workbench.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
 * Provides a utilities for opening locations in a browser.
 * 
 * @author Steffen Pingel
 */
public class BrowserUtil {

	/**
	 * Opens <code>location</code> in a web-browser according to the Eclipse workbench preferences.
	 * 
	 * @param location
	 *            the url to open
	 * @see #openUrl(String, int)
	 */
	public static void openUrl(String location) {
		openUrl(location, SWT.NONE);
	}

	/**
	 * Opens <code>location</code> in a web-browser according to the Eclipse workbench preferences.
	 * 
	 * @param location
	 *            the url to open
	 * @param customFlags
	 *            additional flags that are passed to {@link IWorkbenchBrowserSupport}, pass
	 *            {@link IWorkbenchBrowserSupport#AS_EXTERNAL} to force opening external browser
	 */
	public static void openUrl(String location, int customFlags) {
		try {
			URL url = null;
			if (location != null) {
				url = new URL(location);
			}
			if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.EXTERNAL
					|| (customFlags & IWorkbenchBrowserSupport.AS_EXTERNAL) != 0) {
				try {
					IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
					support.getExternalBrowser().openURL(url);
				} catch (PartInitException e) {
					Status status = new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
							Messages.WorkbenchUtil_Browser_Initialization_Failed);
					CommonsWorkbenchPlugin.getDefault().getLog().log(status);
					if (!CoreUtil.TEST_MODE) {
						MessageDialog.openError(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
								status.getMessage());
					}
				}
			} else {
				IWebBrowser browser = null;
				int flags = customFlags;
				if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
					flags |= IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.NAVIGATION_BAR;
				} else {
					flags |= IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.NAVIGATION_BAR;
				}

				String generatedId = "org.eclipse.mylyn.web.browser-" + Calendar.getInstance().getTimeInMillis(); //$NON-NLS-1$
				browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, generatedId, null, null);
				browser.openURL(url);
			}
		} catch (PartInitException e) {
			Status status = new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
					Messages.WorkbenchUtil_Browser_Initialization_Failed, e);
			CommonsWorkbenchPlugin.getDefault().getLog().log(status);
			if (!CoreUtil.TEST_MODE) {
				MessageDialog.openError(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
						status.getMessage());
			}
		} catch (MalformedURLException e) {
			if (location != null && location.trim().equals("")) { //$NON-NLS-1$
				Status status = new Status(IStatus.WARNING, CommonsWorkbenchPlugin.ID_PLUGIN,
						Messages.WorkbenchUtil_No_URL_Error, e);
				if (!CoreUtil.TEST_MODE) {
					MessageDialog.openWarning(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
							status.getMessage());
				} else {
					CommonsWorkbenchPlugin.getDefault().getLog().log(status);
				}
			} else {
				Status status = new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN, NLS.bind(
						Messages.WorkbenchUtil_Invalid_URL_Error, location), e);
				if (!CoreUtil.TEST_MODE) {
					MessageDialog.openError(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
							status.getMessage());
				} else {
					CommonsWorkbenchPlugin.getDefault().getLog().log(status);
				}
			}
		}
	}

}
