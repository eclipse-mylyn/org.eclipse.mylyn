/*******************************************************************************
 * Copyright (c) 2013 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.egit.GerritToGitMapping;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritRemoteFactoryProvider;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.gerrit.ui.egit.EGitUiUtil;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;

/**
 * @author Miles Parker
 * @author Steffen Pingel
 */
public abstract class AbstractPatchSetUiFactory extends AbstractUiFactory<IReviewItemSet> {

	public AbstractPatchSetUiFactory(String name, IUiContext context, IReviewItemSet object) {
		super(name, context, object);
	}

	protected PatchSetDetail getPatchSetDetail() {
		return getGerritFactoryProvider().getReviewItemSetFactory()
				.getConsumerForModel(getModelObject().getReview(), getModelObject())
				.getRemoteObject();
	}

	protected GerritRemoteFactoryProvider getGerritFactoryProvider() {
		return (GerritRemoteFactoryProvider) getFactoryProvider();
	}

	protected GerritChange getChange() {
		return getGerritFactoryProvider().getReviewFactory()
				.getConsumerForModel(getModelObject().getReview().getRepository(), getModelObject().getReview())
				.getRemoteObject();
	}

	protected final String getGerritProject(ChangeDetail changeDetail) {
		return changeDetail.getChange().getProject().get();
	}

	protected final Repository resolveGitRepository() {
		//Here we try to resolve the Git repository in the workspace for this Patch Set.  
		//If so, we will use the appropriate file revision to provide navigability in the Compare Editor.
		GerritToGitMapping mapper = getGitRepository(false);
		Repository gitRepository = null;
		if (mapper != null) {
			try {
				gitRepository = mapper.find();
			} catch (IOException e) {
				//If we cannot resolve the git repository, we will detect it later on.  Can be safely ignored
			}
		}
		return gitRepository;
	}

	protected final GerritToGitMapping getGitRepository(boolean displayCloneDialog) {
		ChangeDetail changeDetail = getChange().getChangeDetail();
		GerritConfigX config = GerritCorePlugin.getGerritClient(getTaskRepository()).getGerritConfig();
		GerritToGitMapping mapper = new GerritToGitMapping(getTaskRepository(), config, getGerritProject(changeDetail));
		try {
			if (mapper.find() != null) {
				return mapper;
			} else if (mapper.getGerritProject() != null) {
				if (displayCloneDialog) {
					boolean create = MessageDialog.openQuestion(getShell(), "Clone Git Repository",
							"The referenced Git repository was not found in the workspace. Clone Git repository?");
					if (create) {
						int response = EGitUiUtil.openCloneRepositoryWizard(getShell(), getTaskRepository(),
								mapper.getGerritProject());
						if (response == Window.OK && mapper.find() != null) {
							return mapper;
						}
					}
				}
			} else {
				String message = NLS.bind("No Git repository found for fetching Gerrit change {0}",
						getTask().getTaskKey());
				String reason = NLS.bind(
						"No remote config found that has fetch URL with host ''{0}'' and path matching ''{1}''",
						mapper.getGerritHost(), mapper.getGerritProjectName());
				GerritCorePlugin.logError(message, null);
				ErrorDialog.openError(getShell(), "Gerrit Fetch Change Error", message, new Status(IStatus.ERROR,
						GerritUiPlugin.PLUGIN_ID, reason));
			}
		} catch (IOException e) {
			Status status = new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Error accessing Git repository", e);
			StatusManager.getManager().handle(status, StatusManager.BLOCK | StatusManager.SHOW | StatusManager.LOG);
		}
		return null;
	}
}
