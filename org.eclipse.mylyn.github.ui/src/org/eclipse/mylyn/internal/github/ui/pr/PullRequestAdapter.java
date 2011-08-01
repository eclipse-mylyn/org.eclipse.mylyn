/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.PullRequestCommit;
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
		List<PullRequestCommit> requestCommits = request.getCommits();
		if (requestCommits != null)
			for (PullRequestCommit commit : requestCommits)
				prCommits.add(new PullRequestCommitAdapter(commit));
		commits = prCommits.toArray(new PullRequestCommitAdapter[prCommits
				.size()]);
	}

	@Override
	public Object[] getChildren(Object object) {
		return commits;
	}

}
