/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor;



/**
 * @author Mik Kersten
 * @author Leah Findlater
 */
public class StudyParameters {
	private String title = MylarMonitorPlugin.DEFAULT_TITLE;
	private String description = MylarMonitorPlugin.DEFAULT_DESCRIPTION;
	private IQuestionnairePage questionnairePage = null;
	private long transmitPromptPeriod = MylarMonitorPlugin.DEFAULT_DELAY_BETWEEN_TRANSMITS;
	private String acceptedUrlList = MylarMonitorPlugin.DEFAULT_ACCEPTED_URL_LIST;
	
	private String formsConsent = MylarMonitorPlugin.DEFAULT_ETHICS_FORM;
	private String scriptsUrl = MylarMonitorPlugin.DEFAULT_UPLOAD_SERVER;
	private String scriptsUpload = MylarMonitorPlugin.DEFAULT_UPLOAD_SCRIPT;
	private String scriptsUserId = MylarMonitorPlugin.DEFAULT_UPLOAD_SCRIPT_ID;
	private String scriptsQuestionnaire = MylarMonitorPlugin.DEFAULT_UPLAOD_SCRIPT_QUESTIONNAIRE;
	
	public String getScriptsUserId() {
		return scriptsUserId;
	}
	public void setScriptsUserId(String scriptsGetUserId) {
		this.scriptsUserId = scriptsGetUserId;
	}
	public String getScriptsQuestionnaire() {
		return scriptsQuestionnaire;
	}
	public void setScriptsQuestionnaire(String scriptsQuestionnaire) {
		this.scriptsQuestionnaire = scriptsQuestionnaire;
	}
	public String getScriptsUrl() {
		return scriptsUrl;
	}
	public void setScriptsUrl(String scriptsServerUrl) {
		this.scriptsUrl = scriptsServerUrl;
	}
	public String getScriptsUpload() {
		return scriptsUpload;
	}
	public void setScriptsUpload(String scriptsUpload) {
		this.scriptsUpload = scriptsUpload;
	}
	public String getFormsConsent() {
		return formsConsent;
	}
	public void setFormsConsent(String formsConsent) {
		this.formsConsent = formsConsent;
	}
	public long getTransmitPromptPeriod() {
		return transmitPromptPeriod;
	}
	public void setTransmitPromptPeriod(long transmitPromptPeriod) {
		this.transmitPromptPeriod = transmitPromptPeriod;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public IQuestionnairePage getQuestionnairePage() {
		return questionnairePage;
	}
	public void setQuestionnairePage(IQuestionnairePage questionnairePage) {
		this.questionnairePage = questionnairePage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAcceptedUrlList () {
		return this.acceptedUrlList;
	}
	public void setAcceptedUrlList(String acceptedUrlList) {
		this.acceptedUrlList = acceptedUrlList;
	}
}
