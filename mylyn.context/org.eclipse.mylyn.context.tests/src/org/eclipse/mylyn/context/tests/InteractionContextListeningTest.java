/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

import junit.framework.TestCase;

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
			contextManager.addListener(new AbstractContextListener() {
				@Override
				public void contextChanged(ContextChangeEvent event) {
					switch (event.getEventKind()) {
						case ACTIVATED:
							contextManager.addListener(listener);
							contextManager.removeListener(listener);
							break;
					}
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

	private class StubContextListener extends AbstractContextListener {

		private int activationEventCount;

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
				case ACTIVATED:
					contextManager.removeListener(this);
					activationEventCount++;
					break;
			}
		}

	}
}