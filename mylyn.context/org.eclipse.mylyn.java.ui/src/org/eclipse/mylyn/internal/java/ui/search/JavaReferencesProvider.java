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

import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;

/**
 * @author Mik Kersten
 */
public class JavaReferencesProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".references"; //$NON-NLS-1$

	public static final String NAME = "referenced by"; //$NON-NLS-1$

	public JavaReferencesProvider() {
		super(JavaStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptResultElement(IJavaElement element) {
		if (element instanceof IImportDeclaration) {
			return false;
		}
		if (element instanceof IMethod method) {
			if (method.getElementName().startsWith("test")) { //$NON-NLS-1$
				return false; // HACK
			} else {
				return true;
			}
		}
		return false;
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
