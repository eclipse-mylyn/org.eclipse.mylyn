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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.commons.ui.repositories.RepositoryWizardPage;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizardPage extends RepositoryWizardPage {

	private IBuildServer model;

	public BuildServerWizardPage(String pageName) {
		super(pageName);
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
		return new BuildServerPart(getModel());
	}

	public void setModel(IBuildServer model) {
		this.model = model;
	}

	public IBuildServer getModel() {
		return model;
	}

}
