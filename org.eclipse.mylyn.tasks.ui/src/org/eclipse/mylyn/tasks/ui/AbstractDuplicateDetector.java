/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.search.SearchHitCollector;

public abstract class AbstractDuplicateDetector {

	protected String name;

	protected String kind;

	public abstract SearchHitCollector getSearchHitCollector(TaskRepository repository, RepositoryTaskData taskData);

	public void setName(String name) {
		this.name = name;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getName() {
		return this.name;
	}

	public String getKind() {
		return this.kind;
	}

}
