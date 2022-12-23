/*******************************************************************************
 * Copyright (c) 2004, 2010 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Compute repository connectors type/label map
 * 
 * @author Willian Mitsuda
 */
public class TaskRepositoryParameterValues implements IParameterValues {

	public Map<String, String> getParameterValues() {
		Collection<AbstractRepositoryConnector> connectors = TasksUi.getRepositoryManager().getRepositoryConnectors();
		Map<String, String> values = new HashMap<String, String>();
		for (AbstractRepositoryConnector connector : connectors) {
			if (connector.canCreateRepository()) {
				values.put(connector.getLabel(), connector.getConnectorKind());
			}
		}
		return values;
	}

}
