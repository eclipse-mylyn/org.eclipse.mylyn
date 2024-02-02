/*******************************************************************************
 * Copyright (c) 2010, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Added support for build service messages; bug 325079
 *     Itema AS - Automatic refresh when a new repo has been added; bug 330910
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.view.BuildsView;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class BuildServerWizard extends Wizard implements INewWizard {

	private IBuildServer model;

	private IBuildServer original;

	public BuildServerWizard(IBuildServer server) {
		original = server;
		setNeedsProgressMonitor(true);
		if (isNew()) {
			setWindowTitle("New Build Server");
			setDefaultPageImageDescriptor(
					WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_NEW_WIZ));
		} else {
			setWindowTitle("Build Server Properties");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_SETTINGS);
		}
	}

	/**
	 * We use this method when the wizard instance has been created by means of the extension point mechanism and need to set initial build
	 * server data.
	 *
	 * @param server
	 *            the build server
	 */
	public void setBuildServer(IBuildServer server) {
		original = server;
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_SETTINGS);
	}

	@Override
	public void addPages() {
		BuildServerWizardPage page = new BuildServerWizardPage("newBuildServer"); //$NON-NLS-1$
		page.init(getModel(), getSelectedPlans());
		initPage(page);
		addPage(page);
	}

	protected void initPage(BuildServerWizardPage page) {
		// ignore
	}

	private List<IBuildPlan> getSelectedPlans() {
		return BuildsUiInternal.getModel().getPlans(original);
	}

	public IBuildServer getModel() {
		if (model == null) {
			model = ((BuildServer) original).createWorkingCopy();
			RepositoryLocation workingCopy = new RepositoryLocation(original.getLocation());
			// use an in memory credentials store that is backed by the actual credentials store
			workingCopy.setCredentialsStore(new InMemoryCredentialsStore(workingCopy.getCredentialsStore()));
			((BuildServer) model).setLocation(workingCopy);
		}
		return model;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public boolean performFinish() {
		((BuildServer) getModel()).applyToOriginal();
		getModel().getLocation().setIdPreservingCredentialsStore(getModel().getLocation().getUrl());
		original.getLocation().apply(getModel().getLocation());
		if (isNew()) {
			BuildsUiInternal.getModel().getServers().add(original);
		}
		updateSubscription(BuildsUiInternal.getModel());
		BuildsView.openInActivePerspective();

		BuildsUiInternal.getFactory().getRefreshOperation(original).execute();
		try {
			BuildsUiInternal.save();
		} catch (IOException e) {
			StatusManager.getManager()
					.handle(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Unexpected error while saving builds",
							e));
		}

		return true;
	}

	/**
	 * Compare the set of selected plans with existing subscriptions and updates <code>model</code> accordingly.
	 */
	private void updateSubscription(IBuildModel model) {
		List<IBuildPlan> oldPlans = ((BuildModel) model).getPlans(original);
		List<IBuildPlan> selectedPlans = ((BuildServerWizardPage) getPages()[0]).getSelectedPlans();

		Set<String> oldPlanIds = BuildsUiInternal.toSetOfIds(oldPlans);
		HashSet<String> toRemovePlanIds = new HashSet<>(oldPlanIds);
		Set<String> toAddPlanIds = BuildsUiInternal.toSetOfIds(selectedPlans);
		toRemovePlanIds.removeAll(toAddPlanIds);
		toAddPlanIds.removeAll(oldPlanIds);

		if (toAddPlanIds.size() > 0) {
			List<IBuildPlan> addPlans = new ArrayList<>(toAddPlanIds.size());
			for (IBuildPlan plan : selectedPlans) {
				if (toAddPlanIds.contains(plan.getId())) {
					((BuildPlan) plan).setServer(original);
					addPlans.add(plan);
				}
			}
			model.getPlans().addAll(addPlans);
		}

		if (toRemovePlanIds.size() > 0) {
			List<IBuildPlan> removePlans = new ArrayList<>(toRemovePlanIds.size());
			for (IBuildPlan plan : oldPlans) {
				if (toRemovePlanIds.contains(plan.getId())) {
					removePlans.add(plan);
				}
			}
			model.getPlans().removeAll(removePlans);
		}
	}

	private boolean isNew() {
		return ((BuildServer) original).eContainer() != BuildsUiInternal.getModel();
	}

}
