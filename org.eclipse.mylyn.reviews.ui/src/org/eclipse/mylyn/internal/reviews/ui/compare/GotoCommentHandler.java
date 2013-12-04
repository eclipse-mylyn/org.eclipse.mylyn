/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

final class GotoCommentHandler extends AbstractHandler {
	private final Direction direction;

	private final ReviewCompareAnnotationSupport support;

	GotoCommentHandler(Direction direction, ReviewCompareAnnotationSupport support) {
		this.direction = direction;
		this.support = support;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		support.gotoAnnotation(direction);

		// ignore
		return null;
	}
}