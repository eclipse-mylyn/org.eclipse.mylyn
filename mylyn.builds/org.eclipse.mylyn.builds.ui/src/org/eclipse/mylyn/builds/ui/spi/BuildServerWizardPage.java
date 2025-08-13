/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.ui.RepositoryWizardPage;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizardPage extends RepositoryWizardPage {

	private IBuildServer model;

	private List<IBuildPlan> selectedPlans;

	public BuildServerWizardPage(String pageName) {
		super(pageName);
		setTitle(Messages.BuildServerWizardPage_buildServerProperties);
		setElement(new IAdaptable() {
			@Override
			public <T> T getAdapter(Class<T> adapter) {
				if (adapter == RepositoryLocation.class) {
					return adapter.cast(getModel().getLocation());
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
