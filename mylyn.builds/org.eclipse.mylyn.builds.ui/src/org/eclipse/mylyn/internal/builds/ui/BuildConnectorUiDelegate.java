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

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;

/**
 * @author Steffen Pingel
 */
public class BuildConnectorUiDelegate extends BuildConnectorUi {

	public BuildConnectorUiDelegate(BuildConnectorDescriptor descriptor, BuildConnector core) {
		init(core, descriptor.getElement());
	}

}
