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

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritPatchSetContent;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.PatchSetDetail;

/**
 * @author Steffen Pingel
 */
public class GetPatchSetContentJob extends Job {

	private GerritPatchSetContent patchSetContent;

	private final PatchSetDetail patchSetDetail;

	private final TaskRepository repository;

	public GetPatchSetContentJob(TaskRepository repository, PatchSetDetail patchSetDetail) {
		super("Caching Patch Set Content");
		this.repository = repository;
		this.patchSetDetail = patchSetDetail;
	}

	public GerritPatchSetContent getPatchSetContent() {
		return patchSetContent;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		GerritConnector connector = (GerritConnector) TasksUi.getRepositoryConnector(repository.getConnectorKind());
		GerritClient client = connector.getClient(repository);
		try {
			int reviewId = patchSetDetail.getInfo().getKey().getParentKey().get();
			patchSetContent = client.getPatchSetContent(reviewId + "", null, patchSetDetail, monitor); //$NON-NLS-1$
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} catch (GerritException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Review retrieval failed", e),
					StatusManager.LOG);
		}
		return Status.OK_STATUS;
	}

}