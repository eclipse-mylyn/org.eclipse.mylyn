/*******************************************************************************
 * Copyright (c) 2013, Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastien Dubois (Ericsson) - Adapted to use with Mylyn Reviews
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

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
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.internal.reviews.ui.compare.CompareUtil;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;

/**
 * A collection of common utility functions used to resolve the Git File Revisions
 * 
 * @author Sebastien Dubois
 */
public class GitFileRevisionUtils {

	//Needed to identify this plug-in, since there is no activator for it.
	private static String PLUGIN_ID = "org.eclipse.mylyn.reviews.core"; //$NON-NLS-1$

	public static org.eclipse.team.core.history.IFileRevision getFileRevision(final Repository repository,
			final IFileRevision reviewFileRevision) {
		org.eclipse.team.core.history.IFileRevision gitFileRevision = null;

		if (reviewFileRevision != null && reviewFileRevision.getPath() != null) {
			//Get SHA-1 for the file revision to look for the correct file revision in the Git repository
			ObjectInserter inserter = repository.newObjectInserter();
			String id = inserter.idFor(Constants.OBJ_BLOB, CompareUtil.getContent(reviewFileRevision)).getName();
			inserter.release();
			if (id != null) {
				final ObjectId objId = ObjectId.fromString(id);
				if (objId != null) {
					final IPath path = Path.fromPortableString(reviewFileRevision.getPath());
					gitFileRevision = new org.eclipse.team.core.history.provider.FileRevision() {

						public org.eclipse.team.core.history.IFileRevision withAllProperties(IProgressMonitor monitor)
								throws CoreException {
							return this;
						}

						public boolean isPropertyMissing() {
							return false;
						}

						public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
							return getFileRevisionStorage(null, repository, path, objId);
						}

						public String getName() {
							return path.lastSegment();
						}
					};
				}
			}
		}
		return gitFileRevision;
	}

	private static IStorage getFileRevisionStorage(final IProgressMonitor monitor, final Repository repository,
			final IPath path, final ObjectId objId) {

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
				//Here we append the object Id to the path to distinguish it from the path of this file revision 
				//from the  workspace file.  This is  needed to get good AST resolution and navigability.
				return path.append(Path.fromPortableString(objId.getName()));
			}

			public InputStream getContents() throws CoreException {
				InputStream in = null;
				try {
					in = getBlobContent(monitor, repository, objId);
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
				}
				return in;
			}
		};
	}

	private static InputStream getBlobContent(final IProgressMonitor monitor, final Repository repository,
			final ObjectId objId) throws CoreException {
		InputStream resStream = null;
		try {
			resStream = repository.open(objId, Constants.OBJ_BLOB).openStream();
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
		}
		return resStream;
	}
}
