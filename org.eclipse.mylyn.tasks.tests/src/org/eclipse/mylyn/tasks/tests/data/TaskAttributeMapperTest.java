/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.data;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskAttributeMapperTest extends TestCase {

	TaskRepository taskRepository = new TaskRepository("kind", "repository");

	TaskAttributeMapper mapper = new TaskAttributeMapper(taskRepository);

	TaskData data = new TaskData(mapper, "kind", "repository", "id");

	public void testEqualsCommentId() {
		TaskCommentMapper comment = new TaskCommentMapper();
		comment.setCommentId("commentid");
		comment.setNumber(1);

		TaskAttribute attributeOld = data.getRoot().createAttribute("1");
		comment.applyTo(attributeOld);

		TaskAttribute attributeNew = data.getRoot().createAttribute("2");
		comment.applyTo(attributeNew);
		assertTrue(mapper.equals(attributeNew, attributeOld));

		comment.setCommentId("commentid2");
		comment.applyTo(attributeOld);
		assertFalse("Expected not equals:\n" + attributeOld + "\n" + attributeNew,
				mapper.equals(attributeNew, attributeOld));
	}

	public void testEqualsCommentMylyn37() {
		TaskCommentMapper comment = new TaskCommentMapper();
		comment.setNumber(1);

		// Mylyn 3.7 before release
		TaskAttribute attributeOld = data.getRoot().createAttribute("1");
		comment.applyTo(attributeOld);
		attributeOld.setValue("1");

		// Mylyn 3.7 release
		TaskAttribute attributeNew = data.getRoot().createAttribute("2");
		comment.applyTo(attributeNew);
		assertEquals("", attributeNew.getValue());

		assertTrue("Expected equals:\n" + attributeOld + "\n" + attributeNew, mapper.equals(attributeNew, attributeOld));
		assertFalse("Expected not equals:\n" + attributeOld + "\n" + attributeNew,
				mapper.equals(attributeOld, attributeNew));
	}

	public void testEqualsCommentMylyn37CommentId() {
		TaskCommentMapper comment = new TaskCommentMapper();
		comment.setNumber(1);
		comment.setCommentId("2");

		// Mylyn 3.7 before release, but missing sub-attribute
		TaskAttribute attributeOld = data.getRoot().createAttribute("1");
		comment.applyTo(attributeOld);
		attributeOld.setValue("1");

		// Mylyn 3.7 release
		TaskAttribute attributeNew = data.getRoot().createAttribute("2");
		comment.applyTo(attributeNew);
		assertEquals("2", attributeNew.getValue());

		assertFalse("Expected not equals:\n" + attributeOld + "\n" + attributeNew,
				mapper.equals(attributeNew, attributeOld));

		// Mylyn 3.7 before release with ID sub-attribute
		TaskAttribute idAttribute = attributeOld.createAttribute("task.common.comment.id");
		idAttribute.setValue("2");
		assertTrue("Expected equals:\n" + attributeOld + "\n" + attributeNew, mapper.equals(attributeNew, attributeOld));

		// IDs should now be unequal
		idAttribute.setValue("3");
		assertFalse("Expected not equals:\n" + attributeOld + "\n" + attributeNew,
				mapper.equals(attributeNew, attributeOld));
	}

}
