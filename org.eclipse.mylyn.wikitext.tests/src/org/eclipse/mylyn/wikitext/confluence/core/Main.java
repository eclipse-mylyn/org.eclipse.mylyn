/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence.core;


import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tests.AbstractTestApplication;

/**
 *
 *
 * @author David Green
 */
public class Main extends AbstractTestApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main main = new Main();
			main.doMain();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	protected MarkupLanguage createMarkupLanguage() {
		return new ConfluenceLanguage();
	}

}
