/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Provides information about a {@link TaskRepository} such as the version of the repository.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @see AbstractRepositoryConnector#validate(TaskRepository, org.eclipse.core.runtime.IProgressMonitor)
 * @since 3.11
 */
public class RepositoryInfo {

	private final RepositoryVersion version;

	public RepositoryInfo(@NonNull RepositoryVersion version) {
		this.version = checkNotNull(version);
	}

	@NonNull
	public RepositoryVersion getVersion() {
		return version;
	}

}
