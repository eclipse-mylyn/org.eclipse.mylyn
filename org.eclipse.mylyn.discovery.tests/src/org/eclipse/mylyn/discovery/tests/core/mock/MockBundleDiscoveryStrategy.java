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
