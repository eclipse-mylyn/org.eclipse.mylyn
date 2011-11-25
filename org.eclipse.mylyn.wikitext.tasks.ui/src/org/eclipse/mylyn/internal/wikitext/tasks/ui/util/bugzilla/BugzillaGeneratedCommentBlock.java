/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tasks.ui.util.bugzilla;

/**
 * A block for detecting bugzilla-generated text such as: *** This bug has been marked as a duplicate of bug 1234 ***
 * 
 * @author David Green
 * @deprecated instead use {@link org.eclipse.mylyn.wikitext.core.parser.markup.block.BugzillaGeneratedCommentBlock}
 */
@Deprecated
public class BugzillaGeneratedCommentBlock extends
		org.eclipse.mylyn.wikitext.core.parser.markup.block.BugzillaGeneratedCommentBlock {

}
