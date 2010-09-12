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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;

/**
 * @author Steffen Pingel
 */
public class BuildsAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IBuild.class, IBuildServer.class };

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptable, Class adapterType) {
		if (adapterType == IBuild.class) {
			if (adaptable instanceof IBuildPlan) {
				return ((IBuildPlan) adaptable).getLastBuild();
			}
		}
		if (adapterType == IBuildServer.class) {
			if (adaptable instanceof IBuildElement) {
				return ((IBuildElement) adaptable).getServer();
			}
		}
		return null;
	}

}
