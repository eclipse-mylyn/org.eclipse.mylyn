/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class RefreshConfigRequest extends AbstractRequest<GerritConfiguration> {

	public RefreshConfigRequest() {
	}

	@Override
	protected GerritConfiguration execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.refreshConfig(monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Refreshing_Configuration;
	}

}
