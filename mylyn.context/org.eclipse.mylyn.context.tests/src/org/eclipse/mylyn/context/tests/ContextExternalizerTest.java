/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.context.tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.tests.support.DomContextReader;
import org.eclipse.mylyn.context.tests.support.DomContextWriter;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextExternalizer;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.SaxContextReader;
import org.eclipse.mylyn.internal.context.core.SaxContextWriter;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
@SuppressWarnings("nls")
public class ContextExternalizerTest extends AbstractContextTest {

	private static final String CONTEXT_HANDLE = "context-externalization";

	private InteractionContext context;

	private IInteractionContextScaling scaling;

	private File contextFile;

	private AbstractContextContributor contributor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scaling = ContextCore.getCommonContextScaling();
		context = new InteractionContext(CONTEXT_HANDLE, ContextCore.getCommonContextScaling());
		assertNotNull(ContextCore.getContextManager());
	}

	@Override
	protected void tearDown() throws Exception {
		if (contextFile != null && contextFile.exists()) {
			contextFile.delete();
		}

		if (contributor != null) {
			ContextCorePlugin.getDefault().removeContextContributor(contributor);
		}
		super.tearDown();
	}

	public void testContentAttributeExternalization() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		context.parseEvent(mockSelection("1"));
		context.setContentLimitedTo("foobar");

		IInteractionContext loaded = writeAndReadContext(context, externalizer);

		assertEquals("foobar", loaded.getContentLimitedTo());
	}

	public void testSaxExternalizationAgainstDom() throws Exception {
		File file = CommonTestUtil.getFile(this, "testdata/externalizer/testcontext.xml.zip");
		assertTrue(file.getAbsolutePath(), file.exists());
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		IInteractionContext domReadContext = externalizer.readContextFromXml(CONTEXT_HANDLE, file,
				new DomContextReader(), scaling);

		IInteractionContext saxReadContext = externalizer.readContextFromXml(CONTEXT_HANDLE, file,
				new SaxContextReader(), scaling);
		assertEquals(284, saxReadContext.getInteractionHistory().size()); // known from testdata
		assertEquals(domReadContext, saxReadContext);

		File domOut = new File("dom-out.xml");
		domOut.deleteOnExit();
		externalizer.writeContextToXml(saxReadContext, domOut, new DomContextWriter());

		File saxOut = new File("sax-out.xml");
		saxOut.deleteOnExit();
		externalizer.writeContextToXml(domReadContext, saxOut, new SaxContextWriter());

		IInteractionContext domReadAfterWrite = externalizer.readContextFromXml(CONTEXT_HANDLE, saxOut,
				new DomContextReader(), scaling);
		IInteractionContext saxReadAfterWrite = externalizer.readContextFromXml(CONTEXT_HANDLE, domOut,
				new SaxContextReader(), scaling);

		assertEquals(domReadAfterWrite, saxReadAfterWrite);
	}

	public void testContextSize() throws Exception {
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

	public void testExternalization() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

		IInteractionElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockNavigation("2"));
		IInteractionRelation edge = node.getRelation("2");
		assertNotNull(edge);
		assertEquals(1, node.getRelations().size());
		context.parseEvent(mockInterestContribution("3", scaling.getLandmark() + scaling.getDecay() * 3));
		assertTrue("interest: " + context.get("3").getInterest().getValue(),
				context.get("3").getInterest().isLandmark());
		float doi = node.getInterest().getValue();
		assertNotNull(context.getLandmarks());

		// "3" not a user event
		assertEquals("2", context.getActiveNode().getHandleIdentifier());

		IInteractionContext loaded = writeAndReadContext(context, externalizer);
		assertEquals(3, loaded.getInteractionHistory().size());
		IInteractionElement loadedNode = loaded.get("1");
		IInteractionRelation edgeNode = loadedNode.getRelation("2");
		assertNotNull(edgeNode);
		assertEquals(1, loadedNode.getRelations().size());

		IInteractionElement landmark = loaded.get("3");
		assertNotNull(loadedNode);
		assertEquals(doi, loadedNode.getInterest().getValue());
		assertTrue(landmark.getInterest().isLandmark());
		assertNotNull(loaded.getLandmarks());

		assertEquals("2", loaded.getActiveNode().getHandleIdentifier());
	}

	/**
	 * What is written and read from disk should always return the same doi for an element when the context is collapsed
	 * 
	 * @throws Exception
	 */
	public void testExternalizationWithCollapse() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

		// create nodes in the context and ensure that writing and reading work properly
		IInteractionElement node1 = context.parseEvent(mockSelection("1"));
		IInteractionElement node2 = context.parseEvent(mockSelection("2"));
		context.parseEvent(mockSelection("2"));
		context.parseEvent(mockSelection("2"));

		float doi1 = node1.getInterest().getValue();
		float doi2 = node2.getInterest().getValue();

		int numEvents = context.getUserEventCount();

		// key to this test
		context.collapse();
		InteractionContext loadedContext = (InteractionContext) writeAndReadContext(context, externalizer);

		assertEquals(numEvents, loadedContext.getUserEventCount());

		IInteractionElement loadedNode1 = loadedContext.get("1");
		IInteractionElement loadedNode2 = loadedContext.get("2");

		assertEquals(doi1, loadedNode1.getInterest().getValue());
		assertEquals(doi2, loadedNode2.getInterest().getValue());

		//
		// try to write a second time without changes
		//

		// key to this test
		loadedContext.collapse();
		InteractionContext loadedContext2 = (InteractionContext) writeAndReadContext(loadedContext, externalizer);

		assertEquals(numEvents, loadedContext2.getUserEventCount());

		loadedNode1 = loadedContext2.get("1");
		loadedNode2 = loadedContext2.get("2");

		assertEquals(doi1, loadedNode1.getInterest().getValue());
		assertEquals(doi2, loadedNode2.getInterest().getValue());

		//
		// try to change the context that was read and write again
		//
		node1 = loadedContext2.parseEvent(mockSelection("1"));
		node2 = loadedContext2.parseEvent(mockSelection("2"));
		loadedContext2.parseEvent(mockSelection("2"));
		loadedContext2.parseEvent(mockSelection("1"));

		doi1 = node1.getInterest().getValue();
		doi2 = node2.getInterest().getValue();

		numEvents = loadedContext2.getUserEventCount();

		loadedContext2.collapse();

		InteractionContext loadedContext3 = (InteractionContext) writeAndReadContext(loadedContext2, externalizer);

		assertEquals(numEvents, loadedContext3.getUserEventCount());

		loadedNode1 = loadedContext3.get("1");
		loadedNode2 = loadedContext3.get("2");

		assertEquals(doi1, loadedNode1.getInterest().getValue());
		assertEquals(doi2, loadedNode2.getInterest().getValue());
	}

	private IInteractionContext writeAndReadContext(InteractionContext contextToWrite,
			InteractionContextExternalizer externalizer) throws Exception {
		File file = ContextCorePlugin.getContextStore().getFileForContext(contextToWrite.getHandleIdentifier());
		file.deleteOnExit();
		externalizer.writeContextToXml(contextToWrite, file);

		// TODO: fix up directory refs
		File dataDirectory = ContextCorePlugin.getContextStore().getContextDirectory().getParentFile();
		File contextsDirectory = new File(dataDirectory, "contexts"/*WorkspaceAwareContextStore.CONTEXTS_DIRECTORY*/);
		File zippedContextFile = new File(contextsDirectory,
				contextToWrite.getHandleIdentifier() + InteractionContextManager.CONTEXT_FILE_EXTENSION);
		assertTrue(zippedContextFile.exists());
		IInteractionContext loaded = externalizer.readContextFromXml(CONTEXT_HANDLE, zippedContextFile, scaling);
		assertNotNull(loaded);
		return loaded;
	}

	public void testReadOtherContextHandle() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

		context.setHandleIdentifier("handle-1");
		context.parseEvent(mockSelection("1"));
		File file1 = File.createTempFile("context", null);
		file1.deleteOnExit();
		externalizer.writeContextToXml(context, file1);

		context.setHandleIdentifier("handle-2");
		context.parseEvent(mockSelection("2"));
		File file2 = File.createTempFile("context", null);
		file2.deleteOnExit();
		externalizer.writeContextToXml(context, file2);

		context = (InteractionContext) externalizer.readContextFromXml("handle-1", file1, scaling);
		assertNotNull(context);
		assertEquals(1, context.getAllElements().size());

		context = (InteractionContext) externalizer.readContextFromXml("handle-1", file2, scaling);
		assertNotNull(context);
		assertEquals(2, context.getAllElements().size());

		context = (InteractionContext) externalizer.readContextFromXml("abc", file1, scaling);
		assertNotNull(context);
		assertEquals(1, context.getAllElements().size());
	}

	public void testReadInvalidContextHandle() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		File file = File.createTempFile("context", null);
		file.deleteOnExit();
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
			ZipEntry entry = new ZipEntry("name");
			out.putNextEntry(entry);
		}

		context = (InteractionContext) externalizer.readContextFromXml("abc", file, scaling);
		assertNull(context);
	}

	public void testAddContextContributor() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		ContextCorePlugin contextCorePlugin = ContextCorePlugin.getDefault();
		contributor = mock(AbstractContextContributor.class);
		when(contributor.getDataAsStream(context)).thenReturn(null);
		int initialContributor = contextCorePlugin.getContextContributor().size();

		contextCorePlugin.addContextContributor(contributor);
		assertEquals(initialContributor + 1, contextCorePlugin.getContextContributor().size());
		assertEquals(contributor, contextCorePlugin.getContextContributor().get(initialContributor));

		externalizer.writeContext(context, mock(ZipOutputStream.class));
		verify(contributor).getDataAsStream(context);

		contextCorePlugin.removeContextContributor(contributor);
		assertEquals(initialContributor, contextCorePlugin.getContextContributor().size());
	}

	public void testWriteAdditionalContextData() throws Exception {
		InteractionContextExternalizer externalizer = new InteractionContextExternalizer();
		contributor = mock(AbstractContextContributor.class);
		InteractionEvent event = mockNavigation("InteractionEvent");
		context.parseEvent(event);

		String testContributorId = "myContributor";
		String testData = "important context information";
		InputStream testStream = new ByteArrayInputStream(testData.getBytes());
		when(contributor.getIdentifier()).thenReturn(testContributorId);
		when(contributor.getDataAsStream(context)).thenReturn(testStream);
		ContextCorePlugin.getDefault().addContextContributor(contributor);

		contextFile = ContextCorePlugin.getContextStore().getFileForContext(context.getHandleIdentifier());

		externalizer.writeContextToXml(context, contextFile);
		InputStream resultStream = externalizer.getAdditionalInformation(contextFile, testContributorId);
		assertNotNull(resultStream);
		assertNull(externalizer.getAdditionalInformation(contextFile, "nonExistingContributor"));
		try (Scanner scanner = new Scanner(resultStream)) {
			assertEquals(testData, scanner.useDelimiter("\\A").next());
		}

		resultStream = ContextCore.getContextManager().getAdditionalContextData(context, testContributorId);
		assertNotNull(resultStream);
		assertNull(externalizer.getAdditionalInformation(contextFile, "nonExistingContributor"));
	}
}
