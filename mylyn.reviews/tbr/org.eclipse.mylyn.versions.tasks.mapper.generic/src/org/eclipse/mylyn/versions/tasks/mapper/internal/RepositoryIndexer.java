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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexSearcher;

/**
 * 
 * @author Kilian Matt
 *
 */
public class RepositoryIndexer {

	private static final int STARTUP_DELAY_MILLIS = 10000;
	private IndexRepositoryJob repoSyncJob;

	public RepositoryIndexer() {
		repoSyncJob = new IndexRepositoryJob();
	}

	public void start() {
		repoSyncJob.schedule(STARTUP_DELAY_MILLIS);
	}

	public void stop() {
		repoSyncJob.cancel();
	}

	private static class IndexRepositoryJob extends Job {

		public IndexRepositoryJob() {
			super("Indexing repositories.");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IChangeSetIndexSearcher indexer = RepositoryIndexerPlugin.getDefault().getIndexer();
			((ChangeSetIndexer)indexer).reindex(monitor);
			
			return new Status(IStatus.OK, RepositoryIndexerPlugin.PLUGIN_ID,
					"Indexing finished");
		}

	}
	
}
