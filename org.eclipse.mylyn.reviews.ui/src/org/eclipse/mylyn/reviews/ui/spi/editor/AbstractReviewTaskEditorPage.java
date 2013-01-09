/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

/**
 * Marks task editor as providing Review model for extending classes.
 * 
 * @author Miles Parker
 */
public abstract class AbstractReviewTaskEditorPage extends AbstractTaskEditorPage {

	public AbstractReviewTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
	}

	/**
	 * Returns the current review. All instances should provide one open, accessible review model instance at init time,
	 * and that review should be constant throughout the editor life-cycle.
	 */
	public abstract IReview getReview();
}
