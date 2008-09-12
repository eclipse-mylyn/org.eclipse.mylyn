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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;

/**
 * @author Mik Kersten
 */
public class JavaImplementorsProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".implementors";

	public static final String NAME = "implemented by";

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
