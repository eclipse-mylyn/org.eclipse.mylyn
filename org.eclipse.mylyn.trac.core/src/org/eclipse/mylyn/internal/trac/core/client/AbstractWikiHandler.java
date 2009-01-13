/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Xiaoyang Guan
 */
public abstract class AbstractWikiHandler {

	public abstract String[] downloadAllPageNames(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException;

	public abstract String getWikiUrl(TaskRepository repository);

	public abstract TracWikiPage getWikiPage(TaskRepository repository, String pageName, IProgressMonitor monitor)
			throws CoreException;

	public abstract void postWikiPage(TaskRepository repository, TracWikiPage page, IProgressMonitor monitor)
			throws CoreException;

	public abstract TracWikiPageInfo[] getPageHistory(TaskRepository repository, String pageName,
			IProgressMonitor monitor) throws CoreException;
}
