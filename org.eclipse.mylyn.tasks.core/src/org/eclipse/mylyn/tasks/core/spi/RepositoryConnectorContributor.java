/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
