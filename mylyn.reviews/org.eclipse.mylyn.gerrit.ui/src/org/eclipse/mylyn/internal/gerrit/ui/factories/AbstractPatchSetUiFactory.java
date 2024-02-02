/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson, Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.mylyn.internal.reviews.ui.ReviewUiUtil;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
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
		return getPatchSetDetail(getModelObject());
	}

	protected PatchSetDetail getPatchSetDetail(IReviewItemSet set) {
		RemoteEmfConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> consumer = getGerritFactoryProvider()
				.getReviewItemSetFactory()
				.getConsumerForModel(getModelObject().getReview(), set);
		//This is not really a remote call, we're just ensuring we have the patch set detail available.
		if (consumer.getRemoteObject() == null) {
			try {
				consumer.pull(false, new NullProgressMonitor());
			} catch (CoreException e) {
				//We'll handle any issues downstream if we don't have a remote object
			}
		}
		return consumer.getRemoteObject();
	}

	protected GerritRemoteFactoryProvider getGerritFactoryProvider() {
		return (GerritRemoteFactoryProvider) getFactoryProvider();
	}

	protected GerritChange getChange() {
		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer = getGerritFactoryProvider()
				.getReviewFactory()
				.getConsumerForModel(getModelObject().getReview().getRepository(), getModelObject().getReview());
		GerritChange remoteObject = consumer.getRemoteObject();
		consumer.release();
		return remoteObject;
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
					boolean create = MessageDialog.openQuestion(getShell(),
							Messages.AbstractPatchSetUiFactory_Clone_Git_Repository,
							Messages.AbstractPatchSetUiFactory_Git_repository_not_found_in_workspace);
					if (create) {
						int response = EGitUiUtil.openCloneRepositoryWizard(getShell(), getTaskRepository(),
								mapper.getGerritProject());
						if (response == Window.OK && mapper.find() != null) {
							return mapper;
						}
					}
				}
			} else {
				String message = NLS.bind(Messages.AbstractPatchSetUiFactory_No_Git_repository_found_for_fetching,
						getTask().getTaskKey());
				String reason = NLS.bind(Messages.AbstractPatchSetUiFactory_No_remote_config_found_with_fetch_URL,
						mapper.getGerritHost(), mapper.getGerritProjectName());
				GerritCorePlugin.logError(message, null);
				ErrorDialog.openError(getShell(), Messages.AbstractPatchSetUiFactory_Gerrit_Fetch_Change_Error, message,
						new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, reason));
			}
		} catch (IOException e) {
			Status status = new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Error accessing Git repository", e); //$NON-NLS-1$
			StatusManager.getManager().handle(status, StatusManager.BLOCK | StatusManager.SHOW | StatusManager.LOG);
		}
		return null;
	}

	protected boolean isAnonymous() {
		return ReviewUiUtil.isAnonymous(getModelObject());
	}

	@Override
	protected boolean isExecutableStateKnown() {
		return isAnonymous()
				|| getChange() != null && getChange().getChangeDetail() != null && getPatchSetDetail() != null;
	}
}
