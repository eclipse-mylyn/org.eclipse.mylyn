/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 */
public class TaskTransfer extends ByteArrayTransfer {

	private static final TaskTransfer INSTANCE = new TaskTransfer();

	private static final String TYPE_NAME = "task-transfer-format:" + System.currentTimeMillis() + ":"
			+ INSTANCE.hashCode();

	private static final int TYPEID = registerType(TYPE_NAME);

	private TaskTransfer() {
	}

	public static TaskTransfer getInstance() {
		return INSTANCE;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object data, TransferData transferData) {
		if (!(data instanceof AbstractTask[])) {
			return;
		}

		AbstractTask[] tasks = (AbstractTask[]) data;
		int resourceCount = tasks.length;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(out);

			//write the number of resources
			dataOut.writeInt(resourceCount);

			//write each resource
			for (int i = 0; i < tasks.length; i++) {
				writeResource(dataOut, tasks[i]);
			}

			//cleanup
			dataOut.close();
			out.close();
			byte[] bytes = out.toByteArray();
			super.javaToNative(bytes, transferData);
		} catch (IOException e) {
			//it's best to send nothing if there were problems
		}
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes == null) {
			return null;
		}
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			int count = in.readInt();
			AbstractTask[] results = new AbstractTask[count];
			for (int i = 0; i < count; i++) {
				results[i] = readTask(in);
			}
			return results;
		} catch (IOException e) {
			return null;
		}
	}

	private AbstractTask readTask(DataInputStream dataIn) throws IOException {
		String handle = dataIn.readUTF();
		return TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
	}

	private void writeResource(DataOutputStream dataOut, AbstractTask task) throws IOException {
		dataOut.writeUTF(task.getHandleIdentifier());
	}
}
