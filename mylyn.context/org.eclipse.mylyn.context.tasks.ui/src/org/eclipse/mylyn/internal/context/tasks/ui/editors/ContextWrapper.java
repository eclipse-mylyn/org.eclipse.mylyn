/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.context.tasks.ui.editors;

import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Shawn Minto
 */
class ContextWrapper implements IInteractionContext {

	private IInteractionContext wrappedContext;

	private final ITask task;

	public ContextWrapper(IInteractionContext context, ITask task) {
		this.wrappedContext = context;
		this.task = task;
	}

	public boolean isForSameTaskAs(IInteractionContext context) {
		return context != null && isForSameTaskAs(context.getHandleIdentifier());
	}

	public boolean isForSameTaskAs(String contextHandle) {
		return contextHandle != null && contextHandle.equals(task.getHandleIdentifier());
	}

	public IInteractionContext getWrappedContext() {
		return wrappedContext;
	}

	public void setWrappedContext(IInteractionContext wrappedContext) {
		this.wrappedContext = wrappedContext;
	}

	public String getHandleIdentifier() {
		return wrappedContext.getHandleIdentifier();
	}

	public List<InteractionEvent> getInteractionHistory() {
		return wrappedContext.getInteractionHistory();
	}

	public boolean isInteresting(String elementHandle) {
		return wrappedContext.isInteresting(elementHandle);
	}

	public List<IInteractionElement> getInteresting() {
		return wrappedContext.getInteresting();
	}

	public List<IInteractionElement> getLandmarks() {
		return wrappedContext.getLandmarks();
	}

	public IInteractionElement get(String element) {
		return wrappedContext.get(element);
	}

	public IInteractionElement getActiveNode() {
		return wrappedContext.getActiveNode();
	}

	public void delete(IInteractionElement element) {
		wrappedContext.delete(element);
	}

	public void delete(Collection<IInteractionElement> elements) {
		wrappedContext.delete(elements);
	}

	public void updateElementHandle(IInteractionElement element, String newHandle) {
		wrappedContext.updateElementHandle(element, newHandle);
	}

	public List<IInteractionElement> getAllElements() {
		return wrappedContext.getAllElements();
	}

	public IInteractionContextScaling getScaling() {
		return wrappedContext.getScaling();
	}

	public String getContentLimitedTo() {
		return wrappedContext.getContentLimitedTo();
	}

	public void setContentLimitedTo(String contentLimitedTo) {
		wrappedContext.setContentLimitedTo(contentLimitedTo);
	}

}