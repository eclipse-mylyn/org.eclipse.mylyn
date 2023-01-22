/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.client.AbstractWikiHandler;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracWikiClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiHandler extends AbstractWikiHandler {

	private final TracRepositoryConnector connector;

	public TracWikiHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public String[] downloadAllPageNames(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.TracWikiHandler_Download_Wiki_Page_Names, IProgressMonitor.UNKNOWN);
		try {
			String[] names = getTracWikiClient(repository).getAllWikiPageNames(monitor);
			return names;
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	@Override
	public TracWikiPage getWikiPage(TaskRepository repository, String pageName, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask(Messages.TracWikiHandler_Download_Wiki_Page, IProgressMonitor.UNKNOWN);
		try {
			TracWikiPage page = getTracWikiClient(repository).getWikiPage(pageName, monitor);
			return page;
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	@Override
	public void postWikiPage(TaskRepository repository, TracWikiPage newPage, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask(Messages.TracWikiHandler_Upload_Wiki_Page, IProgressMonitor.UNKNOWN);
		try {
			String pageName = newPage.getPageInfo().getPageName();
			String content = newPage.getContent();
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("comment", newPage.getPageInfo().getComment()); //$NON-NLS-1$
			attributes.put("author", newPage.getPageInfo().getAuthor()); //$NON-NLS-1$
			boolean success = getTracWikiClient(repository).putWikipage(pageName, content, attributes, monitor);
			if (success) {
				return;
			} else {
				throw new CoreException(TracCorePlugin.toStatus(
						new TracException("Failed to upload wiki page. No further information available."), //$NON-NLS-1$
						repository));
			}
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	@Override
	public TracWikiPageInfo[] getPageHistory(TaskRepository repository, String pageName, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask(Messages.TracWikiHandler_Retrieve_Wiki_Page_History, IProgressMonitor.UNKNOWN);
		try {
			TracWikiPageInfo[] versions = getTracWikiClient(repository).getWikiPageInfoAllVersions(pageName, monitor);
			return versions;
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	private ITracWikiClient getTracWikiClient(TaskRepository repository) throws TracException {
		ITracClient client = connector.getClientManager().getTracClient(repository);
		if (client instanceof ITracWikiClient) {
			return (ITracWikiClient) client;
		} else {
			throw new TracException("The access mode of " + repository.toString() //$NON-NLS-1$
					+ " does not support Wiki page editting."); //$NON-NLS-1$
		}
	}

	@Override
	public String getWikiUrl(TaskRepository repository) {
		return repository.getRepositoryUrl() + ITracClient.WIKI_URL;
	}
}
