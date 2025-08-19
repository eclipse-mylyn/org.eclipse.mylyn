/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * Holds orphaned or uncategorized tasks for a given repository
 *
 * @author Rob Elves
 * @author Mik Kersten
 */
public class UnmatchedTaskContainer extends AutomaticRepositoryTaskContainer {

	private static final String HANDLE = "orphans"; //$NON-NLS-1$

	public UnmatchedTaskContainer(String connectorKind, String repositoryUrl) {
		super(HANDLE, connectorKind, repositoryUrl);
	}

	@Override
	public String getSummary() {
		return Messages.UnmatchedTaskContainer_Unmatched;
	}

}
