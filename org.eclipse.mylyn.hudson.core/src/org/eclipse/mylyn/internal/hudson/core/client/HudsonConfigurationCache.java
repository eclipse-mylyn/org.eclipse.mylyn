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

	@Override
	protected HudsonConfiguration createConfiguration() {
		return new HudsonConfiguration();
	}

	@Override
	protected HudsonConfiguration readConfiguration(ObjectInputStream in) throws IOException, ClassNotFoundException {
		return (HudsonConfiguration) in.readObject();
	}

}
