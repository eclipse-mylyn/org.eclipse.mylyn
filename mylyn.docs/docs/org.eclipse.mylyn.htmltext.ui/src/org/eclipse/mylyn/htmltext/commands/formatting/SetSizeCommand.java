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
public class SetSizeCommand extends Command {

	private String setSelectedSize;

	private String sizeOfSelection;

	@Override
	public String getCommandIdentifier() {
		return "setsize"; //$NON-NLS-1$
	}

	@Override
	public String getCommand() {
		return "integration.format.setSize(" + setSelectedSize + ");"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String[] getAvailableSizes() {
		String sizeString = String.valueOf(composer.evaluate("return integration.format.getAvailableSizes();")); //$NON-NLS-1$
		String[] split = sizeString.split(";"); //$NON-NLS-1$
		String[] returnValue = new String[split.length];
		for (int i = 0, n = split.length; i < n; i++) {
			returnValue[i] = split[i].split("/")[0]; //$NON-NLS-1$
		}
		return returnValue;

	}

	public void setSizeToWidget(String selectedFormat) {
		setSelectedSize = selectedFormat;
	}

	public String getSizeOfSelection() {
		return sizeOfSelection;
	}

	@Override
	public void setComposer(HtmlComposer composer) {
		super.setComposer(composer);
		new FormatChangeFunction(composer.getBrowser());
	}

	public void setSizeOfSelection(String sizeOfSelection) {
		String oldValue = this.sizeOfSelection;
		this.sizeOfSelection = sizeOfSelection;
		firePropertyChange("sizeOfSelection", oldValue, sizeOfSelection); //$NON-NLS-1$
	}

	private class FormatChangeFunction extends BrowserFunction {

		public FormatChangeFunction(Browser browser) {
			super(browser, "_delegate_selectedsize"); //$NON-NLS-1$
		}

		@Override
		public Object function(Object[] arguments) {
			setSizeOfSelection(String.valueOf(arguments[0]));
			return null;
		}

	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
