/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.spi;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;

/**
 * Implementors of this class can contribute instances of {@link AbstractRepositoryConnector} at runtime.
 * 
 * @since 3.10
 */
public abstract class RepositoryConnectorContributor {

	/**
	 * Returns a list of descriptors for connectors that are to be contributed. This method is invoked when the tasks
	 * core framework is initialized.
	 * 
	 * @return a list of descriptors
	 */
	@NonNull
	public abstract List<RepositoryConnectorDescriptor> getDescriptors();

}
