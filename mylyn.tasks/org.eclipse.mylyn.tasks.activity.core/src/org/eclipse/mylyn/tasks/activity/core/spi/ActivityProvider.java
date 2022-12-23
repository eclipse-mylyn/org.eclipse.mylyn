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

package org.eclipse.mylyn.tasks.activity.core.spi;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.activity.core.ActivityScope;

/**
 * @author Steffen Pingel
 */
public abstract class ActivityProvider {

	public abstract void open(IActivitySession session);

	public abstract void query(ActivityScope scope, IProgressMonitor monitor) throws CoreException;

	public abstract void close();

}
