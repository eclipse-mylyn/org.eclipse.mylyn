/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.token;

import org.eclipse.mylyn.wikitext.confluence.internal.ConfluenceContentState;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

public class ImpliedHyperlinkReplacementToken
		extends org.eclipse.mylyn.wikitext.parser.markup.token.ImpliedHyperlinkReplacementToken {

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkReplacementTokenProcessor();
	}

	private static class HyperlinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String target = group(1);
			if (getState().isWithinLink()) {
				getBuilder().characters(target);
			} else {
				getBuilder().link(target, target);
			}
		}

		@Override
		public ConfluenceContentState getState() {
			return (ConfluenceContentState) super.getState();
		}
	}
}
