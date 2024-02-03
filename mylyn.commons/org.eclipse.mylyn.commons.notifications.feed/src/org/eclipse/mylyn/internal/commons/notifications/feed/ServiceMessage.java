/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.feed;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;

/**
 * @author Robert Elves
 */
public class ServiceMessage extends AbstractNotification {

	private Date date;

	private String description;

	private String eTag;

	private String id = "0"; //$NON-NLS-1$

	private String image;

	private String lastModified;

	private String title;

	private String url;

	public ServiceMessage(String eventId) {
		super(eventId);
	}

	public int compareTo(ServiceMessage o) {
		return -getId().compareTo(o.getId());
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getETag() {
		return eTag;
	}

	public String getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	@Override
	public String getLabel() {
		return getTitle();
	}

	public String getLastModified() {
		return lastModified;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public boolean isValid() {
		return id != null && title != null && description != null && image != null;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
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
		builder.append(", date="); //$NON-NLS-1$
		builder.append(date);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

	/**
	 * Called when the user clicks a link in the message.
	 * 
	 * @return whether the message should be closed when the link is clicked
	 */
	public boolean openLink(String link) {
		return false;
	}

}
