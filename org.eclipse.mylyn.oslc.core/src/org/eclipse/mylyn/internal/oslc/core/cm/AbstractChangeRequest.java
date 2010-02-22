/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Tasktop EULA
 * which accompanies this distribution, and is available at
 * http://tasktop.com/legal
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core.cm;

/**
 * @see http://open-services.net/bin/view/Main/CmResourceDefinitionsV1
 * 
 * @author Robert Elves
 */
public abstract class AbstractChangeRequest {

	protected final String identifier;

	protected String title;

	private String type;

	private String description;

	private String subject;

	private String creator;

	private String modified;

	private String url;

	public AbstractChangeRequest(String identifier, String title) {
		this.identifier = identifier;
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModified() {
		return modified;
	}

	/**
	 * @param modified
	 *            - must conform to RFC3339
	 */
	public void setModified(String modified) {
		this.modified = modified;
	}

}