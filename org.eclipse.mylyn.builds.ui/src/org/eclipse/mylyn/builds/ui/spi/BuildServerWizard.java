/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUiUtil;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.view.BuildsView;
import org.eclipse.mylyn.internal.commons.repositories.InMemoryCredentialsStore;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizard extends Wizard implements INewWizard {

	private IBuildServer model;

	private final IBuildServer original;

	public BuildServerWizard(IBuildServer server) {
		this.original = server;
		setNeedsProgressMonitor(true);
		if (isNew()) {
			setWindowTitle("New Build Server");
			setDefaultPageImageDescriptor(WorkbenchImages
					.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_NEW_WIZ));
		} else {
			setWindowTitle("Build Server Properties");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_SETTINGS);
		}
	}

	@Override
	public void addPages() {
		BuildServerWizardPage page = new BuildServerWizardPage("newBuildServer");
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
			if (workingCopy.getProperty(RepositoryLocation.PROPERTY_ID) == null) {
				workingCopy.setProperty(RepositoryLocation.PROPERTY_ID, UUID.randomUUID().toString());
			}
			workingCopy.setCredentialsStore(new InMemoryCredentialsStore(workingCopy.getCredentialsStore()));
			((BuildServer) model).setLocation(workingCopy);
		}
		return model;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public boolean performFinish() {
		((BuildServer) getModel()).applyToOriginal();
		original.getLocation().apply(getModel().getLocation());
		if (isNew()) {
			BuildsUiInternal.getModel().getServers().add(original);
		}

		updateSubscription(BuildsUiInternal.getModel());

		BuildsView.openInActivePerspective();

		return true;
	}

	/**
	 * Compare the set of selected plans with existing subscriptions and updates <code>model</code> accordingly.
	 */
	private void updateSubscription(IBuildModel model) {
		List<IBuildPlan> oldPlans = ((BuildModel) model).getPlans(original);
		List<IBuildPlan> selectedPlans = ((BuildServerWizardPage) getPages()[0]).getSelectedPlans();

		Set<String> oldPlanIds = BuildsUiUtil.toSetOfIds(oldPlans);
		HashSet<String> toRemovePlanIds = new HashSet<String>(oldPlanIds);
		Set<String> toAddPlanIds = BuildsUiUtil.toSetOfIds(selectedPlans);
		toRemovePlanIds.removeAll(toAddPlanIds);
		toAddPlanIds.removeAll(oldPlanIds);

		if (toAddPlanIds.size() > 0) {
			List<IBuildPlan> addPlans = new ArrayList<IBuildPlan>(toAddPlanIds.size());
			for (IBuildPlan plan : selectedPlans) {
				if (toAddPlanIds.contains(plan.getId())) {
					((BuildPlan) plan).setServer(original);
					addPlans.add(plan);
				}
			}
			model.getPlans().addAll(addPlans);
		}

		if (toRemovePlanIds.size() > 0) {
			List<IBuildPlan> removePlans = new ArrayList<IBuildPlan>(toRemovePlanIds.size());
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
