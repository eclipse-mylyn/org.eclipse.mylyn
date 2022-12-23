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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Defines a stream of {@link ActivityEvent}s that
 * 
 * @author Steffen Pingel
 * @author Timur Achmetow
 */
public interface IActivityStream {

	Set<ActivityEvent> getEvents();

	ActivityScope getScope();

	void query(IProgressMonitor monitor) throws CoreException;
}
