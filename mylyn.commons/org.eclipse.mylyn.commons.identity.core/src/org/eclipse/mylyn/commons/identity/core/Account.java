/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.identity.core;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class Account implements Serializable {

	private static final long serialVersionUID = 3670630150657553390L;

	public static Account id(String id) {
		Assert.isNotNull(id);
		return new Account(id);
	}

	private final String id;

	private String kind;

	private String name;

	private String url;

	private Account(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		Account other = (Account) obj;
		if (!Objects.equals(id, other.id)) {
			return false;
		}
		if (!Objects.equals(kind, other.kind)) {
			return false;
		}
		if (!Objects.equals(name, other.name)) {
			return false;
		}
		if (!Objects.equals(url, other.url)) {
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public String getKind() {
		return kind;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, kind, name, url);
	}

	public Account kind(String kind) {
		this.kind = kind;
		return this;
	}

	public Account name(String name) {
		this.name = name;
		return this;
	}

	public Account url(String url) {
		this.url = url;
		return this;
	}

}
