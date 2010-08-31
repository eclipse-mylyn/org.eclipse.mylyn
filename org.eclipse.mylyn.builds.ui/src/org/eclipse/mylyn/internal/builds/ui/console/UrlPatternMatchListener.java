/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.console;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

/**
 * @author Steffen Pingel
 */
public class UrlPatternMatchListener implements IPatternMatchListenerDelegate {

	private TextConsole console;

	public void connect(TextConsole console) {
		this.console = console;
	}

	public void disconnect() {
		// ignore

	}

	public void matchFound(PatternMatchEvent event) {
		try {
			int offset = event.getOffset();
			int length = event.getLength();
			String url = console.getDocument().get(offset, length - 1);
			IHyperlink link = new UrlHyperLink(url);
			console.addHyperlink(link, offset, length - 1);
		} catch (BadLocationException e) {
		}
	}

}
