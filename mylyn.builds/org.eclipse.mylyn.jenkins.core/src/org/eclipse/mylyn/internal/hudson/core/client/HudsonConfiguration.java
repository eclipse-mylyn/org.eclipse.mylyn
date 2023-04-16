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

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class HudsonConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	public Map<String, String> jobNameById = new HashMap<String, String>();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HudsonConfiguration [jobNameById=");
		builder.append(jobNameById);
		builder.append("]");
		return builder.toString();
	}

}
