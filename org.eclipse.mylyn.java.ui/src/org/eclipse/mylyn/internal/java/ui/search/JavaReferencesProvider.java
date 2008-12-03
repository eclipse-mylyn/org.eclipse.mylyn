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
		if (element instanceof IMethod) {
			IMethod method = (IMethod) element;
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
