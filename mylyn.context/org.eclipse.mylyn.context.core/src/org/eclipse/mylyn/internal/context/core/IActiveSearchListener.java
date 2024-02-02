/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.List;

/**
 * Interface for a listener of when the bugzilla search is completed
 * 
 * @author Shawn Minto
 */
public interface IActiveSearchListener {
	/**
	 * Called when a background search is completed
	 * 
	 * @param l
	 *            The list of objects that were returned by the search
	 */
	void searchCompleted(List<?> l);

	boolean resultsGathered();
}
