/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;

/**
 * 
 * @author mattk
 *
 */
public class ChangesetScopeItem implements IReviewScopeItem {

	private String revisionId;
	private String repositoryUrl;

	public ChangesetScopeItem(String revisionId, String repositoryUrl) {
		super();
		this.revisionId = revisionId;
		this.repositoryUrl = repositoryUrl;
	}

	@Override
	public List<IReviewFile> getReviewFiles(NullProgressMonitor monitor)
			throws CoreException {
		for (ScmConnector connector : ScmCore.getAllRegisteredConnectors()) {
			for (ScmRepository repository : connector
					.getRepositories(new NullProgressMonitor())) {
				if (repositoryUrl.equals(repository.getUrl())) {
					ChangeSet changeset = connector.getChangeset(repository,
							new IFileRevision() {

								@Override
								public IStorage getStorage(
										IProgressMonitor monitor)
										throws CoreException {
									return null;
								}

								@Override
								public String getName() {
									return null;
								}

								@Override
								public URI getURI() {
									return null;
								}

								@Override
								public long getTimestamp() {
									return 0;
								}

								@Override
								public boolean exists() {
									return false;
								}

								@Override
								public String getContentIdentifier() {
									return revisionId;
								}

								@Override
								public String getAuthor() {
									return null;
								}

								@Override
								public String getComment() {
									// TODO Auto-generated method stub
									return null;
								}

								@Override
								public ITag[] getTags() {
									// TODO Auto-generated method stub
									return null;
								}

								@Override
								public boolean isPropertyMissing() {
									// TODO Auto-generated method stub
									return false;
								}

								@Override
								public IFileRevision withAllProperties(
										IProgressMonitor monitor)
										throws CoreException {
									// TODO Auto-generated method stub
									return null;
								}
							}, monitor);
					List<IReviewFile> list = new ArrayList<IReviewFile>();
					for (Change change : changeset.getChanges()) {
						list.add(new ChangeSetReviewFile(change));
					}
					return list;

				}
			}

		}
		return new ArrayList<IReviewFile>();
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getRevisionId() {
		return revisionId;
	}

	@Override
	public String getDescription() {
		return "Changeset " + revisionId;
	}

	@Override
	public String getType(int count) {
		return count == 1 ? "changeset" : "changesets";
	}

}
