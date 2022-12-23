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
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.search.ui.ISearchPageScoreComputer;

/**
 * Implements a {@link IAdapterFactory} for {@link ISearchPageScoreComputer}s which ranks {@link AbstractTaskContainer}s
 * high for the task search page
 * 
 * @author Willian Mitsuda
 */
public class SearchScoreComputerAdapterFactory implements IAdapterFactory {

	private final ISearchPageScoreComputer computer = new ISearchPageScoreComputer() {

		public int computeScore(String pageId, Object input) {
			if (!TaskSearchPage.ID.equals(pageId)) {
				return ISearchPageScoreComputer.UNKNOWN;
			}
			if (input instanceof IRepositoryElement) {
				return 100;
			}
			return ISearchPageScoreComputer.LOWEST;
		}

	};

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (ISearchPageScoreComputer.class.equals(adapterType)) {
			return computer;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { ISearchPageScoreComputer.class };
	}

}
