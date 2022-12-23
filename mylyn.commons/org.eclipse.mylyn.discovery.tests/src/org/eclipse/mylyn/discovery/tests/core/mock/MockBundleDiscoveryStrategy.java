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

import org.eclipse.core.runtime.IContributor;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;
import org.eclipse.mylyn.internal.discovery.core.model.BundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.Policy;

/**
 * a discovery strategy for bundles where the policy can be arbitrarily set
 * 
 * @author David Green
 */
public class MockBundleDiscoveryStrategy extends BundleDiscoveryStrategy {
	private Policy policy = Policy.defaultPolicy();

	@Override
	protected AbstractDiscoverySource computeDiscoverySource(IContributor contributor) {
		AbstractDiscoverySource discoverySource = super.computeDiscoverySource(contributor);
		discoverySource.setPolicy(policy);
		return discoverySource;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}
