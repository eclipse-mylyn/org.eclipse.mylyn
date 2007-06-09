/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
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

	public static final String ID = ID_GENERIC + ".junitreferences";

	public static final String NAME = "tested by";

	public JUnitReferencesProvider() {
		super(JavaStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptResultElement(IJavaElement element) {
		if (element instanceof IMethod) {
			IMethod method = (IMethod) element;
			boolean isTestMethod = false;
			boolean isTestCase = false;
			if (method.getElementName().startsWith("test"))
				isTestMethod = true;

			IJavaElement parent = method.getParent();
			if (parent instanceof IType) {
				IType type = (IType) parent;
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
