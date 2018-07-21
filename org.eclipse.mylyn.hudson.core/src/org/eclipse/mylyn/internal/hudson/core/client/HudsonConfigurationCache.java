/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.eclipse.mylyn.builds.core.spi.AbstractConfigurationCache;

/**
 * @author Steffen Pingel
 */
public class HudsonConfigurationCache extends AbstractConfigurationCache<HudsonConfiguration> {

	public HudsonConfigurationCache(File cacheFile) {
		super(cacheFile);
	}

	public HudsonConfigurationCache() {
		super();
	}

	@Override
	protected HudsonConfiguration createConfiguration() {
		return new HudsonConfiguration();
	}

	@Override
	protected HudsonConfiguration readConfiguration(ObjectInputStream in) throws IOException, ClassNotFoundException {
		return (HudsonConfiguration) in.readObject();
	}

}
