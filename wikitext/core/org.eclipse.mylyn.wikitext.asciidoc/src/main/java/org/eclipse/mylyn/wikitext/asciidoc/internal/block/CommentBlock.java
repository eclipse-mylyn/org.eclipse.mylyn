/*******************************************************************************
 * Copyright (c) 2007, 2016 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Pattern;

/**
 * Text block containing comment
 */
public class CommentBlock extends AsciiDocBlock {

	public CommentBlock() {
		super(Pattern.compile("^/////*\\s*")); //$NON-NLS-1$
	}

	@Override
	protected void processBlockStart() {
		// do nothing for comments
	}

	@Override
	protected void processBlockContent(String line) {
		// do nothing for comments
	}

	@Override
	protected void processBlockEnd() {
		// do nothing for comments
	}

	@Override
	protected void resetLastTitle() {
		// do nothing for comments
	}
}
