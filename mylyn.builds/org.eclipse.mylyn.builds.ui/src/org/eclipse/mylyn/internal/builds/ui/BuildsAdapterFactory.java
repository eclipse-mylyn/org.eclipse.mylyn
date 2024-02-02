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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.internal.builds.ui.history.BuildHistoryPageSource;
import org.eclipse.team.ui.history.IHistoryPageSource;

/**
 * @author Steffen Pingel
 */
public class BuildsAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IBuild.class, IBuildServer.class,
			IHistoryPageSource.class };

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(final Object adaptable, Class adapterType) {
		if (adapterType.isAssignableFrom(IHistoryPageSource.class)) {
			return BuildHistoryPageSource.getInstance();
		}
		if (adapterType.isAssignableFrom(IBuild.class)) {
			if (adaptable instanceof IBuildPlan) {
				return ((IBuildPlan) adaptable).getLastBuild();
			}
		}
		if (adapterType.isAssignableFrom(IBuildServer.class)) {
			if (adaptable instanceof IBuildElement) {
				return ((IBuildElement) adaptable).getServer();
			}
		}
		return null;
	}

}
