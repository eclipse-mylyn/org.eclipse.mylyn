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

	public BuildScheduler getScheduler() {
		return null;
	}

	public void handleResult(AbstractOperation operation, IStatus result) {
		this.result = result;
	}

	public IBuildModelRealm getRealm() {
		return realm;
	}

	public IStatus getResult() {
		return result;
	}

}
