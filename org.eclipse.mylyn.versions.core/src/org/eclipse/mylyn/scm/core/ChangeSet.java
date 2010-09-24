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

package org.eclipse.mylyn.scm.core;

import java.util.ArrayList;
import java.util.List;

public class ChangeSet {

	List<Change> changes = new ArrayList<Change>();

	String kind;

	ScmRepository repository;

	public List<Change> getChanges() {
		return changes;
	}

	public String getKind() {
		return kind;
	}

	public ScmRepository getRepository() {
		return repository;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setRepository(ScmRepository repository) {
		this.repository = repository;
	}

}
