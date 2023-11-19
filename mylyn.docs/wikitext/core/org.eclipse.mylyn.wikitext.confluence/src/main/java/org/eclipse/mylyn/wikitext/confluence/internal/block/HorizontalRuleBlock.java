/*******************************************************************************
 * Copyright (c) 2011, 2015 Igor Malinin, Paul Lin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * @author Igor Malinin
 * @author Paul Lin
 */
public class HorizontalRuleBlock extends Block {

	private static final Pattern PATTERN = Pattern.compile("\\s*-{4}\\s*"); //$NON-NLS-1$

	@Override
	protected int processLineContent(String line, int offset) {
		builder.horizontalRule();
		setClosed(true);
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher matcher = PATTERN.matcher(line);
			return matcher.matches();
		}
		return false;
	}
}
