/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (ISearchPageScoreComputer.class.equals(adapterType)) {
			return computer;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { ISearchPageScoreComputer.class };
	}

}
