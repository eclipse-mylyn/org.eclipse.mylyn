/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.RepositoryTemplate;

/**
 * @author Steffen Pingel
 */
public class RepositoryTemplateManager {

	private Map<String, Set<RepositoryTemplate>> templateByConnectorKind;

	public synchronized void addTemplate(String connectorKind, RepositoryTemplate template) {
		getTemplates(connectorKind).add(template);
	}

	public synchronized Set<RepositoryTemplate> getTemplates(String connectorKind) {
		if (templateByConnectorKind == null) {
			templateByConnectorKind = new HashMap<String, Set<RepositoryTemplate>>();
		}
		Set<RepositoryTemplate> templates = templateByConnectorKind.get(connectorKind);
		if (templates == null) {
			templates = new LinkedHashSet<RepositoryTemplate>();
			templateByConnectorKind.put(connectorKind, templates);
		}
		return templates;
	}

	public synchronized void removeTemplate(String connectorKind, RepositoryTemplate template) {
		getTemplates(connectorKind).remove(template);
	}

	/**
	 * Returns null if template not found.
	 */
	public synchronized RepositoryTemplate getTemplate(String connectorKind, String label) {
		for (RepositoryTemplate template : getTemplates(connectorKind)) {
			if (template.label.equals(label)) {
				return template;
			}
		}
		return null;
	}

}
