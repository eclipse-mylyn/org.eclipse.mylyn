/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
