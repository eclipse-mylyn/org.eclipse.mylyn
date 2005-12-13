/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.core.tests;

import java.io.File;

import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextExternalizer;
import org.eclipse.mylar.core.internal.ScalingFactors;

/**
 * @author Mik Kersten
 */
public class ContextExternalizationTest extends AbstractContextTest {

	private MylarContext context;

	private ScalingFactors scaling;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scaling = new ScalingFactors();
		context = new MylarContext("context-externalization", new ScalingFactors());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testContextSize() {
		MylarContextExternalizer externalizer = new MylarContextExternalizer();
		String path = "extern.xml";
		File file = new File(path);
		file.deleteOnExit();

		int ORIGINAL = 100;
		for (int i = 0; i < ORIGINAL; i++) {
			context.parseEvent(mockSelection("1"));
			context.parseEvent(mockPreferenceChange("2"));
		}
		context.collapse();
		externalizer.writeContextToXML(context, file);
		long size = file.length();

		context.reset();
		for (int i = 0; i < ORIGINAL * ORIGINAL; i++) {
			context.parseEvent(mockSelection("1"));
			context.parseEvent(mockPreferenceChange("2"));
		}
		context.collapse();
		externalizer.writeContextToXML(context, file);
		long size2 = file.length();
		assertTrue(size <= size2 * 2);
	}

	public void testExternalization() {
		MylarContextExternalizer externalizer = new MylarContextExternalizer();
		String path = "extern.xml";
		File file = new File(path);
		file.deleteOnExit();

		IMylarElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockNavigation("2"));
		IMylarRelation edge = node.getRelation("2");
		assertNotNull(edge);
		assertEquals(1, node.getRelations().size());
		context.parseEvent(mockInterestContribution("3", scaling.getLandmark() + scaling.getDecay().getValue() * 3));
		assertTrue("interest: " + context.get("3").getInterest().getValue(), context.get("3").getInterest()
				.isLandmark());
		float doi = node.getInterest().getValue();
		assertNotNull(context.getLandmarks());

		// "3" not a user event
		assertEquals("2", context.getActiveNode().getHandleIdentifier());

		externalizer.writeContextToXML(context, file);
		MylarContext loaded = externalizer.readContextFromXML(file);
		assertNotNull(loaded);
		assertEquals(3, loaded.getInteractionHistory().size());
		IMylarElement loadedNode = loaded.get("1");
		IMylarRelation edgeNode = loadedNode.getRelation("2");
		assertNotNull(edgeNode);
		assertEquals(1, loadedNode.getRelations().size());

		IMylarElement landmark = loaded.get("3");
		assertNotNull(loadedNode);
		assertEquals(doi, loadedNode.getInterest().getValue());
		assertTrue(landmark.getInterest().isLandmark());
		assertNotNull(loaded.getLandmarks());

		assertEquals("2", loaded.getActiveNode().getHandleIdentifier());
	}

}
