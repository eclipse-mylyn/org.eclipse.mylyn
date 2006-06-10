/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.provisional.tasklist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.tasklist.AbstractAttributeFactory;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface IOfflineTaskHandler {
	
	public AbstractAttributeFactory getAttributeFactory();
	
	public RepositoryTaskData downloadTaskData(AbstractRepositoryTask repositoryTask) throws CoreException;
	
}
