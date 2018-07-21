/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskRelationHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * @author Steffen Pingel
 */
public class TaskRelationHyperlinkDetectorTest extends TaskHyperlinkDetectorTest {

	@Override
	protected AbstractTaskHyperlinkDetector createHyperlinkDetector() {
		TaskRelationHyperlinkDetector detector = new TaskRelationHyperlinkDetector() {
			@Override
			protected TaskRepository getTaskRepository(ITextViewer textViewer) {
				return repository;
			}
		};
		return detector;
	}

}
