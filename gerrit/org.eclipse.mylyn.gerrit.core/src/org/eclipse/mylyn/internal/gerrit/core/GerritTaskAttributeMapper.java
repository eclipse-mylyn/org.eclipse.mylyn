/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * TaskAttributeMapper for Gerrit.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 */
public class GerritTaskAttributeMapper extends TaskAttributeMapper {

	/**
	 * Constructor.
	 * 
	 * @param taskRepository
	 */
	public GerritTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	/* (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#mapToRepositoryKey(org.eclipse.mylyn.tasks.core.data.
	 * TaskAttribute, java.lang.String) */
	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		for (GerritAttribute attr : GerritAttribute.values()) {
			if (key.equals(attr.getTaskKey())) {
				key = attr.getGerritKey();
				break;
			}
		}
		return key;
	}

}
