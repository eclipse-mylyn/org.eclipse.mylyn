/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.discovery.tests.core.mock;

import java.net.URL;

import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;

/**
 * @author David Green
 */
public class MockDiscoverySource extends AbstractDiscoverySource {

	@Override
	public Object getId() {
		return "mock:mock";
	}

	@Override
	public URL getResource(String resourceName) {
		return null;
	}

}
