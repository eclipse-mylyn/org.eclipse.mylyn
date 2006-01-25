/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ide.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.ui.wizards.CommitWizard;

/**
 * @author Mik Kersten
 */
public class MylarCommitWizard extends CommitWizard {

	public MylarCommitWizard(IResource[] resources, ITask task) throws CVSException {
		super(resources);
	}

	@Override
	public void dispose() {
		try {
			super.dispose();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "failure on disposing commit wizard", false);
		}
	}

	// protected void setComment(String comment, CommitWizardCommitPage page) {
	// try { // HACK: using reflection to gain accessibility
	// Class clazz = page.getClass();
	// Field field = clazz.getDeclaredField("fCommentArea");
	// field.setAccessible(true);
	// Object commentArea = field.get(page);
	// if (commentArea != null && commentArea instanceof CommitCommentArea) {
	// ((CommitCommentArea)commentArea).setProposedComment(comment);
	// }
	// } catch (Exception e) {
	// MylarPlugin.log(e, "could not set comment");
	// }
	// }

	// @Override
	// public void addPages() {
	// super.addPages();
	// commitPage = super.getCommitPage();
	// // setComment(generateComment(task), commitPage);
	// }

	// public boolean canFinish() {
	// return super.canFinish();
	// }
}
