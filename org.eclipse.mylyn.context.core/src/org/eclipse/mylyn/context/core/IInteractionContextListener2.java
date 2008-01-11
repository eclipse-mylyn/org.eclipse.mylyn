/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.List;

/**
 * Notified of changes to the context model activity.
 * 
 * API-3.0: fold into super interface
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 * @since 2.2
 */
public interface IInteractionContextListener2 extends IInteractionContextListener {

	/**
	 * @param context	can be null
	 */
	public void contextPreActivated(IInteractionContext context);
	
	public void elementsDeleted(List<IInteractionElement> elements);
}
