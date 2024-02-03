/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Provides a hook for accessing the part when opening an element in an editor. This class should only be accessed from the SWT thread.
 * 
 * @author Steffen Pingel
 */
public class EditorHandle implements IAdaptable {

	private Object item;

	private IWorkbenchPart part;

	private IStatus status;

	private final CountDownLatch progressLatch = new CountDownLatch(1);

	/**
	 * Constructs a handle with a status.
	 * 
	 * @param status
	 *            specifies the result of opening the editor
	 * @see #getStatus()
	 */
	public EditorHandle(IStatus status) {
		this.status = status;
	}

	public EditorHandle() {
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * Returns the item that was opened.
	 * 
	 * @return null, if no item is associated with the editor
	 */
	public Object getItem() {
		return item;
	}

	/**
	 * Returns the editor part.
	 * 
	 * @return the editor or null if the editor is not open, yet, or does not a workbench part
	 */
	public IWorkbenchPart getPart() {
		return part;
	}

	/**
	 * Returns the result of opening the editor.
	 * 
	 * @return a severity of {@link IStatus#OK} indicates that the operation was successful.
	 */
	public IStatus getStatus() {
		return status;
	}

	/**
	 * Sets the item that was opened.
	 * 
	 * @see {@link #getItem()}
	 */
	public void setItem(Object item) {
		this.item = item;
	}

	/**
	 * Sets the editor part that was opened.
	 * 
	 * @see {@link #getPart()}
	 */
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}

	/**
	 * Sets the result of the open operation.
	 * 
	 * @see {@link #getStatus()}
	 */
	public void setStatus(IStatus status) {
		this.status = status;
		progressLatch.countDown();
	}

	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return progressLatch.await(timeout, unit);
	}

}
