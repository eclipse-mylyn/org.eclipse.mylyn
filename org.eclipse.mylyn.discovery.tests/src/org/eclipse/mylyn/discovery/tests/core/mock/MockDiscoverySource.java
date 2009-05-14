/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.discovery.tests.core.mock;

import java.net.URL;

import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
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
