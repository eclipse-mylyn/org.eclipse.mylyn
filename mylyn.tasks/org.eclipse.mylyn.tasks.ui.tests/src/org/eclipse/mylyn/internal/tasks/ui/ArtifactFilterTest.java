/*******************************************************************************
 * Copyright (c) 2015 Vaughan Hilts and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Vaughan Hilts - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

/**
 * @author Vaughan Hilts
 */
@SuppressWarnings("restriction")
public class ArtifactFilterTest {

	@Test
	public void testReviewArtifactIsFiltered() {
		ITask task = mock(ITask.class);
		when(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_ARTIFACT)).thenReturn("true");
		TaskReviewArtifactFilter filter = new TaskReviewArtifactFilter();

		boolean wasFiltered = !filter.select(null, task);
		assertTrue(wasFiltered);
	}

	@Test
	public void testNonReviewArtifactIsntFiltered() {
		ITask task = mock(ITask.class);
		when(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_ARTIFACT)).thenReturn("false");
		TaskReviewArtifactFilter filter = new TaskReviewArtifactFilter();

		boolean wasFiltered = !filter.select(null, task);
		assertFalse(wasFiltered);
	}

	@Test
	public void testUnclassifiedReviewArtifactIsntFiltered() {
		// If a task is yet to be determined, we should not filter it
		ITask task = mock(ITask.class);
		TaskReviewArtifactFilter filter = new TaskReviewArtifactFilter();

		boolean wasFiltered = !filter.select(null, task);
		assertFalse(wasFiltered);
	}

	@Test
	public void testArbitaryElementsAreNotFiltered() {
		Object element = new Object();

		TaskReviewArtifactFilter filter = new TaskReviewArtifactFilter();
		boolean wasFiltered = !filter.select(null, element);
		assertFalse(wasFiltered);
	}

}
