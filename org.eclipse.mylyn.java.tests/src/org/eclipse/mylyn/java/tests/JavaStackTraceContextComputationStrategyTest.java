/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.java.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.java.ui.JavaStackTraceContextComputationStrategy;
import org.eclipse.mylyn.internal.java.ui.JavaStackTraceContextComputationStrategy.Element;

/**
 * Test for {@link JavaStackTraceContextComputationStrategy}.
 * 
 * @author David Green
 */
public class JavaStackTraceContextComputationStrategyTest extends TestCase {

	@SuppressWarnings("unused")
	private InteractionContext context;

	private JavaStackTraceContextComputationStrategy stackTraceDetector;

	@Override
	protected void setUp() throws Exception {
		context = new InteractionContext("test", new InteractionContextScaling());
		stackTraceDetector = new JavaStackTraceContextComputationStrategy();
	}

	public void testStackTrace() throws IOException {
		File file = CommonTestUtil.getFile(this, "testdata/taskDescription1.txt");
		List<Element> contextObjects = stackTraceDetector.computeElements(CommonTestUtil.read(file));
		assertFalse(contextObjects.isEmpty());
		assertTrue(contextObjects.contains(new Element("java.lang.NullPointerException", null)));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider", "getAttachmentId")));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider", "getColumnText")));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.jface.viewers.StructuredViewer$UpdateItemSafeRunnable", "run")));
	}

}
