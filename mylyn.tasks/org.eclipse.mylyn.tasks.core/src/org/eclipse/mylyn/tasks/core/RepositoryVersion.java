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
 * Represents the version of a {@link TaskRepository}.
 *
 * @since 3.11
 * @noextend This class is not intended to be subclassed by clients.
 */
// FIXME bug 425593 make this class more generic
public class RepositoryVersion {

	private final String version;

	public RepositoryVersion(@NonNull String version) {
		this.version = checkNotNull(version);
	}

	@Override
	public String toString() {
		return version;
	}

}
