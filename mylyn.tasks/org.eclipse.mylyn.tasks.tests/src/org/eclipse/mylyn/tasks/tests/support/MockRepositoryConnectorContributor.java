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

package org.eclipse.mylyn.tasks.tests.support;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorContributor;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorDescriptor;

public class MockRepositoryConnectorContributor extends RepositoryConnectorContributor {

	@Override
	public List<RepositoryConnectorDescriptor> getDescriptors() {
		return Collections.<RepositoryConnectorDescriptor> singletonList(new MockRepositoryConnectorDescriptor());
	}

}
