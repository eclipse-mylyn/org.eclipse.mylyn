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

package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.jface.action.IAction;

/**
 * An action that is used in a part for a review. This is used to notify other parts when the action has run (e.g. close
 * the annotation popup).
 * 
 * @author Shawn Minto
 */
public interface IReviewAction extends IAction {

	void setActionListener(IReviewActionListener listener);

}
