/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.tests.support;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.RepositoryProvider;

/**
 * @author Steffen Pingel
 */
public class MockRepositoryProvider extends RepositoryProvider {

	public static final String ID = "org.eclipse.mylyn.versions.tests.repository.mock";

	public MockRepositoryProvider() {
		// ignore
	}

	public void deconfigure() throws CoreException {
		// ignore

	}

	@Override
	public void configureProject() throws CoreException {
		// ignore

	}

	@Override
	public String getID() {
		return ID;
	}

}
