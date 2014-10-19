/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;

public class BugzillaRestConfiguration implements Serializable {

	private static final long serialVersionUID = -4658602653003624954L;

	private final String repositoryId;

	private Map<String, Field> fields;

	public BugzillaRestConfiguration(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public Field getFieldWithName(String fieldName) {
		return fields.get(fieldName);
	}

}
