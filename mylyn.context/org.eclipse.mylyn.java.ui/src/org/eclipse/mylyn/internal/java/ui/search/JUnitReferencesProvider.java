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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.junit.InteractionContextTestUtil;

/**
 * @author Mik Kersten
 */
public class JUnitReferencesProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".junitreferences"; //$NON-NLS-1$

	public static final String NAME = "tested by"; //$NON-NLS-1$

	public JUnitReferencesProvider() {
		super(JavaStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptResultElement(IJavaElement element) {
		if (element instanceof IMethod method) {
			boolean isTestMethod = false;
			boolean isTestCase = false;
			if (method.getElementName().startsWith("test")) { //$NON-NLS-1$
				isTestMethod = true;
			}

			IJavaElement parent = method.getParent();
			if (parent instanceof IType type) {
				isTestCase = InteractionContextTestUtil.isTestType(type);
			}
			return isTestMethod && isTestCase;
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
