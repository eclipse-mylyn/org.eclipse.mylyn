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

import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public interface IMylarContext {

	/**
	 * @return	null if no unique handle, e.g. if a composite context
	 */
	public abstract String getHandleIdentifier();
	
	public abstract List<InteractionEvent> getInteractionHistory();

	public List<IMylarElement> getInteresting();

	public abstract IMylarElement get(String element);

	public abstract IMylarElement getActiveNode();

	public abstract void delete(IMylarElement element);

	public abstract void updateElementHandle(IMylarElement element, String newHandle);

	public abstract List<IMylarElement> getAllElements();

}
