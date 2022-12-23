/*******************************************************************************
 * Copyright (c) 2009, 2012 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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