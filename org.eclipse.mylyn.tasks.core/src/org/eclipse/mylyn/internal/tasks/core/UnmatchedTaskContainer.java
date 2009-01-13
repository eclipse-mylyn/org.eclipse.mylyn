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
 * Holds orphaned or uncategorized tasks for a given repository
 * 
 * @author Rob Elves
 * @author Mik Kersten
 */
public class UnmatchedTaskContainer extends AutomaticRepositoryTaskContainer {

	public static final String LABEL = Messages.UnmatchedTaskContainer_Unmatched;

	public static final String HANDLE = "orphans"; //$NON-NLS-1$

	public UnmatchedTaskContainer(String connectorKind, String repositoryUrl) {
		super(repositoryUrl + "-" + HANDLE, connectorKind, repositoryUrl); //$NON-NLS-1$
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
