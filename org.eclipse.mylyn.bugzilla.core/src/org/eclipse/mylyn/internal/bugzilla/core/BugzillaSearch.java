/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class BugzillaSearch {

	public static class Entry {

		public final String key;

		public final String value;

		public Entry(String key, String value) {
			this.key = key;
			this.value = value;
		}

	}

	private final List<Entry> parameters;

	public BugzillaSearch(TaskRepository repository, String queryUrl) throws UnsupportedEncodingException {
		parameters = new ArrayList<Entry>();

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
			parameters.add(new Entry(key, value));
		}
	}

	public List<Entry> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public List<Entry> getParameters(String key) {
		List<Entry> result = new ArrayList<Entry>();
		for (Entry entry : parameters) {
			if (entry.key.equals(key)) {
				result.add(entry);
			}
		}
		return Collections.unmodifiableList(result);
	}

}
