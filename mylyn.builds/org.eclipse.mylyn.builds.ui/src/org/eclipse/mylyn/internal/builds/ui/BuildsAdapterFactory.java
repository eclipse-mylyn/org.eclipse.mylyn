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
 *     See git history
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
	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

	@Override
	public <T> T getAdapter(final Object adaptable, Class<T> adapter) {
		if (adapter.isAssignableFrom(IHistoryPageSource.class)) {
			return adapter.cast(BuildHistoryPageSource.getInstance());
		}
		if (adapter.isAssignableFrom(IBuild.class)) {
			if (adaptable instanceof IBuildPlan) {
				return adapter.cast(((IBuildPlan) adaptable).getLastBuild());
			}
		}
		if (adapter.isAssignableFrom(IBuildServer.class)) {
			if (adaptable instanceof IBuildElement) {
				return adapter.cast(((IBuildElement) adaptable).getServer());
			}
		}
		return null;
	}

}
