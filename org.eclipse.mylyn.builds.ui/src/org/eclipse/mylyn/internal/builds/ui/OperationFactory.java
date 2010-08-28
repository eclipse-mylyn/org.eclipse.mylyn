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

package org.eclipse.mylyn.internal.builds.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.operations.IOperationService;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.builds.internal.core.operations.RunBuildOperation;

/**
 * @author Steffen Pingel
 */
public class OperationFactory {

	private final IOperationService service;

	public OperationFactory(IOperationService service) {
		this.service = service;
	}

	public RefreshOperation getRefreshOperation() {
		return new RefreshOperation(service, BuildsUiInternal.getModel());
	}

	public RefreshOperation getRefreshOperation(IBuildElement element) {
		return new RefreshOperation(service, BuildsUiInternal.getModel(), Collections.singletonList(element));
	}

	public RefreshOperation getRefreshOperation(List<IBuildElement> elements) {
		return new RefreshOperation(service, BuildsUiInternal.getModel(), elements);
	}

	public RunBuildOperation getRunBuildOperation(RunBuildRequest request) {
		return new RunBuildOperation(service, request);
	}

}
