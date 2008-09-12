/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
	public void searchCompleted(List<?> l);

	public boolean resultsGathered();
}
