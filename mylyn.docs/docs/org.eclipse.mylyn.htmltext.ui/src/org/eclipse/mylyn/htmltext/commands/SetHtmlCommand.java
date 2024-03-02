/*******************************************************************************
 * Copyright (c) 2010, 2024 Tom Seidel, Remus Software and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.htmltext.commands;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class SetHtmlCommand extends Command {

	private String html;

	@Override
	public String getCommandIdentifier() {
		return "sethtml"; //$NON-NLS-1$
	}

	@Override
	public String getCommand() {
		return "integration.editor.setData('" + html + "');"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

	public void setHtml(String htmlToSet) {
		this.html = htmlToSet.replaceAll("\\'", "\\\\'").replaceAll("\\s", " ");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

}
