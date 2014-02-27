/*******************************************************************************
 * Copyright (c) 2013 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 * @author Sebastien Dubois
 */
public class OpenFileUiFactory extends AbstractPatchSetUiFactory {

	private final IFileItem item;

	public OpenFileUiFactory(IUiContext context, IReviewItemSet set, IFileItem item) {
		super(Messages.OpenFileUiFactory_Open_File, context, set);
		this.item = item;
	}

	@Override
	public void execute() {
		//(The action is always available so isExecutable is never called from framework.)
		if (!isExecutable()) {
			handleExecutionStateError();
			return;
		}

		if (item.getBase() == null || item.getTarget() == null) {
			getEditor().setMessage(Messages.OpenFileUiFactory_File_not_available, IMessageProvider.WARNING);
			return;
		}

		GerritReviewBehavior behavior = new GerritReviewBehavior(getTask(), resolveGitRepository());
		CompareConfiguration configuration = new CompareConfiguration();
		CompareUI.openCompareEditor(new FileItemCompareEditorInput(configuration, item, behavior));
	}

	@Override
	public boolean isExecutable() {
		return getChange() != null;
	}
}
