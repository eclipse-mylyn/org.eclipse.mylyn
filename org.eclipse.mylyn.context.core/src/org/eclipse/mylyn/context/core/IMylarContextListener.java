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
 * 
 * TODO: refactor this interface, it's too cumbersome
 */
public interface IMylarContextListener {

	/**
	 * TODO: refactor
	 */
	public enum UpdateKind {
		HIGHLIGHTER, SCALING, UPDATE, FILTER
	}

	public void contextActivated(IMylarContext context);

	public void contextDeactivated(IMylarContext context);

	/**
	 * E.g. highlighters or scaling factors are being actively modified (for
	 * active updating).
	 * 
	 * @param kind
	 *            TODO
	 */
	public void presentationSettingsChanging(UpdateKind kind);

	/**
	 * Modification completed (for slow updating).
	 * 
	 * @param kind
	 *            TODO
	 */
	public void presentationSettingsChanged(UpdateKind kind);

	/**
	 * Called when the interest level for multiple elements changes, sorted
	 * according to the containment hierarchy. The last element is the element
	 * invoking the change.
	 */
	public void interestChanged(List<IMylarElement> elements);

	public void nodeDeleted(IMylarElement element);

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

	public void edgesChanged(IMylarElement element);
}
