/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.tests.support.DomContextReader;
import org.eclipse.mylyn.context.tests.support.DomContextWriter;
import org.eclipse.mylyn.context.tests.support.FileTool;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextExternalizer;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.SaxContextReader;
import org.eclipse.mylyn.internal.context.core.ScalingFactors;

/**
 * @author Mik Kersten
 */
public class ContextExternalizerTest extends AbstractContextTest {

	private InteractionContext context;

	private ScalingFactors scaling;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scaling = new ScalingFactors();
		context = new InteractionContext("context-externalization", new ScalingFactors());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testContentAttributeExternalization() {
		// Gets a file to write to and creates contexts folder if necessary
		File file = ContextCorePlugin.getContextManager().getFileForContext(context.getHandleIdentifier());
		file.deleteOnExit();
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		context.parseEvent(mockSelection("1"));
		context.setContentLimitedTo("foobar");
		
		externalizer.writeContextToXml(context, file);

		File dataDirectory = ContextCorePlugin.getDefault().getContextStore().getRootDirectory();
		File contextsDirectory = new File(dataDirectory, "contexts"/*WorkspaceAwareContextStore.CONTEXTS_DIRECTORY*/);
		File zippedContextFile = new File(contextsDirectory, context.getHandleIdentifier()
				+ InteractionContextManager.CONTEXT_FILE_EXTENSION);
		assertTrue(zippedContextFile.exists());
		InteractionContext loaded = externalizer.readContextFromXML("handle", zippedContextFile);
		assertNotNull(loaded);
		
		assertEquals("foobar", loaded.getContentLimitedTo());
	}
	
	public void testSaxExternalizationAgainstDom() {
		File file = FileTool.getFileInPlugin(MylarCoreTestsPlugin.getDefault(), new Path(
				"testdata/externalizer/testcontext.xml.zip"));
		assertTrue(file.getAbsolutePath(), file.exists());
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		externalizer.setReader(new DomContextReader());
		InteractionContext domReadContext = externalizer.readContextFromXML("handle", file);

		externalizer.setReader(new SaxContextReader());
		InteractionContext saxReadContext = externalizer.readContextFromXML("handle", file);
		assertEquals(284, saxReadContext.getInteractionHistory().size()); // known
		// from
		// testdata
		assertEquals(domReadContext, saxReadContext);

		externalizer.setWriter(new DomContextWriter());
		File domOut = new File("dom-out.xml");
		domOut.deleteOnExit();
		externalizer.writeContextToXml(saxReadContext, domOut);

		externalizer.setWriter(new DomContextWriter());
		File saxOut = new File("sax-out.xml");
		saxOut.deleteOnExit();
		externalizer.writeContextToXml(saxReadContext, saxOut);
		assertEquals(domOut.length(), saxOut.length());

		externalizer.setReader(new DomContextReader());
		InteractionContext domReadAfterWrite = externalizer.readContextFromXML("handle", file);
		externalizer.setReader(new SaxContextReader());
		InteractionContext saxReadAfterWrite = externalizer.readContextFromXML("handle", file);

		assertEquals(domReadAfterWrite, saxReadAfterWrite);
	}

	public void testContextSize() {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		String path = "extern.xml";
		File file = new File(path);
		file.deleteOnExit();

		int ORIGINAL = 100;
		for (int i = 0; i < ORIGINAL; i++) {
			context.parseEvent(mockSelection("1"));
			context.parseEvent(mockPreferenceChange("2"));
		}
		context.collapse();
		externalizer.writeContextToXml(context, file);
		long size = file.length();

		context.reset();
		for (int i = 0; i < ORIGINAL * ORIGINAL; i++) {
			context.parseEvent(mockSelection("1"));
			context.parseEvent(mockPreferenceChange("2"));
		}
		context.collapse();
		externalizer.writeContextToXml(context, file);
		long size2 = file.length();
		assertTrue(size <= size2 * 2);
	}

	public void testExternalization() {
		// Gets a file to write to and creates contexts folder if necessary
		File file = ContextCorePlugin.getContextManager().getFileForContext(context.getHandleIdentifier());
		file.deleteOnExit();
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

		IInteractionElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockNavigation("2"));
		IInteractionRelation edge = node.getRelation("2");
		assertNotNull(edge);
		assertEquals(1, node.getRelations().size());
		context.parseEvent(mockInterestContribution("3", scaling.getLandmark() + scaling.getDecay().getValue() * 3));
		assertTrue("interest: " + context.get("3").getInterest().getValue(), context.get("3").getInterest()
				.isLandmark());
		float doi = node.getInterest().getValue();
		assertNotNull(context.getLandmarkMap());

		// "3" not a user event
		assertEquals("2", context.getActiveNode().getHandleIdentifier());

		externalizer.writeContextToXml(context, file);

		File dataDirectory = ContextCorePlugin.getDefault().getContextStore().getRootDirectory();
		File contextsDirectory = new File(dataDirectory, "contexts"/*WorkspaceAwareContextStore.CONTEXTS_DIRECTORY*/);
		File zippedContextFile = new File(contextsDirectory, context.getHandleIdentifier()
				+ InteractionContextManager.CONTEXT_FILE_EXTENSION);
		assertTrue(zippedContextFile.exists());
		InteractionContext loaded = externalizer.readContextFromXML("handle", zippedContextFile);
		assertNotNull(loaded);
		assertEquals(3, loaded.getInteractionHistory().size());
		IInteractionElement loadedNode = loaded.get("1");
		IInteractionRelation edgeNode = loadedNode.getRelation("2");
		assertNotNull(edgeNode);
		assertEquals(1, loadedNode.getRelations().size());

		IInteractionElement landmark = loaded.get("3");
		assertNotNull(loadedNode);
		assertEquals(doi, loadedNode.getInterest().getValue());
		assertTrue(landmark.getInterest().isLandmark());
		assertNotNull(loaded.getLandmarkMap());

		assertEquals("2", loaded.getActiveNode().getHandleIdentifier());
	}

}
