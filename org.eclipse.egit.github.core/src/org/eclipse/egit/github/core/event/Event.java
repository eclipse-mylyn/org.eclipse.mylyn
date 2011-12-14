/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.util.DateUtils;

/**
 * Event model class.
 */
public class Event implements Serializable {

	private static final long serialVersionUID = 3633702964380402233L;

	/**
	 * Make sure this is above payload. Payload deserialization depends on being
	 * able to read the type first.
	 */
	private String type;

	@SerializedName("public")
	private boolean isPublic;

	private EventPayload payload;

	private EventRepository repo;

	private User actor;

	private User org;

	private Date createdAt;

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 * @return this Event
	 */
	public Event setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * @return isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic
	 * @return this Event
	 */
	public Event setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	/**
	 * @return the repo
	 */
	public EventRepository getRepo() {
		return repo;
	}

	/**
	 * @param repo
	 * @return this Event
	 */
	public Event setRepo(EventRepository repo) {
		this.repo = repo;
		return this;
	}

	/**
	 * @return the actor
	 */
	public User getActor() {
		return actor;
	}

	/**
	 * @param actor
	 * @return this Event
	 */
	public Event setActor(User actor) {
		this.actor = actor;
		return this;
	}

	/**
	 * @return the org
	 */
	public User getOrg() {
		return org;
	}

	/**
	 * @param org
	 * @return this Event
	 */
	public Event setOrg(User org) {
		this.org = org;
		return this;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return DateUtils.clone(createdAt);
	}

	/**
	 * @param createdAt
	 * @return this Event
	 */
	public Event setCreatedAt(Date createdAt) {
		this.createdAt = DateUtils.clone(createdAt);
		return this;
	}

	/**
	 * @return payload
	 */
	public EventPayload getPayload() {
		return payload;
	}

	/**
	 * @param payload
	 * @return this event
	 */
	public Event setPayload(EventPayload payload) {
		this.payload = payload;
		return this;
	}
}
