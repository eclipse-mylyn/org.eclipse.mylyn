/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sun.misc.BASE64Encoder;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.trac";

	public final static String REPOSITORY_KIND = "trac";

	public final static String TITLE_MESSAGE_DIALOG = "Mylar Trac Client";

	private static TracUiPlugin plugin;

	public TracUiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static TracUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Copied from BugzillaPlugin.
	 * 
	 * TODO refactor into common utility method
	 */
	public static HttpURLConnection getHttpConnection(URL url) throws IOException, NoSuchAlgorithmException,
			KeyManagementException, MalformedURLException {
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		URLConnection cntx = getUrlConnection(url, proxySettings);
		if (cntx == null || !(cntx instanceof HttpURLConnection)) {
			throw new MalformedURLException();
		}

		HttpURLConnection connection = (HttpURLConnection) cntx;
		return connection;
	}

	/**
	 * Copied from BugzillaPlugin.getUrlConnection().
	 * 
	 * TODO refactor into common utility method
	 */
	public static URLConnection getUrlConnection(URL url, Proxy proxy) throws IOException, NoSuchAlgorithmException,
			KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("TLS");

		javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[] { new TrustAll() };
		ctx.init(null, tm, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		URLConnection connection = url.openConnection(proxy);
		return connection;
	}

	public static void setAuthCredentials(URLConnection connection, String username, String password) {
		byte[] credentials = (username + ":" + password).getBytes();
		BASE64Encoder encoder = new BASE64Encoder();
		connection.setRequestProperty("Authorization", "Basic " + encoder.encode(credentials));
	}

	public static void handleTracException(Throwable e) {
		if (e instanceof TracLoginException) {
			MessageDialog.openError(null, TITLE_MESSAGE_DIALOG,
					"Your login name or password is incorrect. Ensure proper repository configuration in "
							+ TaskRepositoriesView.NAME + ".");
		} else if (e instanceof TracException) {
			MessageDialog.openError(null, TITLE_MESSAGE_DIALOG, "Connection Error: " + e.getMessage());
		} else if (e instanceof ClassCastException) {
			MessageDialog.openError(null, TITLE_MESSAGE_DIALOG, "Error parsing server response");
		} else {
			MylarStatusHandler.fail(e, "Unexpected error: " + e.getMessage(), true);
		}
	}
}
