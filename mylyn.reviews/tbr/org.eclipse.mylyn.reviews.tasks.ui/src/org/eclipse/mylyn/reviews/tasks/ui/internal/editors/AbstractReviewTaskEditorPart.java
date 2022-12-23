/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;

public abstract class AbstractReviewTaskEditorPart  extends AbstractTaskEditorPart{

	private ReviewTaskEditorPage reviewPage;
public AbstractReviewTaskEditorPart() {
super();
}
	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		reviewPage = (ReviewTaskEditorPage)taskEditorPage;
	}
	protected ReviewTaskEditorPage getReviewPage() {
		return reviewPage;
	}
}
