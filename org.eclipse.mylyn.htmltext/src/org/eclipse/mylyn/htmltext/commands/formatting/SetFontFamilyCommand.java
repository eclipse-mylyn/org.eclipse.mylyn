/*******************************************************************************
 * Copyright (c) 2010 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.htmltext.commands.formatting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.htmltext.HtmlComposer;
import org.eclipse.mylyn.htmltext.commands.Command;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class SetFontFamilyCommand extends Command {

	private String setSelectedFontfamily;

	private String fontfamilyOfSelection;

	@Override
	public String getCommandIdentifier() {
		return "setfontfamily";
	}

	@Override
	public String getCommand() {
		return "integration.format.setFont('" + setSelectedFontfamily + "');";
	}

	public Map<String, String> getAvailableFontfamilies() {
		String sizeString = String.valueOf(composer.evaluate("return integration.format.getAvailableFonts();"));
		String[] split = sizeString.split(";");
		Map<String, String> returnValue = new HashMap<String, String>();
		for (int i = 0, n = split.length; i < n; i++) {
			returnValue.put(split[i].split("/")[0], split[i].split("/")[1]);
		}
		return returnValue;

	}

	public void setFontfamilyToWidget(String selectedFontfamily) {
		setSelectedFontfamily = selectedFontfamily;
	}

	public String getFontfamilyOfSelection() {
		return fontfamilyOfSelection;
	}

	@Override
	public void setComposer(HtmlComposer composer) {
		super.setComposer(composer);
		new FontfamilyChangeFunction(composer.getBrowser());
	}

	public void setFontfamilyOfSelection(String sizeOfSelection) {
		String oldValue = fontfamilyOfSelection;
		fontfamilyOfSelection = sizeOfSelection;
		firePropertyChange("fontfamilyOfSelection", oldValue, sizeOfSelection);
	}

	private class FontfamilyChangeFunction extends BrowserFunction {

		public FontfamilyChangeFunction(Browser browser) {
			super(browser, "_delegate_selectedfontfamily");
		}

		@Override
		public Object function(Object[] arguments) {
			setFontfamilyOfSelection(String.valueOf(arguments[0]));
			return null;
		}

	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
