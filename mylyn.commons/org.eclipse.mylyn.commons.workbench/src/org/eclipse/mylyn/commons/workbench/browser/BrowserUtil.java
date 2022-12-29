/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.mylyn.internal.commons.workbench.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
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
 * @since 3.7
 */
public class BrowserUtil {

	/**
	 * Flag that indicates that a URL should be opened in browser and not in a rich editor.
	 * 
	 * @see #openUrl(IWorkbenchPage, String, int)
	 * @since 3.7
	 */
	public static final int NO_RICH_EDITOR = 1 << 17;

	static class BrowserHandle extends EditorHandle {

		private final IWebBrowser browser;

		public BrowserHandle(IWebBrowser browser) {
			super(Status.OK_STATUS);
			this.browser = browser;
		}

		@Override
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			if (adapter == IWebBrowser.class) {
				return browser;
			}
			return super.getAdapter(adapter);
		}

	}

	static class UrlHandlerInitializer {

		private static List<AbstractUrlHandler> handlers;

		static {
			ExtensionPointReader<AbstractUrlHandler> reader = new ExtensionPointReader<AbstractUrlHandler>(
					CommonsWorkbenchPlugin.ID_PLUGIN, "urlHandlers", "handler", AbstractUrlHandler.class); //$NON-NLS-1$ //$NON-NLS-2$
			reader.read();

			handlers = reader.getItems();

			// handlers with higher priories are sorted first
			Collections.sort(handlers, new Comparator<AbstractUrlHandler>() {
				public int compare(AbstractUrlHandler o1, AbstractUrlHandler o2) {
					return o2.getPriority() - o1.getPriority();
				};
			});
		}

	}

	/**
	 * Opens <code>location</code> in a rich editor if applicable or web-browser according to the workbench preferences.
	 * 
	 * @param location
	 *            the url to open
	 * @see #openUrl(IWorkbenchPage, String, int)
	 * @since 3.7
	 */
	public static void openUrl(String location) {
		openUrl(location, SWT.NONE);
	}

	/**
	 * Opens <code>location</code> in a rich editor if applicable or in a web-browser according to the workbench
	 * preferences.
	 * 
	 * @param location
	 *            the url to open
	 * @param customFlags
	 *            additional flags that are passed to {@link IWorkbenchBrowserSupport}, pass
	 *            {@link IWorkbenchBrowserSupport#AS_EXTERNAL} to force opening external browser
	 * @see #openUrl(IWorkbenchPage, String, int)
	 * @since 3.7
	 */
	public static void openUrl(String location, int customFlags) {
		IWorkbenchPage page = null;
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		}
		openUrl(page, location, customFlags);
	}

	/**
	 * Opens <code>location</code> in a rich editor if applicable or in a web-browser according to the workbench
	 * preferences.
	 * 
	 * @param page
	 *            the workbench page to open the editor in
	 * @param location
	 *            the url to open
	 * @param customFlags
	 *            additional flags that are passed to {@link IWorkbenchBrowserSupport}, pass
	 *            {@link IWorkbenchBrowserSupport#AS_EXTERNAL} to force opening external browser
	 * @return a handle that describes the editor or browser that was opened; if {@link EditorHandle#getStatus()}
	 *         returns an error status the operation was not successful
	 * @since 3.7
	 */
	public static EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags) {
		try {
			return openUrlInternal(page, location, customFlags);
		} catch (PartInitException e) {
			Status status = new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
					Messages.WorkbenchUtil_Browser_Initialization_Failed, e);
			CommonsWorkbenchPlugin.getDefault().getLog().log(status);
			if (!CoreUtil.TEST_MODE) {
				MessageDialog.openError(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
						status.getMessage());
			}
			return new EditorHandle(status);
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
				return new EditorHandle(status);
			} else {
				Status status = new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
						NLS.bind(Messages.WorkbenchUtil_Invalid_URL_Error, location), e);
				if (!CoreUtil.TEST_MODE) {
					MessageDialog.openError(WorkbenchUtil.getShell(), Messages.WorkbenchUtil_Open_Location_Title,
							status.getMessage());
				} else {
					CommonsWorkbenchPlugin.getDefault().getLog().log(status);
				}
				return new EditorHandle(status);
			}
		}
	}

	private static EditorHandle openUrlInternal(IWorkbenchPage page, String location, int customFlags)
			throws MalformedURLException, PartInitException {
		if (location != null && (customFlags & IWorkbenchBrowserSupport.AS_EXTERNAL) == 0
				&& (customFlags & NO_RICH_EDITOR) == 0) {
			// delegate to handler
			if (page != null) {
				EditorHandle handle = openUrlByHandler(page, location, customFlags);
				if (handle != null) {
					return handle;
				}
			}
		}

		URL url = null;
		if (location != null) {
			url = new URL(location);
		}

		if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.EXTERNAL
				|| (customFlags & IWorkbenchBrowserSupport.AS_EXTERNAL) != 0) {
			// open in external browser
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			final IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(url);
			return new BrowserHandle(browser);
		} else {
			// open in internal browser
			int flags = customFlags;
			if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
				flags |= IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.NAVIGATION_BAR;
			} else {
				flags |= IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			String generatedId = "org.eclipse.mylyn.web.browser-" + Calendar.getInstance().getTimeInMillis(); //$NON-NLS-1$
			IWebBrowser browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, generatedId, null, null);
			browser.openURL(url);
			return new BrowserHandle(browser);
		}
	}

	private static EditorHandle openUrlByHandler(final IWorkbenchPage page, final String location,
			final int customFlags) {
		for (final AbstractUrlHandler handler : UrlHandlerInitializer.handlers) {
			final AtomicReference<EditorHandle> result = new AtomicReference<EditorHandle>();
			SafeRunnable.run(new ISafeRunnable() {
				public void run() throws Exception {
					result.set(handler.openUrl(page, location, customFlags));
				}

				public void handleException(Throwable exception) {
					CommonsWorkbenchPlugin.getDefault()
							.getLog()
							.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
									NLS.bind("Unexpected error in {0} while opening URL ''{1}''", handler.getClass(), //$NON-NLS-1$
											location)));
				}
			});

			if (result.get() != null) {
				// success
				return result.get();
			}
		}
		return null;
	}

}
