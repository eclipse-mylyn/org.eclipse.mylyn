/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestComposite;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Pull request adapter
 */
public class PullRequestAdapter extends WorkbenchAdapter {

	private final PullRequestCommitAdapter[] commits;

	/**
	 * Create pull request adapter
	 * 
	 * @param request
	 */
	public PullRequestAdapter(PullRequestComposite request) {
		List<PullRequestCommitAdapter> prCommits = new ArrayList<PullRequestCommitAdapter>();
		List<RepositoryCommit> requestCommits = request.getCommits();
		if (requestCommits != null)
			for (RepositoryCommit commit : requestCommits)
				prCommits.add(new PullRequestCommitAdapter(commit));
		commits = prCommits.toArray(new PullRequestCommitAdapter[prCommits
				.size()]);
	}

	@Override
	public Object[] getChildren(Object object) {
		return commits;
	}

}
