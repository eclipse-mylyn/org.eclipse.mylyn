/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * Caches {@link ITracClient} objects.
 * 
 * @author Steffen Pingel
 */
public class TracClientManager {

	Map<String, ITracClient> clientByUrl = new HashMap<String, ITracClient>();

	public TracClientManager() {
	}

	public ITracClient getRepository(TaskRepository taskRepository) throws MalformedURLException {
		ITracClient repository = clientByUrl.get(taskRepository.getUrl());
		if (repository == null) {
			repository = TracClientFactory.createClient(taskRepository.getUrl(), Version.fromVersion(taskRepository
					.getVersion()), taskRepository.getUserName(), taskRepository.getPassword());
			// TODO need to get notified when task repositories are removed or
			// settings are changed therefore disable caching for now
			// clientByUrl.put(taskRepository.getUrl(), repository);
		}
		return repository;
	}
}
