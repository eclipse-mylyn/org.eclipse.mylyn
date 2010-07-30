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

import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.RepositoryValidator;
import org.eclipse.mylyn.internal.builds.ui.BuildServerValidator;
import org.eclipse.mylyn.internal.commons.ui.SectionComposite;
import org.eclipse.mylyn.internal.commons.ui.team.RepositoryLocationPart;

public class BuildServerPart extends RepositoryLocationPart {

	private final IBuildServer model;

	public BuildServerPart(IBuildServer model) {
		super(model.getLocation());
		this.model = model;
	}

	public final IBuildServer getModel() {
		return model;
	}

	@Override
	public boolean canValidate() {
		return true;
	}

	@Override
	protected void createSections(SectionComposite sectionComposite) {
		sectionComposite.createSection("Build Plans");
	}

	@Override
	protected RepositoryValidator getValidator() {
		return new BuildServerValidator(getModel());
	}

}
