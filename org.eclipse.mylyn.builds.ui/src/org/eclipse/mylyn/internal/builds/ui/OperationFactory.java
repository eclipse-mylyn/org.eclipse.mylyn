/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.operations.AbortBuildOperation;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
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

	public AbortBuildOperation getAbortBuildOperation(IBuild build) {
		return new AbortBuildOperation(service, build);
	}

	public GetBuildsOperation getGetBuildsOperation(GetBuildsRequest request) {
		return new GetBuildsOperation(service, request);
	}

	public IOperationService getService() {
		return service;
	}

}
