/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests.support.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;

/**
 * @author Mik Kersten
 */
public class TestActiveSearchListener implements IActiveSearchListener {

	private AbstractRelationProvider prov = null;

	private List<?> results = null;

	public TestActiveSearchListener(AbstractRelationProvider prov) {
		this.prov = prov;
	}

	private boolean gathered = false;

	public void searchCompleted(List<?> l) {
		List<Object> accepted = new ArrayList<Object>(l.size());
		if (prov != null) {
			for (Object o : l) {
				if (prov.acceptResultElement(o)) {
					accepted.add(o);
				}
			}
			results = accepted;
		} else {
			results = l;
		}
		gathered = true;
	}

	public boolean resultsGathered() {
		return gathered;
	}

	public List<?> getResults() {
		return results;
	}

}
