/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;

/**
 * @author Mik Kersten
 */
public class SearchResultsLabelProvider extends TaskElementLabelProvider {

	private final SearchResultContentProvider contentProvider;

	public SearchResultsLabelProvider(SearchResultContentProvider contentProvider) {
		super(true);
		this.contentProvider = contentProvider;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof Person || object instanceof TaskGroup) {
			return super.getText(object) + " (" + contentProvider.getChildren(object).length + ")";
		} else {
			return super.getText(object);
		}
	}

}
