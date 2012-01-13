/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.core;

import java.io.File;

import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.internal.hudson.core.HudsonConnector;

/**
 * @author Steffen Pingel
 */
public class HudsonCore {

	public static BuildConnector createConnector(File cacheFile) {
		return new HudsonConnector(cacheFile);
	}

}
