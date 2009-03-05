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

package org.eclipse.mylyn.internal.pde.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.mylyn.internal.java.ui.JavaUiUtil;
import org.eclipse.pde.api.tools.ui.internal.completion.APIToolsJavadocCompletionProposalComputer;

/**
 * Legacy computer that support API tooling content assist on Eclipse 3.4.
 * 
 * @author Steffen Pingel
 */
// TODO e3.5 remove this class
@SuppressWarnings("restriction")
public class FocusedApiToolsJavadocCompletionProposalComputer extends APIToolsJavadocCompletionProposalComputer {

	private static final String ASSIST_API_TOOLS = "org.eclipse.pde.api.tools.ui.apitools_proposal_category"; //$NON-NLS-1$

	@Override
	@SuppressWarnings("unchecked")
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (JavaUiUtil.getProposalCategory(ASSIST_API_TOOLS) == null
				&& !JavaUiUtil.isDefaultAssistActive(JavaUiUtil.ASSIST_JDT_NOTYPE)) {
			// on Eclipse 3.4 this was part of javaNoTypeProposalCategory, only return results if that is not enabled to avoid duplicates 
			return super.computeCompletionProposals(context, monitor);
		} else {
			// on Eclipse 3.5 this is a separate computer contributed by API tooling, don't do anything in that case
			return Collections.emptyList();
		}
	}

}
