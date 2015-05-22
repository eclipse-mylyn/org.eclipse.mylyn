/*******************************************************************************
 * Copyright (c) 2009, 2012 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import org.eclipse.jface.text.source.LineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;

public interface IReviewCompareSourceViewer {

	LineRange getSelection();

	void focusOnLines(ILocation ranges);

	void registerContextMenu();

	ReviewAnnotationModel getAnnotationModel();

}