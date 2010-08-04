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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.commons.ui.team.RepositoryWizardPage;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizardPage extends RepositoryWizardPage {

	private IBuildServer model;

	private List<IBuildPlan> selectedPlans;

	public BuildServerWizardPage(String pageName) {
		super(pageName);
		setTitle("Build Server Properties");
		setElement(new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == RepositoryLocation.class) {
					return getModel().getLocation();
				}
				return null;
			}
		});
	}

	@Override
	protected BuildServerPart doCreateRepositoryPart() {
		BuildServerPart buildServerPart = new BuildServerPart(getModel());
		buildServerPart.initSelectedPlans(selectedPlans);
		return buildServerPart;
	}

	public IBuildServer getModel() {
		return model;
	}

	@Override
	public BuildServerPart getPart() {
		return (BuildServerPart) super.getPart();
	}

	public List<IBuildPlan> getSelectedPlans() {
		return getPart().getSelectedPlans();
	}

	public void init(IBuildServer model, List<IBuildPlan> selectedPlans) {
		this.model = model;
		this.selectedPlans = selectedPlans;
	}

}
