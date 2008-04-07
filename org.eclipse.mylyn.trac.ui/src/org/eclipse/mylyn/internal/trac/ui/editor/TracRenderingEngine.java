/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;

/**
 * 
 * @author Xiaoyang Guan
 * @since 2.1
 */
public class TracRenderingEngine extends AbstractRenderingEngine {

	/**
	 * generate HTML preview page for <code>text</code>
	 */
	@Override
	public String renderAsHtml(TaskRepository repository, String text, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Render HTML Preview", IProgressMonitor.UNKNOWN);
		try {
			ITracClient client = TracCorePlugin.getDefault()
					.getConnector()
					.getClientManager()
					.getRepository(repository);
			if (client instanceof TracXmlRpcClient) {
				TracXmlRpcClient tracXmlRpcClient = (TracXmlRpcClient) client;
				String htmlText = tracXmlRpcClient.wikiToHtml(text, monitor);
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				String htmlHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">"
						+ "<head>"
						+ "<link rel=\"stylesheet\" href=\"REPOSITORY_URL/chrome/common/css/trac.css\" type=\"text/css\" />"
						+ "<link rel=\"stylesheet\" href=\"REPOSITORY_URL/chrome/common/css/wiki.css\" type=\"text/css\" />"
						+ "<link rel=\"icon\" href=\"REPOSITORY_URL/chrome/common/trac.ico\" type=\"image/x-icon\" />"
						+ "<link rel=\"shortcut icon\" href=\"EPOSITORY_URL/chrome/common/trac.ico\" type=\"image/x-icon\" />"
						+ "<style type=\"text/css\">body {background: #f4f4f4 url(REPOSITORY_URL/chrome/common/draft.png);margin: 0;padding: 0;}</style>"
						+ "</head>";
				String htmlBody = "<body> " + htmlText + "</body>";
				String htmlFooter = "</html>";

				String html = htmlHeader.replace("REPOSITORY_URL", repository.getRepositoryUrl()) + htmlBody + htmlFooter;
				return html;
			} else {
				throw new CoreException(TracCorePlugin.toStatus(new TracException(
						"Preview is available only in XML-RPC access mode"), repository));
			}
		} catch (TracException e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		} finally {
			monitor.done();
		}
	}

}
