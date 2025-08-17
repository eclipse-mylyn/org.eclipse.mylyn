/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson, Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.gerrit.ui.GerritCompareUi;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.reviews.core.model.IComment;
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

	private final IComment comment;

	public OpenFileUiFactory(IUiContext context, IReviewItemSet set, IFileItem item) {
		this(context, set, item, null);
	}

	public OpenFileUiFactory(IUiContext context, IReviewItemSet set, IFileItem item, IComment comment) {
		super(Messages.OpenFileUiFactory_Open_File, context, set);
		this.item = item;
		this.comment = comment;
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
		GerritCompareUi.openFileComparisonEditor(configuration, item, behavior, comment);
	}

	@Override
	public boolean isExecutable() {
		return getChange() != null;
	}
}
