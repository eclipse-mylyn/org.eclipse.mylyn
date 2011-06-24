/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Repository id
 */
public class RepositoryId implements IRepositoryIdProvider {

	/**
	 * Create repository from url.
	 * 
	 * @see #createFromId(String)
	 * @param url
	 * @return repository or null if parsing fails
	 */
	public static RepositoryId createFromUrl(URL url) {
		return url != null ? createFromId(url.getPath()) : null;
	}

	/**
	 * Create repository from id. The id is split on the '/' character and the
	 * last two non-empty segments are interpreted to be the repository owner
	 * and name.
	 * 
	 * @param id
	 * @return repository
	 */
	public static RepositoryId createFromId(String id) {
		if (id == null)
			return null;
		String owner = null;
		String name = null;
		String[] segments = id.split("/"); //$NON-NLS-1$
		for (int i = segments.length - 1; i >= 0; i--)
			if (segments[i].length() > 0)
				if (name == null)
					name = segments[i];
				else if (owner == null)
					owner = segments[i];
				else
					break;

		return owner != null && owner.length() > 0 && name != null
				&& name.length() > 0 ? new RepositoryId(owner, name) : null;
	}

	/**
	 * Create from string url
	 * 
	 * @see #createFromUrl(URL)
	 * @param url
	 * @return repository or null if it could not be parsed from url path
	 */
	public static RepositoryId createFromUrl(String url) {
		try {
			return url != null ? createFromUrl(new URL(url)) : null;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private final String owner;

	private final String name;

	/**
	 * Create repository id with given owner and name
	 * 
	 * @param owner
	 *            must be non-null and non-empty
	 * @param name
	 *            must be non-null and non-empty
	 */
	public RepositoryId(String owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	/**
	 * @return owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	public String generateId() {
		return owner + "/" + name;
	}

	@Override
	public int hashCode() {
		return generateId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof RepositoryId) {
			RepositoryId other = (RepositoryId) obj;
			return name.equals(other.name) && owner.equals(other.owner);
		}
		return false;
	}

	@Override
	public String toString() {
		return generateId();
	}

}
