/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.history;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.ui.history.IHistoryPageSource;

/**
 * @author Steffen Pingel
 */
public class TaskHistoryAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IHistoryPageSource.class };

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(final Object adaptable, Class adapterType) {
		if (adapterType.isAssignableFrom(IHistoryPageSource.class)) {
			return TaskHistoryPageSource.getInstance();
		}
		return null;
	}

}
