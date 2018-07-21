/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * Create a part descriptor from an extension point
 * 
 * @author Shawn Minto
 */
public class TaskEditorExtensionPartDescriptor extends TaskEditorPartDescriptor {

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private final IConfigurationElement element;

	public TaskEditorExtensionPartDescriptor(String id, IConfigurationElement element) {
		super(id);
		this.element = element;
		setPath(element.getAttribute(ATTR_PATH));

	}

	@Override
	public AbstractTaskEditorPart createPart() {
		try {
			return (AbstractTaskEditorPart) element.createExecutableExtension(ATTR_CLASS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
