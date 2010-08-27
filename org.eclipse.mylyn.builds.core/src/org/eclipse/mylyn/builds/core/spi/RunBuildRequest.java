/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core.spi;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * @author Steffen Pingel
 */
public class RunBuildRequest {

	private long delay;

	private Map<String, String> parameters;

	private final IBuildPlan plan;

	public RunBuildRequest(IBuildPlan plan) {
		Assert.isNotNull(plan);
		this.plan = plan;
	}

	public long getDelay() {
		return delay;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public IBuildPlan getPlan() {
		return plan;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
