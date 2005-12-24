/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.repositories;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManager {
	
	private List<ITaskRepositoryClient> repositories = new ArrayList<ITaskRepositoryClient>();

//	private List<String> types = new ArrayList<String>();
	
	public List<ITaskRepositoryClient> getRepositoryClients() {
		return repositories;
	}
	
	public void addRepositoryClient(ITaskRepositoryClient repositoryClient) {
		if (!repositories.contains(repositoryClient)) {
			repositories.add(repositoryClient);
		}
	}
	
	public void removeRepositoryClient(ITaskRepositoryClient repositoryClient) {
		repositories.remove(repositoryClient);
	}
}
