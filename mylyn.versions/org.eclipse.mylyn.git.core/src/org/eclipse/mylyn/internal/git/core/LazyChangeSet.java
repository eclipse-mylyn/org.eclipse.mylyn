/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.git.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmUser;

class LazyChangeSet extends ChangeSet {
	private final GitRepository scmRepository;

	private final RevCommit commit;

	private List<Change> delegate;

	public LazyChangeSet(RevCommit r, GitRepository repository) {
		super(getScmUser(r.getCommitterIdent()), getAdjustedCommitTime(r), r.name(), r.getFullMessage(), repository,
				new ArrayList<>());
		commit = r;
		scmRepository = repository;
	}

	private static Date getAdjustedCommitTime(RevCommit r) {
		return new Date((long) r.getCommitTime() * 1000);
	}

	private static ScmUser getScmUser(PersonIdent person) {
		return new ScmUser(person.getEmailAddress(), person.getName(), person.getEmailAddress());
	}

	@Override
	public java.util.List<Change> getChanges() {
		return getOrInitDelegate();
	}

	private synchronized List<Change> getOrInitDelegate() {
		if (delegate == null) {
			delegate = new ArrayList<>();
			fetchChanges();
		}
		return delegate;

	}

	private void fetchChanges() {
		try {
			Repository repository = scmRepository.getRepository();
			RevWalk walk = new RevWalk(repository);

			delegate.addAll(
					((GitConnector) scmRepository.getConnector()).diffCommit(scmRepository, repository, walk, commit));
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}