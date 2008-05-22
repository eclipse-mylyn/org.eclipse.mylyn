/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

/**
 * @author Steffen Pingel
 */
public class InteractionContextListeningTest extends TestCase {

	private final InteractionContext mockContext = new InteractionContext("doitest", new InteractionContextScaling());

	private InteractionContextManager contextManager;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		contextManager.deactivateAllContexts();
	}

	public void testAddRemoveListenerInContextActivated() {
		contextManager = ContextCorePlugin.getContextManager();
		((CompositeInteractionContext) contextManager.getActiveContext()).getContextMap().put("handle", mockContext);

		final StubContextListener listener = new StubContextListener();
		try {
			contextManager.addListener(new ContextListenerAdapter() {
				@Override
				public void contextActivated(IInteractionContext arg0) {
					contextManager.addListener(listener);
					contextManager.removeListener(listener);
				}
			});
			contextManager.activateContext("handle");

			contextManager.deactivateContext("handle");
			contextManager.activateContext("handle");

			assertEquals(0, listener.activationEventCount);
		} finally {
			// clean up just in case
			contextManager.removeListener(listener);
		}
	}

	private class StubContextListener extends ContextListenerAdapter {

		private int activationEventCount;

		@Override
		public void contextActivated(IInteractionContext context) {
			contextManager.removeListener(this);
			activationEventCount++;
		}

	}

	private class ContextListenerAdapter extends AbstractContextListener {

		@Override
		public void contextActivated(IInteractionContext context) {
			// ignore
		}

		@Override
		public void contextCleared(IInteractionContext context) {
			// ignore
		}

		@Override
		public void contextDeactivated(IInteractionContext context) {
			// ignore
		}

		@Override
		public void contextPreActivated(IInteractionContext context) {
			// ignore	
		}

		@Override
		public void elementsDeleted(List<IInteractionElement> elements) {
			// ignore
		}

		@Override
		public void interestChanged(List<IInteractionElement> elements) {
			// ignore
		}

		@Override
		public void landmarkAdded(IInteractionElement element) {
			// ignore
		}

		@Override
		public void landmarkRemoved(IInteractionElement element) {
			// ignore
		}
	}

}