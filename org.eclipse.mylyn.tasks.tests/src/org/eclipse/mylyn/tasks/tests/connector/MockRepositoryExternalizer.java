/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests.connector;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylar.tasks.core.ITask;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten
 */
public class MockRepositoryExternalizer extends DelegatingTaskExternalizer {

	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery query) {
		return query instanceof MockRepositoryQuery;
	}

	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof MockRepositoryTask;
	}

	@Override
	public boolean canCreateElementFor(AbstractQueryHit queryHit) {
		return false;
	}
	
	@Override
	public boolean canReadCategory(Node node) {
		return false;
	}

	@Override
	public boolean canReadQuery(Node node) {
		return false;
	}

	@Override
	public boolean canReadQueryHit(Node node) {
		return false;
	}

	@Override
	public boolean canReadTask(Node node) {
		return false;
	}

}
