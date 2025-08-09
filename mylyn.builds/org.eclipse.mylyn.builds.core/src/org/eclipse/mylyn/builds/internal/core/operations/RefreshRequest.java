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

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.List;

import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * @author Steffen Pingel
 */
public class RefreshRequest {

	private final IBuildModel model;

	/**
	 * Private field used by {@link RefreshSession} to track stale plans on a request basis.
	 */
	List<IBuildPlan> stalePlans;

	final List<IBuildPlan> plansToRefresh;

	public RefreshRequest(IBuildModel model, List<IBuildPlan> plansToRefresh) {
		this.model = model;
		this.plansToRefresh = plansToRefresh;
	}

	public RefreshRequest(IBuildModel model) {
		this(model, null);
	}

	public IBuildModel getModel() {
		return model;
	}

}
