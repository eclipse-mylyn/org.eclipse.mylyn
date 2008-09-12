/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class UnsubmittedTaskContainer extends AutomaticRepositoryTaskContainer {

	private static final String LABEL = "Unsubmitted";

	private static final String HANDLE = "unsubmitted";

	public UnsubmittedTaskContainer(String connectorKind, String repositoryUrl) {
		super(repositoryUrl + "-" + HANDLE, connectorKind, repositoryUrl);
	}

	@Override
	public String getSummaryLabel() {
		return LABEL;
	}

	@Override
	protected String getHandleSuffix() {
		return HANDLE;
	}
}
