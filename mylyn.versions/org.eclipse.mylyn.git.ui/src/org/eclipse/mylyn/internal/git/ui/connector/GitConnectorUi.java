/*******************************************************************************
 * Copyright (c) 2011, 2015 Ericsson Research Canada and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Ericsson - Initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.git.ui.connector;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.GitProvider;
import org.eclipse.egit.ui.internal.commit.CommitEditor;
import org.eclipse.egit.ui.internal.commit.RepositoryCommit;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.git.core.GitRepository;
import org.eclipse.mylyn.internal.git.ui.GetChangeSetDialog;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.ui.spi.ScmConnectorUi;

/**
 * Entry class to resolve the generic versions components from a User interface.
 * 
 * @author Alvaro Sanchez-Leon
 */
public class GitConnectorUi extends ScmConnectorUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.git.ui"; //$NON-NLS-1$

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IResource resource) {
		Assert.isNotNull(resource);

		final IProject project = resource.getProject();
		Assert.isNotNull(project);

		// Resolve Git Scm connector
		final ScmConnector scmConnector = ScmCore.getConnector(resource);
		Assert.isNotNull(scmConnector);

		// Check if the provider is for Git
		if (!GitProvider.class.getName().equals(scmConnector.getProviderId())) {
			throw new RuntimeException("No Git connector: " + scmConnector.getProviderId());
		}

		final GetChangeSetDialog dialog = new GetChangeSetDialog(null, project);
		final int result = dialog.open();
		if (result == Window.OK) {
			return dialog.getChangeSet();
		} // else Window.CANCEL
		return null;
	}

	@Override
	public void showChangeSetInView(ChangeSet cs) {
		String objectId = cs.getId();
		GitRepository repo = (GitRepository) cs.getRepository();
		Repository repository = repo.getRepository();
		CommitEditor.openQuiet(new RepositoryCommit(repository, getCommit(repository, objectId)));
	}

	private RevCommit getCommit(Repository repository, String objectId) {
		RevWalk revWalk = null;
		try {
			revWalk = new RevWalk(repository);
			return revWalk.parseCommit(ObjectId.fromString(objectId));
		} catch (Exception e) {
			return null;
		} finally {
			if (revWalk != null) {
				release(revWalk);
			}
		}
	}

	private void release(RevWalk revWalk) {
		try {
			MethodUtils.invokeMethod(revWalk, "release", null); //$NON-NLS-1$
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			try {
				MethodUtils.invokeMethod(revWalk, "close", null); //$NON-NLS-1$
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN, "Failed to release revWalk " + revWalk, e1)); //$NON-NLS-1$
			}
		}
	}
}
