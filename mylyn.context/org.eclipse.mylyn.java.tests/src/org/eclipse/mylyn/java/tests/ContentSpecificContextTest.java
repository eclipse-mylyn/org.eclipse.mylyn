/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
@SuppressWarnings("nls")
public class ContentSpecificContextTest extends AbstractContextTest {

	public void testEventProcessing() {
		InteractionContext context = new InteractionContext("global-id", new InteractionContextScaling());
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		ContextCore.getContextManager()
				.processInteractionEvent(
						new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "foo-kind", "h0", MOCK_ORIGIN));
		assertEquals(0, context.getAllElements().size());
		ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection("h1"), false, false);
		assertEquals(1, context.getAllElements().size());
		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

	public void testEventProcessingCompositeContext() {
		InteractionContext context1 = new InteractionContext("global-id-1", new InteractionContextScaling());
		InteractionContext context2 = new InteractionContext("global-id-2", new InteractionContextScaling());
		context1.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		context2.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);

		CompositeInteractionContext context = new CompositeInteractionContext(new InteractionContextScaling());
		context.getContextMap().put(context1.getHandleIdentifier(), context1);
		context.getContextMap().put(context2.getHandleIdentifier(), context2);
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		ContextCore.getContextManager()
				.processInteractionEvent(
						new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "foo-kind", "h0", MOCK_ORIGIN));
		assertEquals(0, context.getAllElements().size());
		assertEquals(0, context1.getAllElements().size());
		assertEquals(0, context2.getAllElements().size());
		ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection("h1"), false, false);
		assertEquals(1, context.getAllElements().size());
		assertEquals(1, context1.getAllElements().size());
		assertEquals(1, context2.getAllElements().size());

		context.getContextMap().remove(context2.getHandleIdentifier());

		ContextCore.getContextManager()
				.processInteractionEvent(
						new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "foo-kind", "h0", MOCK_ORIGIN));
		assertEquals(1, context.getAllElements().size());
		assertEquals(1, context1.getAllElements().size());
		assertEquals(1, context2.getAllElements().size());
		ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection("h2"), false, false);
		assertEquals(2, context.getAllElements().size());
		assertEquals(2, context1.getAllElements().size());
		assertEquals(1, context2.getAllElements().size());

		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

}
