/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.io.File;

import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexSearcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * 
 * @author Kilian Matt
 * 
 */
public class RepositoryIndexerPlugin implements BundleActivator {
	public static final String PLUGIN_ID = "org.eclipse.mylyn.versions.tasks.mapper";
	private static RepositoryIndexerPlugin instance;

	private RepositoryIndexer synchronizer;
	private IChangeSetIndexSearcher indexSearch;

	public RepositoryIndexerPlugin() {
	}

	public static RepositoryIndexerPlugin getDefault() {
		return instance;
	}

	public void start(BundleContext context) throws Exception {
		RepositoryIndexerPlugin.instance = this;
		synchronizer = new RepositoryIndexer();
		synchronizer.start();
	}

	public void stop(BundleContext context) throws Exception {
		RepositoryIndexerPlugin.instance = null;
		synchronizer.stop();
	}

	public IChangeSetIndexSearcher getIndexer() {
		if (indexSearch == null) {
			initIndexer();
		}
		return indexSearch;
	}

	protected synchronized void initIndexer() {
		if (indexSearch == null) {
			File file = new EclipseIndexLocationProvider().getIndexLocation();
			indexSearch = new ChangeSetIndexer(file,
					new EclipseWorkspaceRepositorySource());
		}
	}


}
