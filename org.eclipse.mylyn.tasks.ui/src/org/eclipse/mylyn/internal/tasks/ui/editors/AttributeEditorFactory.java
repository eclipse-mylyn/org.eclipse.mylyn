/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

/**
 * @since 2.3
 * @author Steffen Pingel
 */
public class AttributeEditorFactory {

	public AttributeEditorFactory() {
	}

	public AbstractAttributeEditor getEditor(RepositoryTaskAttribute taskAttribute) {
		return null;
	}

}
