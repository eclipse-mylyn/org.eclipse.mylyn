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
import org.eclipse.mylyn.htmltext.util.ColorConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.9
 */
public class SetForegroundColorCommand extends Command {

	private String color;

	@Override
	public String getCommandIdentifier() {
		return "setForeground"; //$NON-NLS-1$
	}

	@Override
	public String getCommand() {
		return "new CKEDITOR.style(CKEDITOR.instances.editor1.config['colorButton_foreStyle'], { color : '#" //$NON-NLS-1$
				 + color + "' } ).apply( CKEDITOR.instances.editor1.document );"; //$NON-NLS-1$
	}

	public void setColor(String hexCode) {
		color = hexCode;
	}

	public void setColor(RGB color) {
		this.color = ColorConverter.convertRgbToHex(color);
	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
