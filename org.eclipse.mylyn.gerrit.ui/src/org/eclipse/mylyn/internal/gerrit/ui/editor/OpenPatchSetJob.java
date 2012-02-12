/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.ReviewItemCache;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritPatchSetContent;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 */
public class OpenPatchSetJob extends Job {

	private final TaskRepository repository;

	private final ChangeDetail changeDetail;

	private final PatchSet base;

	private final PatchSet target;

	private final ITask task;

	private final ReviewItemCache cache;

	public OpenPatchSetJob(TaskRepository repository, ITask task, ChangeDetail changeDetail, PatchSet base,
			PatchSet target, ReviewItemCache cache) {
		super("Opening Patch Set");
		this.repository = repository;
		this.task = task;
		this.changeDetail = changeDetail;
		this.base = base;
		this.target = target;
		this.cache = cache;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		GerritConnector connector = (GerritConnector) TasksUi.getRepositoryConnector(repository.getConnectorKind());
		GerritClient client = connector.getClient(repository);
		try {
			PatchSetDetail targetDetail = client.getPatchSetDetail((base != null) ? base.getId() : null,
					target.getId(), monitor);

			int reviewId = targetDetail.getInfo().getKey().getParentKey().get();
			GerritPatchSetContent patchSetContent = client.getPatchSetContent(
					reviewId + "", base, targetDetail, monitor); //$NON-NLS-1$

			final IReviewItemSet items = GerritUtil.createInput(changeDetail, patchSetContent, cache);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					doOpen(items);
				}
			});
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} catch (GerritException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Review retrieval failed", e),
					StatusManager.LOG);
		}
		return Status.OK_STATUS;
	}

	private void doOpen(IReviewItemSet items) {
		CompareConfiguration configuration = new CompareConfiguration();
		CompareUI.openCompareEditor(new ReviewItemSetCompareEditorInput(configuration, items, null,
				new GerritReviewBehavior(task)));
	}

}
