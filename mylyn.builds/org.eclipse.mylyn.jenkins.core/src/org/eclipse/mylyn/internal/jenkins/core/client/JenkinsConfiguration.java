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

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class JenkinsConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	public Map<String, String> jobNameById = new HashMap<>();

	@Override
	public String toString() {
		return "JenkinsConfiguration [jobNameById=" + jobNameById + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
