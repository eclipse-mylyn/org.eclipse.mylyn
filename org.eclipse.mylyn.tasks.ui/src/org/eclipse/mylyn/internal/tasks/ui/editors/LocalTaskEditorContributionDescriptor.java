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

/**
 * @author Shawn Minto
 */
public class LocalTaskEditorContributionDescriptor {

	private final IConfigurationElement element;

	private final String id;

	public LocalTaskEditorContributionDescriptor(IConfigurationElement element) {
		this.element = element;
		this.id = element.getAttribute("id"); //$NON-NLS-1$
	}

	public AbstractLocalEditorPart createPart() {
		try {
			return (AbstractLocalEditorPart) element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getId() {
		return id;
	}

}
