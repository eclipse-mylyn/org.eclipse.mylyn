/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * COPIED FROM: org.eclipse.search.internal.ui.text.SearchResultUpdater
 *
 * @author Shawn Minto
 */
public class XmlActiveSearchUpdater implements IResourceChangeListener, IQueryListener {
	private final FileSearchResult fResult;

	public XmlActiveSearchUpdater(FileSearchResult result) {
		fResult = result;
		NewSearchUI.addQueryListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			handleDelta(delta);
		}
	}

	private void handleDelta(IResourceDelta d) {
		try {
			d.accept(delta -> {
				switch (delta.getKind()) {
					case IResourceDelta.ADDED:
						return false;
					case IResourceDelta.REMOVED:
						IResource res = delta.getResource();
						if (res instanceof IFile) {
							Match[] matches = fResult.getMatches(res);
							fResult.removeMatches(matches);

//							for (Match matche : matches) {
//							 Match m = matches[j];
//							 XmlNodeHelper xnode =
//							 XmlJavaReferencesProvider.nodeMap.remove(m);
//							 System.out.println("REMOVED RES: " +
//							 xnode.getHandle());
//							 System.out.println(XmlJavaReferencesProvider.nodeMap);
//							}
						}
						break;
					case IResourceDelta.CHANGED:
						// TODO want to do something on chages to invalidate
						// handle changed resource
						break;
				}
				return true;
			});
		} catch (CoreException e) {
			StatusHandler.log(e.getStatus());
		}
	}

	@Override
	public void queryAdded(ISearchQuery query) {
		// don't care
	}

	@Override
	public void queryRemoved(ISearchQuery query) {
		if (fResult.equals(query.getSearchResult())) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
			NewSearchUI.removeQueryListener(this);
		}
	}

	@Override
	public void queryStarting(ISearchQuery query) {
		// don't care
	}

	@Override
	public void queryFinished(ISearchQuery query) {
		// don't care
	}
}
