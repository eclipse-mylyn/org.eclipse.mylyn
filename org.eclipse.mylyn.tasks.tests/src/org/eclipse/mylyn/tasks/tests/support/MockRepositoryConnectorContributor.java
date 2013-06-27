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
