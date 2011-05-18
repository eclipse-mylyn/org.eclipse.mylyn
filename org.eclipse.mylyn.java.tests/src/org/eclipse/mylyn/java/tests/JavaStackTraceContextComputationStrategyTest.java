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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.java.ui.JavaStackTraceContextComputationStrategy;
import org.eclipse.mylyn.internal.java.ui.JavaStackTraceContextComputationStrategy.Element;

/**
 * test for {@link JavaStackTraceContextComputationStrategy}
 * 
 * @author David Green
 */
public class JavaStackTraceContextComputationStrategyTest extends TestCase {

	private InteractionContext context;

	private JavaStackTraceContextComputationStrategy stackTraceDetector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = new InteractionContext("test", new InteractionContextScaling());
		stackTraceDetector = new JavaStackTraceContextComputationStrategy();
	}

	public void testStackTrace() throws IOException {
		List<Element> contextObjects = stackTraceDetector.computeElements(getContent("resources/taskDescription1.txt"));
		assertFalse(contextObjects.isEmpty());
		assertTrue(contextObjects.contains(new Element("java.lang.NullPointerException", null)));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider", "getAttachmentId")));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider", "getColumnText")));
		assertTrue(contextObjects.contains(new Element(
				"org.eclipse.jface.viewers.StructuredViewer$UpdateItemSafeRunnable", "run")));
	}

	private String getContent(String path) throws IOException {
		InputStream in = JavaStackTraceContextComputationStrategyTest.class.getResourceAsStream(path);
		if (in == null) {
			throw new IllegalStateException(path);
		}
		try {
			StringWriter writer = new StringWriter();
			Reader reader = new InputStreamReader(new BufferedInputStream(in), "utf-8");
			int i;
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}
			return writer.toString();
		} finally {
			in.close();
		}
	}
}
