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
		wrappedContext = context;
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

	@Override
	public String getHandleIdentifier() {
		return wrappedContext.getHandleIdentifier();
	}

	@Override
	public List<InteractionEvent> getInteractionHistory() {
		return wrappedContext.getInteractionHistory();
	}

	@Override
	public boolean isInteresting(String elementHandle) {
		return wrappedContext.isInteresting(elementHandle);
	}

	@Override
	public List<IInteractionElement> getInteresting() {
		return wrappedContext.getInteresting();
	}

	@Override
	public List<IInteractionElement> getLandmarks() {
		return wrappedContext.getLandmarks();
	}

	@Override
	public IInteractionElement get(String element) {
		return wrappedContext.get(element);
	}

	@Override
	public IInteractionElement getActiveNode() {
		return wrappedContext.getActiveNode();
	}

	@Override
	public void delete(IInteractionElement element) {
		wrappedContext.delete(element);
	}

	@Override
	public void delete(Collection<IInteractionElement> elements) {
		wrappedContext.delete(elements);
	}

	@Override
	public void updateElementHandle(IInteractionElement element, String newHandle) {
		wrappedContext.updateElementHandle(element, newHandle);
	}

	@Override
	public List<IInteractionElement> getAllElements() {
		return wrappedContext.getAllElements();
	}

	@Override
	public IInteractionContextScaling getScaling() {
		return wrappedContext.getScaling();
	}

	@Override
	public String getContentLimitedTo() {
		return wrappedContext.getContentLimitedTo();
	}

	@Override
	public void setContentLimitedTo(String contentLimitedTo) {
		wrappedContext.setContentLimitedTo(contentLimitedTo);
	}

}