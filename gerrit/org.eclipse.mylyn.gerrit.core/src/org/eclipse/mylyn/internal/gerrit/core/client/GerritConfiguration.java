/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Sascha Scholz
 */
public final class GerritConfiguration {

	private GerritConfig gerritConfig;

	GerritConfiguration() {
		// no-args constructor needed by gson	
	}

	public GerritConfiguration(GerritConfig gerritConfig) {
		this.gerritConfig = gerritConfig;
	}

	/**
	 * @return the Gerrit configuration instance, never null
	 */
	public GerritConfig getGerritConfig() {
		return gerritConfig;
	}

}
