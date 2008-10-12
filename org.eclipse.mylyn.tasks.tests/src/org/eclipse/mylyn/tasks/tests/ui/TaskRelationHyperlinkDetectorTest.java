/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
