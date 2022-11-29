/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology
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

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileRevision;

/**
 * ScmArtifact implementation for git
 * 
 * @author mattk
 */
public class GitArtifact extends ScmArtifact {

	private final GitRepository repository;

	public GitArtifact(String id, String path, GitRepository repository) {
		super(id, path);
		this.repository = repository;
	}

	@Override
	public IFileRevision getFileRevision(IProgressMonitor monitor) {
		try {
			final IPath path = Path.fromPortableString(getPath());
			return new FileRevision() {

				public IFileRevision withAllProperties(IProgressMonitor monitor) throws CoreException {
					return this;
				}

				public boolean isPropertyMissing() {
					return false;
				}

				public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
					return new IStorage() {

						@SuppressWarnings("rawtypes")
						public Object getAdapter(Class adapter) {
							return null;
						}

						public boolean isReadOnly() {
							return true;
						}

						public String getName() {
							return path.lastSegment();
						}

						public IPath getFullPath() {
							return path;
						}

						public InputStream getContents() throws CoreException {
							try {
								return repository.getRepository()
										.open(ObjectId.fromString(getId()), Constants.OBJ_BLOB)
										.openStream();
							} catch (Exception e) {
								e.printStackTrace();
								throw new CoreException(
										new Status(IStatus.ERROR, GitConnector.PLUGIN_ID, e.getMessage()));
							}
						}
					};
				}

				public String getName() {
					return path.lastSegment();
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public IFileRevision[] getContributors(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFileRevision[] getTargets(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

}
