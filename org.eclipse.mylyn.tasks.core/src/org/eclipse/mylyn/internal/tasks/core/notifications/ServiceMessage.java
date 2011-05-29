/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.notifications;

/**
 * @author Robert Elves
 */
public class ServiceMessage implements Comparable<ServiceMessage> {

	public enum Element {
		ID, TITLE, DESCRIPTION, URL, IMAGE
	};

	private String id;

	private String title;

	private String description;

	private String url;

	private String image;

	private String eTag;

	private String lastModified;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getImage() {
		return image;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getETag() {
		return eTag;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isValid() {
		return id != null && title != null && description != null && image != null;
	}

	public int compareTo(ServiceMessage o) {
		return -getId().compareTo(o.getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceMessage [id="); //$NON-NLS-1$
		builder.append(id);
		builder.append(", title="); //$NON-NLS-1$
		builder.append(title);
		builder.append(", description="); //$NON-NLS-1$
		builder.append(description);
		builder.append(", url="); //$NON-NLS-1$
		builder.append(url);
		builder.append(", image="); //$NON-NLS-1$
		builder.append(image);
		builder.append(", eTag="); //$NON-NLS-1$
		builder.append(eTag);
		builder.append(", lastModified="); //$NON-NLS-1$
		builder.append(lastModified);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
