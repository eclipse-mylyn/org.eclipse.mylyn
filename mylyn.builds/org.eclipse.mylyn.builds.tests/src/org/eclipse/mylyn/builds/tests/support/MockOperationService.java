/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.support;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;
import org.eclipse.mylyn.builds.internal.core.operations.AbstractOperation;
import org.eclipse.mylyn.builds.internal.core.operations.IOperationService;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;

/**
 * @author Steffen Pingel
 */
public class MockOperationService implements IOperationService {

	private IStatus result;

	private final IBuildModelRealm realm;

	public MockOperationService(IBuildModelRealm realm) {
		this.realm = realm;
	}

	@Override
	public BuildScheduler getScheduler() {
		return null;
	}

	@Override
	public void handleResult(AbstractOperation operation, IStatus result) {
		this.result = result;
	}

	@Override
	public IBuildModelRealm getRealm() {
		return realm;
	}

	public IStatus getResult() {
		return result;
	}

}
