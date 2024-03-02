/*******************************************************************************
 * Copyright (c) 2010, 2024 Tom Seidel, Remus Software and others
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
public class GetHtmlCommand extends Command {

	@Override
	public String getCommandIdentifier() {
		return "gethtml"; //$NON-NLS-1$
	}

	@Override
	public String getCommand() {
		return "return integration.editor.getData();"; //$NON-NLS-1$
	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
