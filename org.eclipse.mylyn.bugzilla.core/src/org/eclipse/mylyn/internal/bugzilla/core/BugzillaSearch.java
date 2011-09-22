/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class BugzillaSearch {

	private final Map<String, String> parameters;

	public BugzillaSearch(TaskRepository repository, String queryUrl) throws UnsupportedEncodingException {
		parameters = new LinkedHashMap<String, String>();

		queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1); //$NON-NLS-1$
		String[] options = queryUrl.split("&"); //$NON-NLS-1$
		for (String option : options) {
			String key;
			int endindex = option.indexOf("="); //$NON-NLS-1$
			if (endindex == -1) {
				key = null;
			} else {
				key = option.substring(0, option.indexOf("=")); //$NON-NLS-1$
			}
			if (key == null) {
				continue;
			}
			String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1), //$NON-NLS-1$
					repository.getCharacterEncoding());
			parameters.put(key, value);
		}
	}

	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

}
