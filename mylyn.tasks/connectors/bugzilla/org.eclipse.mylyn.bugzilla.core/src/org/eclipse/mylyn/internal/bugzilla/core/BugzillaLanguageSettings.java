/*******************************************************************************
 * Copyright (c) 2004, 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
 * Class describing the html response of Bugzilla requests which are used within Mylyn. Strings should be in the
 * language which is the default for an Bugzilla instance.
 * 
 * @author Frank Becker
 */

public class BugzillaLanguageSettings {
	private String languageName = "<unknown>"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_LOGIN = "error_login"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_COLLISION = "error_collision"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_COMMENT_REQUIRED = "error_comment_required"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_LOGGED_OUT = "error_logged_out"; //$NON-NLS-1$

	public static final String COMMAND_BAD_LOGIN = "bad_login"; //$NON-NLS-1$

	public static final String COMMAND_PROCESSED = "processed"; //$NON-NLS-1$

	public static final String COMMAND_CHANGES_SUBMITTED = "changes_submitted"; //$NON-NLS-1$

	public static final String COMMAND_SUSPICIOUS_ACTION = "suspicious_action"; //$NON-NLS-1$

	public static final String COMMAND_BUG = "bug"; //$NON-NLS-1$

	public static final String COMMAND_SUBMITTED = "submitted"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_CONFIRM_MATCH = "error_confirm_match"; //$NON-NLS-1$

	public static final String COMMAND_ERROR_MATCH_FAILED = "error_match_failed"; //$NON-NLS-1$

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
