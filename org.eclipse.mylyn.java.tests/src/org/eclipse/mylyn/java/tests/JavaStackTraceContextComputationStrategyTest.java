/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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

	/**
	 * bug 354184
	 */
	public void testStackTracePopulatedWithoutEclipseErrorHeaderElements() throws IOException {
		File file = CommonTestUtil.getFile(this, "testdata/taskDescription2.txt");
		List<Element> contextObjects = stackTraceDetector.computeElements(CommonTestUtil.read(file));
		assertFalse(contextObjects.isEmpty());

		//                don't want
		//                                Element [fqn=Date, methodName=null]
		//                                Element [fqn=Message, methodName=null]
		//                                Element [fqn=Severity, methodName=null]
		//                                Element [fqn=Product, methodName=null]
		//                                Element [fqn=Plugin, methodName=null]
		//      do want
		//                                Element [fqn=org.eclipse.swt.SWTException, methodName=null]
		//                                Element [fqn=org.eclipse.swt.SWT, methodName=error]
		//                                Element [fqn=org.eclipse.swt.SWT, methodName=error]
		//                                Element [fqn=org.eclipse.swt.SWT, methodName=error]
		//                                Element [fqn=org.eclipse.swt.graphics.Image, methodName=getBounds]

		//

		assertFalse(contextObjects.contains(new Element("Date", null)));
		assertFalse(contextObjects.contains(new Element("Message", null)));
		assertFalse(contextObjects.contains(new Element("Severity", null)));
		assertFalse(contextObjects.contains(new Element("Product", null)));
		assertFalse(contextObjects.contains(new Element("Plugin", null)));

		assertTrue(contextObjects.contains(new Element("org.eclipse.swt.SWTException", null)));
		assertTrue(contextObjects.contains(new Element("org.eclipse.swt.SWT", "error")));
		assertTrue(contextObjects.contains(new Element("org.eclipse.swt.graphics.Image", "getBounds")));
	}
}
