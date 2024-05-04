/*******************************************************************************
 * Copyright (c) 2013, 2015, Ericsson AB and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastien Dubois (Ericsson) - Adapted to use with Mylyn Reviews
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.compare.CompareUtil;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.team.core.history.IFileRevision;

/**
 * A collection of common utility functions used to resolve the Git File Revisions
 *
 * @author Sebastien Dubois
 */
public class GitFileRevisionUtils {

	//Needed to identify this plug-in, since there is no activator for it.
	private static String PLUGIN_ID = "org.eclipse.mylyn.reviews.core"; //$NON-NLS-1$

	public static IFileRevision getFileRevision(final Repository repository, final IFileVersion reviewFileVersion) {
		IFileRevision gitFileRevision = null;

		if (reviewFileVersion != null && reviewFileVersion.getPath() != null) {
			//Get SHA-1 for the file revision to look for the correct file revision in the Git repository
			ObjectInserter inserter = repository.newObjectInserter();
			String id = inserter.idFor(Constants.OBJ_BLOB, CompareUtil.getContent(reviewFileVersion)).getName();
			release(inserter);
			if (id != null) {
				final ObjectId objId = ObjectId.fromString(id);
				if (objId != null) {
					final IPath path = Path.fromPortableString(reviewFileVersion.getPath());
					gitFileRevision = new org.eclipse.team.core.history.provider.FileRevision() {

						@Override
						public IFileRevision withAllProperties(IProgressMonitor monitor) throws CoreException {
							return this;
						}

						@Override
						public boolean isPropertyMissing() {
							return false;
						}

						@Override
						public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
							return getFileRevisionStorage(null, repository, path, objId);
						}

						@Override
						public String getName() {
							return path.lastSegment();
						}
					};
				}
			}
		}
		return gitFileRevision;
	}

	private static void release(ObjectInserter inserter) {
		try {
			MethodUtils.invokeMethod(inserter, "release", null); //$NON-NLS-1$
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			try {
				MethodUtils.invokeMethod(inserter, "close", null); //$NON-NLS-1$
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
						"Failed to release inserter " + inserter, e1)); //$NON-NLS-1$
			}
		}
	}

	private static IStorage getFileRevisionStorage(final IProgressMonitor monitor, final Repository repository,
			final IPath path, final ObjectId objId) {

		return new IStorage() {
			@Override
			public <T> T getAdapter(Class<T> adapter) {
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
				//Here we append the object Id to the path to distinguish it from the path of this file revision
				//from the  workspace file.  This is  needed to get good AST resolution and navigability.
				return path.append(Path.fromPortableString(objId.getName()));
			}

			@Override
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
