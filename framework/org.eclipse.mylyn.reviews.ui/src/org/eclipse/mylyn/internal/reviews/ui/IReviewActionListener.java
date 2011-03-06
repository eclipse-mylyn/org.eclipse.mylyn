package org.eclipse.mylyn.internal.reviews.ui;

/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

import org.eclipse.jface.action.Action;

/**
 * Listener for when an IReviewAction has ran
 * 
 * @author Shawn Minto
 */
public interface IReviewActionListener {

	void actionRan(Action action);

	void actionAboutToRun(Action action);

}
