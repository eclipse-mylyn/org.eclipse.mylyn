/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiHandler extends AbstractWikiHandler {

	private TracRepositoryConnector connector;

	public TracWikiHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public String[] downloadAllPageNames(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Download Wiki Page Names", IProgressMonitor.UNKNOWN);
		try {
			String[] names = getTracWikiClient(repository).getAllWikiPageNames();
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
		monitor.beginTask("Download Wiki Page", IProgressMonitor.UNKNOWN);
		try {
			TracWikiPage page = getTracWikiClient(repository).getWikiPage(pageName);
			return page;
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	public void postWikiPage(TaskRepository repository, TracWikiPage newPage, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Upload Wiki Page", IProgressMonitor.UNKNOWN);
		try {
			String pageName = newPage.getPageInfo().getPageName();
			String content = newPage.getContent();
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("comment", newPage.getPageInfo().getComment());
			attributes.put("author", newPage.getPageInfo().getAuthor());
			boolean success = getTracWikiClient(repository).putWikipage(pageName, content, attributes);
			if (success) {
				return;
			} else {
				throw new CoreException(TracCorePlugin.toStatus(new TracException(
						"Failed to upload wiki page. No further information available."), repository));
			}
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

	private ITracWikiClient getTracWikiClient(TaskRepository repository) throws TracException {
		try {
			ITracClient client = connector.getClientManager().getRepository(repository);
			if (client instanceof ITracWikiClient) {
				return (ITracWikiClient) client;
			} else {
				throw new TracException("The access mode of " + repository.toString()
						+ " does not support Wiki page editting.");
			}
		} catch (MalformedURLException e) {
			throw new TracException(e);
		}
	}

	@Override
	public String getWikiUrl(TaskRepository repository) {
		return repository.getUrl() + ITracClient.WIKI_URL;
	}
}
