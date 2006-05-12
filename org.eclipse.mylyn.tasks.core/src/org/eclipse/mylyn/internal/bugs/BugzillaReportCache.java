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
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCacheFile;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;

/**
 * @author Shawn Minto
 */
public class BugzillaReportCache {

	private Map<String, BugzillaReport> cache = new HashMap<String, BugzillaReport>();

	public void cache(String handle, BugzillaReport report) {
		cache.put(handle, report);
		cacheFile.add(report);
	}

	public void clearCache() {
		cache.clear();
		cacheFile.removeAll();
	}

	public BugzillaReport getFromCache(String bugHandle) {
		return cache.get(bugHandle);
	}

	public Set<String> getCachedHandles() {
		return cache.keySet();
	}

	private BugzillaCacheFile cacheFile;

	private IPath getCacheFile() {
		IPath stateLocation = Platform.getStateLocation(MylarBugsPlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("offlineReports");
		return configFile;
	}

	public void readCacheFile() {
		IPath cachPath = getCacheFile();

		try {
			cacheFile = new BugzillaCacheFile(cachPath.toFile());
			ArrayList<BugzillaReport> cached = cacheFile.elements();
			for (BugzillaReport bug : cached) {
				if (bug instanceof BugzillaReport)
					cache.put(BugzillaTools.getHandle(bug), (BugzillaReport) bug);
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "occurred while restoring saved offline Bugzilla reports.");
		}
	}

	public BugzillaReport getCached(String handle) {
		return cache.get(handle);
	}
}
