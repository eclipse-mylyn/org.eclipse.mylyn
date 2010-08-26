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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class BuildPlanRequest {

	private final List<String> planIds;

	public BuildPlanRequest(List<String> planIds) {
		this.planIds = Collections.unmodifiableList(new ArrayList<String>(planIds));
	}

	public List<String> getPlanIds() {
		return planIds;
	}

}
