/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.core;

import java.io.File;

import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.internal.jenkins.core.HudsonConnector;

/**
 * @author Steffen Pingel
 */
public class HudsonCore {

	public static BuildConnector createConnector(File cacheFile) {
		return new HudsonConnector(cacheFile);
	}

}
