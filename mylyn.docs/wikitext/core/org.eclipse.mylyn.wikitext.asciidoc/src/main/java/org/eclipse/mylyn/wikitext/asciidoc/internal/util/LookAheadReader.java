/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.util;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.util.LocationTrackingReader;

public class LookAheadReader {

	private ContentState state;

	private LocationTrackingReader reader;

	public void setContentState(ContentState state) {
		if (mustInitReader(state)) {
			this.state = state;
			reader = new LocationTrackingReader(new StringReader(state.getMarkupContent()));
		}
	}

	private boolean mustInitReader(ContentState newState) {
		if ((state != newState) || (reader == null) || (reader.getLineNumber() >= state.getLineNumber())) {
			return true;
		}
		return false;
	}

	public String lookAhead() {
		int lineNumber = state.getLineNumber();
		String nextLine = null;
		while (reader.getLineNumber() < lineNumber) {
			try {
				nextLine = reader.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return nextLine;
	}

}
