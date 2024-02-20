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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
/**
 * Needed to accept existing Hudson repositories so they can be converted to Jenkins repositories. Can probably be deleted around 2024-09
 * once the 4.0.0 release has been adopted "globally"
 *
 * @author George Lindholm
 */
@Deprecated(forRemoval = true)
public class HudsonConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	public Map<String, String> jobNameById = new HashMap<>();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HudsonConfiguration [jobNameById="); //$NON-NLS-1$
		builder.append(jobNameById);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
