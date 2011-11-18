/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

}
