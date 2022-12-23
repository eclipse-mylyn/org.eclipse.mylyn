/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.mylyn.context.core.ContextComputationStrategy;

/**
 * An abstract strategy that can find Java types in the workspace based on their fully qualified name.
 * 
 * @author David Green
 */
public abstract class AbstractJavaContextComputationStrategy extends ContextComputationStrategy {

	protected IType findTypeInWorkspace(String typeName) throws CoreException {
		int dot = typeName.lastIndexOf('.');
		char[][] qualifications;
		String simpleName;
		if (dot != -1) {
			qualifications = new char[][] { typeName.substring(0, dot).toCharArray() };
			simpleName = typeName.substring(dot + 1);
		} else {
			qualifications = null;
			simpleName = typeName;
		}
		char[][] typeNames = new char[][] { simpleName.toCharArray() };

		class ResultException extends RuntimeException {
			private static final long serialVersionUID = 1L;

			private final IType fType;

			public ResultException(IType type) {
				fType = type;
			}
		}
		TypeNameMatchRequestor requestor = new TypeNameMatchRequestor() {
			@Override
			public void acceptTypeNameMatch(TypeNameMatch match) {
				throw new ResultException(match.getType());
			}
		};
		try {
			new SearchEngine().searchAllTypeNames(qualifications, typeNames, SearchEngine.createWorkspaceScope(),
					requestor, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
		} catch (ResultException e) {
			return e.fType;
		}
		return null;
	}
}
