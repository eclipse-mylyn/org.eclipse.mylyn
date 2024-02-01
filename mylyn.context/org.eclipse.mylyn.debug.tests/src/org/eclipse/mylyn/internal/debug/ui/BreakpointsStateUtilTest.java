/*******************************************************************************
 * Copyright (c) 2012, 2015 Sebastian Schmidt and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsStateUtilTest {

	private final IPath pluginStateDir = Platform.getStateLocation(DebugUiPlugin.getDefault().getBundle());

	private final File pluginStateFile = pluginStateDir.append(BreakpointsStateUtil.STATE_FILE).toFile();

	private final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();

	private final BreakpointsStateUtil objectUnderTest = new BreakpointsStateUtil(pluginStateDir);

	private IBreakpoint breakpoint;

	@Before
	public void setUp() throws Exception {
		BreakpointsTestUtil.setManageBreakpointsPreference(true);
		BreakpointsTestUtil.createProject();
		deleteAllBreakpoints();
		breakpoint = BreakpointsTestUtil.createTestBreakpoint();
	}

	@After
	public void tearDown() throws IOException, CoreException {
		deleteAllBreakpoints();
		FileUtils.deleteDirectory(pluginStateDir.toFile());
		WorkspaceSetupHelper.clearWorkspace();
	}

	@Test
	public void testSaveState() throws Exception {
		breakpointManager.addBreakpoint(breakpoint);
		assertEquals(1, breakpointManager.getBreakpoints().length);

		objectUnderTest.saveState();

		assertTrue(pluginStateFile.exists());

		Document pluginStateDocument = getDocument(pluginStateFile);
		Document testDocument = getDocument(new File("testdata/breakpointFile.xml"));
		sortNodes(pluginStateDocument);
		sortNodes(testDocument);
		assertTrue("Documents not equal:\n" + documentToString(pluginStateDocument) + "\n===\n"
				+ documentToString(testDocument), pluginStateDocument.isEqualNode(testDocument));
	}

	private void sortNodes(Node node) {
		NodeList children = node.getChildNodes();
		List<Node> childNodes = new ArrayList<>();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			childNodes.add(child);
			sortNodes(child);
		}
		Collections.sort(childNodes, (a, b) -> {
			if (a.getAttributes() == null) {
				if (b.getAttributes() == null) {
					return 0;
				}
				return 1;
			} else if (b.getAttributes() == null) {
				return -1;
			}
			Node nameA = a.getAttributes().getNamedItem("name");
			Node nameB = b.getAttributes().getNamedItem("name");
			if (nameA == null) {
				if (nameB == null) {
					return 0;
				}
				return 1;
			} else if (nameB == null) {
				return -1;
			}
			return nameA.getNodeValue().compareTo(nameB.getNodeValue());
		});
		for (Node child : childNodes) {
			node.removeChild(child);
		}
		for (Node child : childNodes) {
			node.appendChild(child);
		}
	}

	private String documentToString(Document docuemnt) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(docuemnt), new StreamResult(writer));
		return writer.toString();
	}

	@Test
	public void testSaveStateWithoutBreakpoint() throws CoreException {
		deleteAllBreakpoints();

		objectUnderTest.saveState();

		assertFalse(pluginStateFile.exists()); // nothing to save;
	}

	@Test
	public void testRestoreState() throws CoreException, IOException {
		FileUtils.copyFile(new File("testdata/breakpointFile.xml"), pluginStateFile);

		objectUnderTest.restoreState();

		assertEquals(1, breakpointManager.getBreakpoints().length);
	}

	private void deleteAllBreakpoints() throws CoreException {
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		breakpointManager.removeBreakpoints(breakpoints, true);
		assertEquals(0, breakpointManager.getBreakpoints().length);
	}

	private Document getDocument(File inputFile) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		try (fileInputStream) {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(fileInputStream);
		}
	}

}
