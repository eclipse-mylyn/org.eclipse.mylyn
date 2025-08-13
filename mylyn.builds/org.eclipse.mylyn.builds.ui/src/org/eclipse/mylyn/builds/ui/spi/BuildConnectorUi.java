/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Steffen Pingel
 */
public abstract class BuildConnectorUi {

	private BuildConnector core;

	private IConfigurationElement element;

	private ImageDescriptor descriptor;

	public final void init(BuildConnector core, IConfigurationElement element) {
		Assert.isNotNull(core);
		if (this.core != null) {
			throw new IllegalStateException("Already initialized"); //$NON-NLS-1$
		}
		this.core = core;
		this.element = element;
	}

	public final BuildConnector getCore() {
		return core;
	}

	public ImageDescriptor getImageDescriptor() {
		if (descriptor == null) {
			if (element != null) {
				String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
				if (iconPath != null) {
					descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(),
							iconPath);
				}
			}
		}
		return descriptor;
	}

}
