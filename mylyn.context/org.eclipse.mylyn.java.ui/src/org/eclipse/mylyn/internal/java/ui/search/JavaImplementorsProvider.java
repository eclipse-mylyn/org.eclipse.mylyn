/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.search;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;

/**
 * @author Mik Kersten
 */
public class JavaImplementorsProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".implementors"; //$NON-NLS-1$

	public static final String NAME = "implemented by"; //$NON-NLS-1$

	public JavaImplementorsProvider() {
		super(JavaStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptElement(IJavaElement javaElement) {
		return javaElement != null && javaElement instanceof IType;
	}

	@Override
	protected String getSourceId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
