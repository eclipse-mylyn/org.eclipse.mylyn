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

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.eclipse.mylyn.builds.core.spi.AbstractConfigurationCache;

/**
 * @author Steffen Pingel
 */
public class JenkinsConfigurationCache extends AbstractConfigurationCache<JenkinsConfiguration> {

	public JenkinsConfigurationCache(File cacheFile) {
		super(cacheFile);
	}

	public JenkinsConfigurationCache() {
		super();
	}

	@Override
	protected JenkinsConfiguration createConfiguration() {
		return new JenkinsConfiguration();
	}

	@Override
	protected JenkinsConfiguration readConfiguration(ObjectInputStream in) throws IOException, ClassNotFoundException {
		final Object configuration = in.readObject();
		if (configuration instanceof org.eclipse.mylyn.internal.hudson.core.client.HudsonConfiguration) {
			final JenkinsConfiguration jenkinsConfiguration = new JenkinsConfiguration();
			jenkinsConfiguration.jobNameById = new HashMap<>(
					((org.eclipse.mylyn.internal.hudson.core.client.HudsonConfiguration) configuration).jobNameById);
			return jenkinsConfiguration;
		} else {
			return (JenkinsConfiguration) configuration;
		}
	}

}
