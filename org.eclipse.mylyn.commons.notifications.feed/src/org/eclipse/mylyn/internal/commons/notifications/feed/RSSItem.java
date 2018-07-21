/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Red Hat, Inc - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.feed;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class RSSItem {

	private String title;

	private String link;

	private String pubDate;

	private String creator;

	private ArrayList<String> subjects;

	private String description;

	private ArrayList<String> categories;

	private String guid;

	/**
	 * @return the guid
	 */
	@XmlElement(name = "guid")
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * @return the categories
	 */
	@XmlElement(name = "category", nillable = true)
	public List<String> getCategories() {
		if (categories == null) {
			synchronized (this) {
				if (categories == null) {
					categories = new ArrayList<String>(0);
				}
			}
		}
		return categories;
	}

	/**
	 * @param categories
	 *            the categories to set
	 */
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	/**
	 * @return the link
	 */
	@XmlElement(name = "link")
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the description
	 */
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the title
	 */
	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the pubDate
	 */
	@XmlElement(name = "pubDate")
	public String getPubDate() {
		return pubDate;
	}

	/**
	 * @param pubDate
	 *            the pubDate to set
	 */
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * @return the creator
	 */
	@XmlElement(name = "creator", namespace = "http://purl.org/dc/elements/1.1/")
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the subject
	 */
	@XmlElement(name = "subject", namespace = "http://purl.org/dc/elements/1.1/")
	public List<String> getSubjects() {
		if (subjects == null) {
			synchronized (this) {
				if (subjects == null) {
					subjects = new ArrayList<String>(0);
				}
			}
		}
		return subjects;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubjects(ArrayList<String> subjects) {
		this.subjects = subjects;
	}

	@Override
	public String toString() {
		return "RSSItem [title=" + title + ", link=" + link + ", pubDate=" + pubDate + ", guid=" + guid + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

}
