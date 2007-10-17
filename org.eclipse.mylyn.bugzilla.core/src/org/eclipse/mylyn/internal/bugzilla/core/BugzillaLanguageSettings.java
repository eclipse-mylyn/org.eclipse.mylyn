/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;

/**
 * Class describing the html response of Bugzilla requests which are used within Mylyn.
 * 
 * Strings should be in the language which is the default for an Bugzilla instance.
 * 
 * @author Frank Becker
 */

public class BugzillaLanguageSettings implements Serializable {

	private static final long serialVersionUID = 5181964115189805498L;

	private String languageName = "<unknown>";

	private String login;

	private String login2;

	private String invalid;

	private String password;

	private String checkEmail;

	private String midairCollision;

	private String commentRequired;

	private String loggedOut;

	private String processed;

	public BugzillaLanguageSettings(String languageName, String checkEmail, String commentRequired, String invalid,
			String loggedOut, String login, String midairCollision, String password, String processed) {
		super();
		this.checkEmail = checkEmail;
		this.commentRequired = commentRequired;
		this.invalid = invalid;
		this.languageName = languageName;
		this.loggedOut = loggedOut;
		this.login = login;
		this.midairCollision = midairCollision;
		this.password = password;
		this.processed = processed;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCheckEmail() {
		return checkEmail;
	}

	public void setCheckEmail(String checkEmail) {
		this.checkEmail = checkEmail;
	}

	public String getMidairCollision() {
		return midairCollision;
	}

	public void setMidairCollision(String midairCollision) {
		this.midairCollision = midairCollision;
	}

	public String getCommentRequired() {
		return commentRequired;
	}

	public void setCommentRequired(String commentRequired) {
		this.commentRequired = commentRequired;
	}

	public String getLoggedOut() {
		return loggedOut;
	}

	public void setLoggedOut(String loggedOut) {
		this.loggedOut = loggedOut;
	}

	public String getProcessed() {
		return processed;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
	}

	public String getLogin2() {
		return login2;
	}

	public void setLogin2(String login2) {
		this.login2 = login2;
	}
}
