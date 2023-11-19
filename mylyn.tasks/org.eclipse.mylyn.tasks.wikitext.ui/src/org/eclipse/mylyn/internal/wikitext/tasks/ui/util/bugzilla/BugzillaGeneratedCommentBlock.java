/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.tasks.ui.util.bugzilla;

/**
 * A block for detecting bugzilla-generated text such as: *** This bug has been marked as a duplicate of bug 1234 ***
 *
 * @author David Green
 * @deprecated instead use {@link org.eclipse.mylyn.wikitext.parser.markup.block.BugzillaGeneratedCommentBlock}
 */
@Deprecated
public class BugzillaGeneratedCommentBlock
		extends org.eclipse.mylyn.wikitext.parser.markup.block.BugzillaGeneratedCommentBlock {

}
