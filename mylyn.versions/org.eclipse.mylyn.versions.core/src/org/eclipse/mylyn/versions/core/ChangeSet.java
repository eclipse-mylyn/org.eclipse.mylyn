/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.versions.core.spi.ScmInfoAttributes;

/**
 * @author Steffen Pingel
 */
public class ChangeSet implements ScmInfoAttributes {

	private final List<Change> changes;

	private final Map<String, String> fAtrributes = new HashMap<>();

	private final ScmUser author;

	private final Date date;

	/**
	 * SHA1 hash or revision.
	 */
	private final String id;

	private String kind;

	private final String message;

	private ScmRepository repository;

	public ChangeSet(ScmUser author, Date date, String id, String fullMessage, ScmRepository repository,
			List<Change> changes) {
		this.author = author;
		this.date = date;
		this.id = id;
		message = fullMessage;
		this.repository = repository;
		this.changes = new ArrayList<>(changes);
	}

	public List<Change> getChanges() {
		return Collections.unmodifiableList(changes);
	}

	public String getKind() {
		return kind;
	}

	public ScmRepository getRepository() {
		return repository;
	}

	public ScmUser getAuthor() {
		return author;
	}

	public Date getDate() {
		return date != null ? new Date(date.getTime()) : null;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * This object should not be mutable
	 * 
	 * @param kind
	 */
	@Deprecated
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * This object should not be mutable
	 * 
	 * @param kind
	 */
	@Deprecated
	public void setRepository(ScmRepository repository) {
		this.repository = repository;
	}

	@Override
	public Map<String, String> getInfoAtrributes() {
		return fAtrributes;
	}

}
