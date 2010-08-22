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
