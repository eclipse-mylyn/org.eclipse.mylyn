/*******************************************************************************
 * Copyright (c) 2012, 2015 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.markdown.internal.block;

import java.util.regex.Pattern;

/**
 * @author Stefan Seelmann
 */
public class HorizontalRuleBlock extends NestableBlock {

	private static final Pattern pattern = Pattern.compile("(\\*\\s*){3,}|(-\\s*){3,}|(_\\s*){3,}"); //$NON-NLS-1$

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
