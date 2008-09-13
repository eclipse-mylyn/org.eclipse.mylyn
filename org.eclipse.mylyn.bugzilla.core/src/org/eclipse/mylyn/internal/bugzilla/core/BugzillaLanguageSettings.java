/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class describing the html response of Bugzilla requests which are used within Mylyn.
 * 
 * Strings should be in the language which is the default for an Bugzilla instance.
 * 
 * @author Frank Becker
 */

public class BugzillaLanguageSettings {
	private String languageName = "<unknown>";

	public static final String COMMAND_ERROR_LOGIN = "error_login";

	public static final String COMMAND_ERROR_COLLISION = "error_collision";

	public static final String COMMAND_ERROR_COMMENT_REQUIRED = "error_comment_required";

	public static final String COMMAND_ERROR_LOGGED_OUT = "error_logged_out";

	public static final String COMMAND_BAD_LOGIN = "bad_login";

	public static final String COMMAND_PROCESSED = "processed";

	public static final String COMMAND_CHANGES_SUBMITTED = "changes_submitted";

	private final Map<String, List<String>> languageAttributes = new LinkedHashMap<String, List<String>>();

	public BugzillaLanguageSettings(String languageName) {
		super();
		this.languageName = languageName;
	}

	public String getLanguageName() {
		return languageName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((languageName == null) ? 0 : languageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BugzillaLanguageSettings other = (BugzillaLanguageSettings) obj;
		if (languageName == null) {
			if (other.languageName != null) {
				return false;
			}
		} else if (!languageName.equals(other.languageName)) {
			return false;
		}
		return true;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public void addLanguageAttribute(String command, String response) {
		List<String> commandList = languageAttributes.get(command);
		if (commandList == null) {
			commandList = new LinkedList<String>();
			languageAttributes.put(command.toLowerCase(), commandList);
		}
		commandList.add(response);
	}

	public List<String> getResponseForCommand(String command) {
		return languageAttributes.get(command);
	}
}
