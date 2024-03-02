/*******************************************************************************
 * Copyright (c) 2011, 2024 Tom Seidel, Remus Software and others.
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

package org.eclipse.mylyn.htmltext.commands.formatting;

import org.eclipse.mylyn.htmltext.commands.Command;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.9
 */
public class RemoveFormatCommand extends Command {

	@Override
	public String getCommandIdentifier() {
		return "removeFormat"; //$NON-NLS-1$
	}

}
