/*******************************************************************************
 * Copyright (c) 2004, 2010 Willian Mitsuda and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.search.ui.ISearchPageScoreComputer;

/**
 * Implements a {@link IAdapterFactory} for {@link ISearchPageScoreComputer}s which ranks {@link AbstractTaskContainer}s high for the task
 * search page
 *
 * @author Willian Mitsuda
 */
public class SearchScoreComputerAdapterFactory implements IAdapterFactory {

	private final ISearchPageScoreComputer computer = (pageId, input) -> {
		if (!TaskSearchPage.ID.equals(pageId)) {
			return ISearchPageScoreComputer.UNKNOWN;
		}
		if (input instanceof IRepositoryElement) {
			return 100;
		}
		return ISearchPageScoreComputer.LOWEST;
	};

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (ISearchPageScoreComputer.class.equals(adapterType)) {
			return adapterType.cast(adaptableObject);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { ISearchPageScoreComputer.class };
	}

}
