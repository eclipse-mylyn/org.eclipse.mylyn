/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.context.core;

import java.util.List;


/**
 * @author Mik Kersten
 */
public interface IMylarContextListener {

	public void contextActivated(IMylarContext context);

	public void contextDeactivated(IMylarContext context);

	/**
	 * Called when the interest level for multiple elements changes, sorted
	 * according to the containment hierarchy. The last element is the element
	 * invoking the change.
	 */
	public void interestChanged(List<IMylarElement> elements);

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkAdded(IMylarElement element);

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkRemoved(IMylarElement element);

	public void elementDeleted(IMylarElement element);
	
	public void relationsChanged(IMylarElement element);
}
