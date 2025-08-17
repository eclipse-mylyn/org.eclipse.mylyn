/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson, Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.egit.ui.internal.commit.CommitEditor;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.egit.GerritToGitMapping;
import org.eclipse.mylyn.internal.gerrit.ui.editor.FetchPatchSetJob;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.swt.widgets.Display;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class OpenCommitUiFactory extends AbstractPatchSetUiFactory {

	public OpenCommitUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.OpenCommitUiFactory_Open_Commit, context, set);
	}

	@Override
	public void execute() {
		GerritToGitMapping mapping = getGitRepository(true);
		if (mapping != null) {
			final FetchPatchSetJob job = new FetchPatchSetJob(Messages.OpenCommitUiFactory_Opening_Commit_Viewer,
					mapping.getRepository(), mapping.getRemote(), getPatchSetDetail().getPatchSet());
			job.schedule();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					Display.getDefault().asyncExec(() -> CommitEditor.openQuiet(job.getCommit()));
				}
			});
		}
	}

	@Override
	public boolean isExecutable() {
		ChangeDetailX changeDetail = getChange().getChangeDetail();
		return changeDetail != null && changeDetail.getPatchSets().size() > 1;
	}
}
