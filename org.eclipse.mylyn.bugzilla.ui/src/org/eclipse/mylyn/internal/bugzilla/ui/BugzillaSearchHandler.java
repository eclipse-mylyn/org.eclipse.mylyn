/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractSearchHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Robert Elves
 */
@SuppressWarnings("restriction")
public class BugzillaSearchHandler extends AbstractSearchHandler {

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public boolean queryForText(TaskRepository taskRepository, IRepositoryQuery query, TaskData taskData,
			String searchString) {
		try {
			String queryUrl = taskRepository.getRepositoryUrl()
					+ "/buglist.cgi?long_desc_type=allwordssubstr&long_desc=" //$NON-NLS-1$
					+ URLEncoder.encode(searchString, taskRepository.getCharacterEncoding());
			query.setUrl(queryUrl);
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		return true;
	}

}
