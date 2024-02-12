/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.team.ui.history;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.ui.history.IHistoryPageSource;

/**
 * @author Steffen Pingel
 */
public class TaskHistoryAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IHistoryPageSource.class };

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

	@Override
	public <T> T getAdapter(final Object adaptable, Class<T> adapterType) {
		if (adapterType.isAssignableFrom(IHistoryPageSource.class)) {
			return adapterType.cast(TaskHistoryPageSource.getInstance());
		}
		return null;
	}

}
