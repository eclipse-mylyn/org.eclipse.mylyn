/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCacheFile;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Shawn Minto
 */
public class BugzillaReportCache {

	private Map<String, BugReport> cache = new HashMap<String, BugReport>();

	public void cache(String handle, BugReport report) {
		cache.put(handle, report);
		cacheFile.add(report);
	}

	public void clearCache() {
		cache.clear();
		cacheFile.removeAll();
	}

	public BugReport getFromCache(String bugHandle) {
		return cache.get(bugHandle);
	}

	public Set<String> getCachedHandles() {
		return cache.keySet();
	}

	private BugzillaCacheFile cacheFile;

	private IPath getCacheFile() {
		IPath stateLocation = Platform.getPluginStateLocation(MylarBugsPlugin.getDefault());
		IPath configFile = stateLocation.append("offlineReports");
		return configFile;
	}

	public void readCacheFile() {
		IPath cachPath = getCacheFile();

		try {
			cacheFile = new BugzillaCacheFile(cachPath.toFile());
			ArrayList<IBugzillaBug> cached = cacheFile.elements();
			for (IBugzillaBug bug : cached) {
				if (bug instanceof BugReport)
					cache.put(BugzillaTools.getHandle(bug), (BugReport) bug);
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "occurred while restoring saved offline Bugzilla reports.");
		}
	}

	public BugReport getCached(String handle) {
		return cache.get(handle);
	}
}
