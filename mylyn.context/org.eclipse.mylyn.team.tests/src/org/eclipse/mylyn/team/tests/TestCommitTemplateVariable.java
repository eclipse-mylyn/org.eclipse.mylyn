/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.AbstractCommitTemplateVariable;

@SuppressWarnings("nls")
public class TestCommitTemplateVariable extends AbstractCommitTemplateVariable {

	@Override
	public String getValue(ITask task) {
		StringBuilder sb = new StringBuilder();
		for (String arg : arguments) {
			sb.append(arg);
			sb.append("-");
		}
		return sb.substring(0, sb.length() - 1);
	}

}
