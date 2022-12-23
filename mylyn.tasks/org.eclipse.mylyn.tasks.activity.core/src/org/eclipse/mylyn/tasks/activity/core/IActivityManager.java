/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.activity.core;

/**
 * @author Steffen Pingel
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public interface IActivityManager {

	/**
	 * Returns the activity stream for <code>scope</code>.
	 * 
	 * @param scope
	 *            the scope, must not be <code>null</code>
	 */
	IActivityStream getStream(ActivityScope scope);

}
