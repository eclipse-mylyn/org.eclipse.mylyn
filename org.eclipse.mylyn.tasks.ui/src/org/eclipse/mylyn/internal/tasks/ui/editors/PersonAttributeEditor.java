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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/**
 * @author Steffen Pingel
 */
public class PersonAttributeEditor extends TextAttributeEditor {

	public PersonAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public String getValue() {
		IRepositoryPerson repositoryPerson = getAttributeMapper().getRepositoryPerson(getTaskAttribute());
		if (repositoryPerson != null) {
			return (isReadOnly()) ? repositoryPerson.toString() : repositoryPerson.getPersonId();
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public void setValue(String text) {
		IRepositoryPerson person = getAttributeMapper().getTaskRepository().createPerson(text);
		getAttributeMapper().setRepositoryPerson(getTaskAttribute(), person);
		attributeChanged();
	}

}
