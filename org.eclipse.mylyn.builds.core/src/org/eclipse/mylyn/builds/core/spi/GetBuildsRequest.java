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

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * @author Steffen Pingel
 */
public class GetBuildsRequest {

	private final Kind kind;

	private final IBuildPlan plan;

	public enum Kind {
		ALL, LAST
	};

	public GetBuildsRequest(Kind kind, IBuildPlan plan) {
		Assert.isNotNull(kind);
		Assert.isNotNull(plan);
		this.kind = kind;
		this.plan = plan;
	}

	public IBuildPlan getPlan() {
		return plan;
	}

	public Kind getKind() {
		return kind;
	}

}
