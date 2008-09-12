/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDiffUtil;

/**
 * @author Eugene Kuleshov
 */
public class TaskDiffUtilTest extends TestCase {

	public void testCleanComment() {
		assertEquals("attachment: some attachment. attachment description",
				TaskDiffUtil.cleanCommentText(("Created an attachment (id=111)\n" //
						+ "some attachment\n" //
						+ "\n" //
						+ "attachment description")));
		assertEquals("attachment: some attachment", TaskDiffUtil.cleanCommentText(("Created an attachment (id=111)\n" //
				+ "some attachment\n" //
				+ "\n")));
		assertEquals("some comment", TaskDiffUtil.cleanCommentText(("(In reply to comment #11)\n" //
				+ "some comment\n")));
		assertEquals("some comment. other comment", TaskDiffUtil.cleanCommentText((" (In reply to comment #11)\n" //
				+ "some comment\n" //
				+ "\n" //
				+ " (In reply to comment #12)\n" //
				+ "other comment\n")));
		assertEquals("some comment. other comment", TaskDiffUtil.cleanCommentText((" (In reply to comment #11)\n" //
				+ "some comment.  \n" //
				+ "\n" //
				+ " (In reply to comment #12)\n" //
				+ "> loren ipsum\n" + "> loren ipsum\n" + "other comment\n")));
	}

}
