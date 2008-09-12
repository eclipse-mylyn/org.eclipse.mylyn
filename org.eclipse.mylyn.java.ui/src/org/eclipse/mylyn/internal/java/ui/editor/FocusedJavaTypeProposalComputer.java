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

package org.eclipse.mylyn.internal.java.ui.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.JavaTypeCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.mylyn.internal.java.ui.JavaUiUtil;

/**
 * @author Mik Kersten
 */
public class FocusedJavaTypeProposalComputer extends JavaTypeCompletionProposalComputer {

	public FocusedJavaTypeProposalComputer() {
		FocusedJavaProposalProcessor.getDefault().addMonitoredComputer(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!JavaUiUtil.isDefaultAssistActive(JavaUiUtil.ASSIST_JDT_TYPE)) {
			List proposals = super.computeCompletionProposals(context, monitor);
			return FocusedJavaProposalProcessor.getDefault().projectInterestModel(this, proposals);
		} else {
			return Collections.emptyList();
		}
	}
}
