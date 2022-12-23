/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies.
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

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.commons.notifications.feed.ServiceMessageEvent;
import org.eclipse.mylyn.commons.notifications.feed.ServiceMessageEvent.Kind;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListServiceMessageControl;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.ImmutableList;

/**
 * @author Steffen Pingel
 */
public class TaskListServiceMessageControlTest extends TestCase {

	private final class TestTaskListServiceMessageControl extends TaskListServiceMessageControl {
		private TestTaskListServiceMessageControl(Composite parent) {
			super(parent);
		}

		@Override
		protected void closeMessage() {
			super.closeMessage();
		}
	}

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID, "");
	}

	public void testGetAction() {
		assertEquals("abc", TaskListServiceMessageControl.getAction("ABC"));
		assertEquals("abc", TaskListServiceMessageControl.getAction("abc"));
		assertEquals("def", TaskListServiceMessageControl.getAction("http://eclipse.org?action=DEF"));
		assertEquals("defg", TaskListServiceMessageControl.getAction("http://eclipse.org?action=defg&foo=bar"));
		assertEquals(null, TaskListServiceMessageControl.getAction("http://eclipse.org?foo=bar&action=defg"));
	}

	public void testCloseMessageWithNoId() {
		TestTaskListServiceMessageControl control = new TestTaskListServiceMessageControl(WorkbenchUtil.getShell());
		control.setMessage(new ServiceMessage("123"));
		control.closeMessage();
		assertEquals("", getLastMessageId());
	}

	public void testCloseMessageWithId() {
		TestTaskListServiceMessageControl control = new TestTaskListServiceMessageControl(WorkbenchUtil.getShell());
		ServiceMessage message = new ServiceMessage("123");
		message.setId("300");
		control.setMessage(message);
		control.closeMessage();
		assertEquals("300", getLastMessageId());
	}

	public void testHandleEvent() throws Exception {
		TestTaskListServiceMessageControl control = new TestTaskListServiceMessageControl(WorkbenchUtil.getShell());
		handleMessage(control, "123");
		control.closeMessage();
		assertEquals("123", getLastMessageId());

		handleMessage(control, "100");
		control.closeMessage();
		assertEquals("123", getLastMessageId());

		handleMessage(control, "200");
		control.closeMessage();
		assertEquals("200", getLastMessageId());

		handleMessage(control, "org.eclipse.mylyn.reset.1");
		control.closeMessage();
		assertEquals("", getLastMessageId());

		handleMessage(control, "hello");
		control.closeMessage();
		assertEquals("hello", getLastMessageId());
	}

	private String getLastMessageId() {
		return TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID);
	}

	private void handleMessage(TestTaskListServiceMessageControl control, String id) {
		ServiceMessage message = new ServiceMessage("123");
		message.setTitle("Title");
		message.setDescription("Description");
		message.setImage(Dialog.DLG_IMG_HELP);
		message.setId(id);
		control.handleEvent(new ServiceMessageEvent(TasksUiPlugin.getDefault().getServiceMessageManager(),
				Kind.MESSAGE_UPDATE, ImmutableList.of(message)));
	}
}
