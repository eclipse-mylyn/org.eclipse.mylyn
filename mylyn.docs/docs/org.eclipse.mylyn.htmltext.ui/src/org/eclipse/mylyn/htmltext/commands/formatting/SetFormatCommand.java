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

package org.eclipse.mylyn.htmltext.commands.formatting;

import org.eclipse.mylyn.htmltext.HtmlComposer;
import org.eclipse.mylyn.htmltext.commands.Command;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class SetFormatCommand extends Command {

	private String selectedFormat;

	private String formatOfSelection;

	@Override
	public String getCommandIdentifier() {
		return "setformat"; //$NON-NLS-1$
	}

	@Override
	public String getCommand() {
		return "integration.format.setStyle('" + selectedFormat + "');"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setFormatToWidget(String selectedFormat) {
		this.selectedFormat = selectedFormat;
	}

	public String getFormatOfSelection() {
		return formatOfSelection;
	}

	@Override
	public void setComposer(HtmlComposer composer) {
		super.setComposer(composer);
		new FormatChangeFunction(composer.getBrowser());
	}

	public void setFormatOfSelection(String formatOfSelection) {
		String oldValue = this.formatOfSelection;
		this.formatOfSelection = formatOfSelection;
		firePropertyChange("formatOfSelection", oldValue, formatOfSelection); //$NON-NLS-1$
	}

	private class FormatChangeFunction extends BrowserFunction {

		public FormatChangeFunction(Browser browser) {
			super(browser, "_delegate_selectedformat"); //$NON-NLS-1$
		}

		@Override
		public Object function(Object[] arguments) {
			setFormatOfSelection(String.valueOf(arguments[0]));
			return null;
		}

	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
