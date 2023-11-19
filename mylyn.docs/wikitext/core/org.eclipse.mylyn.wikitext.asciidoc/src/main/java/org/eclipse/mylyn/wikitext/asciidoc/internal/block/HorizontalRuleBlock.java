/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

public class HorizontalRuleBlock extends Block {

	private static final Pattern pattern = Pattern.compile("'{3,}\\s*"); //$NON-NLS-1$

	@Override
	protected int processLineContent(String line, int offset) {
		builder.horizontalRule();
		setClosed(true);
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		return pattern.matcher(line.substring(lineOffset)).matches();
	}
}
