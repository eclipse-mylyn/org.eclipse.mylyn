/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServerConfiguration;

/**
 * @author Steffen Pingel
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BuildServerConfiguration implements IBuildServerConfiguration {

	private final List<IBuildPlan> plans;

	public BuildServerConfiguration(List<IBuildPlan> plans) {
		this.plans = Collections.unmodifiableList(new ArrayList<>(plans));
	}

	@Override
	public List<IBuildPlan> getPlans() {
		return plans;
	}

}
