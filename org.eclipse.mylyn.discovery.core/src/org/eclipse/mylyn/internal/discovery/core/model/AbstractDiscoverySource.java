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
package org.eclipse.mylyn.internal.discovery.core.model;

import java.net.URL;

/**
 * @author David Green
 */
public abstract class AbstractDiscoverySource {
	private Policy policy = Policy.defaultPolicy();

	/**
	 * an identifier that can be used to determine the origin of the source, typically for logging purposes.
	 */
	public abstract Object getId();

	/**
	 * get a resource by an URL relative to the root of the source.
	 * 
	 * @param relativeUrl
	 *            the relative resource name
	 * @return an URL to the resource, or null if it is known that the resource does not exist.
	 */
	public abstract URL getResource(String resourceName);

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}
