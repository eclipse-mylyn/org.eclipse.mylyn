/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.mylyn.wikitext.ui.WikiTextUiPlugin;

/**
 *
 *
 * @author David Green
 */
public class FilePropertyTester extends PropertyTester {

	private static final String CONTENT_TYPE_CLASSIFICATION = "contentTypeClassification";

	public FilePropertyTester() {
	}

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if ((receiver instanceof IFile) && property.equals(CONTENT_TYPE_CLASSIFICATION)) {
			return testContentType((IFile) receiver, toString(expectedValue));
		}
		return false;
	}

	private boolean testContentType(IFile file, String expectedValue) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription != null) {
				IContentType contentType = contentDescription.getContentType();
				while (contentType != null) {
					if (expectedValue.equals(contentType.getId())) {
						return true;
					}
					contentType = contentType.getBaseType();
				}
			}
		} catch (CoreException e) {
			WikiTextUiPlugin.getDefault().log(e);
		}
		return false;
	}

	protected String toString(Object expectedValue) {
		return expectedValue == null ? "" : expectedValue.toString(); //$NON-NLS-1$
	}
}
