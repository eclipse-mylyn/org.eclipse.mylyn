/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.dsl;

/**
 * 
 * @author mattk
 *
 */
public interface IReviewDslMapper {

	public abstract ReviewDslResult parseReviewResult(String text)
			throws ParseException;

	public abstract ReviewDslScope parseReviewScope(String text)
			throws ParseException;

	public abstract ReviewDslResult parseChangedReviewScope(String text);

}