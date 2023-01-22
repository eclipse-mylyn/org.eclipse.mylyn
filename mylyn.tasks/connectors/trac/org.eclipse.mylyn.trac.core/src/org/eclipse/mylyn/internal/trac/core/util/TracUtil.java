/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     Jorrit Schippers - fix for bug 254862
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;

/**
 * Provides static helper methods.
 * 
 * @author Steffen Pingel
 */
public class TracUtil {

	public static Date parseDate(String time) {
		if (time != null) {
			try {
				return TracUtil.parseDate(Long.valueOf(time));
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	public static Date parseDate(long seconds) {
		return new Date(seconds * 1000l);
//		Calendar c = Calendar.getInstance();
//		c.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
//		c.setTimeInMillis(seconds * 1000l);
//		return c.getTime();
	}

	public static long toTracTime(Date date) {
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
//		return c.getTimeInMillis() / 1000l;
		return date.getTime() / 1000l;
	}

	private static String getQueryParameter(IRepositoryQuery query) {
		String url = query.getUrl();
		int i = url.indexOf(ITracClient.QUERY_URL);
		if (i == -1) {
			return null;
		}
		return url.substring(i + ITracClient.QUERY_URL.length());
	}

	/**
	 * Creates a <code>TracSearch</code> object from this query.
	 */
	public static TracSearch toTracSearch(IRepositoryQuery query) {
		String url = getQueryParameter(query);
		if (url != null) {
			TracSearch search = new TracSearch();
			search.fromUrl(url);
			return search;
		}
		return null;
	}

	public static IStatus createPermissionDeniedError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_PERMISSION_DENIED, Messages.TracUtil_Permission_denied);
	}

	public static String encodeUrl(String string) {
		try {
			return URLEncoder.encode(string, ITracClient.CHARSET).replaceAll("\\+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}

}
