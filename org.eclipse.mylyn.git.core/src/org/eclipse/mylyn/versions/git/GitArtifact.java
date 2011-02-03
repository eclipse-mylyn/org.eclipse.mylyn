/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.git;

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
 * @author mattk
 *
 */
public class GitArtifact extends ScmArtifact {
	private GitRepository repository;

	public GitArtifact(String id, String path, GitRepository repository) {
		super(id, path);
		this.repository = repository;
		setPath(path);
		setId(id);
	}

	@Override
	public IFileRevision getFileRevision(IProgressMonitor monitor) {
		try {
			final IPath path = Path.fromPortableString(getPath());
			return new FileRevision() {

				@Override
				public IFileRevision withAllProperties(IProgressMonitor monitor)
						throws CoreException {
					return this;
				}

				@Override
				public boolean isPropertyMissing() {
					return false;
				}

				@Override
				public IStorage getStorage(IProgressMonitor monitor)
						throws CoreException {
					return new IStorage() {

						@SuppressWarnings("rawtypes")
						@Override
						public Object getAdapter(Class adapter) {
							return null;
						}

						@Override
						public boolean isReadOnly() {
							return true;
						}

						@Override
						public String getName() {
							return path.lastSegment();
						}

						@Override
						public IPath getFullPath() {
							return path;
						}

						@Override
						public InputStream getContents() throws CoreException {
							try {
								return repository
										.getRepository()
										.open(ObjectId.fromString(getId()),
												Constants.OBJ_BLOB)
										.openStream();
							} catch (Exception e) {
								e.printStackTrace();
								throw new CoreException(new Status(
										IStatus.ERROR, GitConnector.ID,
										e.getMessage()));
							}
						}
					};
				}

				@Override
				public String getName() {
					return path.lastSegment();
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
