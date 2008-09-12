/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskFromSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskSelection;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class NewTaskFromSelectionActionTest extends TestCase {

	// Steffen: re-enable?
//	public void testNoSelection() throws Exception {
//		NewTaskFromSelectionAction action = new NewTaskFromSelectionAction();
//		assertNull(action.getTaskSelection());
//		action.run();
//		action.selectionChanged(null);
//		assertNull(action.getTaskSelection());
//	}

	public void testComment() throws Exception {
		StubAttributeFactory targetFactory = new StubAttributeFactory();
		TaskComment comment = new TaskComment(targetFactory, 1);

		NewTaskFromSelectionAction action = new NewTaskFromSelectionAction();
		action.selectionChanged(new RepositoryTaskSelection("id", "server", "kind", "", comment, "summary"));
		assertNotNull(action.getTaskSelection());
	}

	public void testText() throws Exception {
		NewTaskFromSelectionAction action = new NewTaskFromSelectionAction();
		action.selectionChanged(new TextSelection(0, 0) {
			@Override
			public String getText() {
				return "text";
			}
		});
		assertNotNull(action.getTaskSelection());

		action.selectionChanged(new TextSelection(0, 0));
		assertNull(action.getTaskSelection());
	}

	private class StubAttributeFactory extends AbstractAttributeFactory {

		private static final long serialVersionUID = 1L;

		private final Map<String, String> attributeMap = new HashMap<String, String>();

		@Override
		public Date getDateForAttributeType(String attributeKey, String dateString) {
			// ignore
			return null;
		}

		@Override
		public String getName(String key) {
			// ignore
			return null;
		}

		@Override
		public boolean isHidden(String key) {
			// ignore
			return false;
		}

		@Override
		public boolean isReadOnly(String key) {
			// ignore
			return false;
		}

		@Override
		public String mapCommonAttributeKey(String key) {
			String mappedKey = attributeMap.get(key);
			return (mappedKey != null) ? mappedKey : key;
		}

	}
}
