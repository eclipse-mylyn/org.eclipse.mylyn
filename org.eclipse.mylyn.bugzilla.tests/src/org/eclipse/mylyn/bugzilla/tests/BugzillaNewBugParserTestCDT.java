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

package org.eclipse.mylar.bugzilla.tests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.core.tests.support.FileTool;
import org.eclipse.mylar.internal.bugzilla.core.NewBugModel;
import org.eclipse.mylar.internal.bugzilla.core.internal.NewBugParser;

/**
 * Tests NewBugParser -- parses product attributes for new bug reports
 */
public class BugzillaNewBugParserTestCDT extends TestCase {

	public BugzillaNewBugParserTestCDT() {
		super();
	}

	public BugzillaNewBugParserTestCDT(String arg0) {
		super(arg0);
	}

	public void testProductCDT() throws Exception {

		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/cdt-page.html"));

		Reader in = new FileReader(f);

		NewBugModel nbm = new NewBugModel();
		new NewBugParser(in).parseBugAttributes(nbm, true); // ** TRUE vs FALSE
															// **

		// attributes for this bug model
		List<Attribute> attributes = nbm.getAttributes();
		// printList(attributes);

		Iterator<Attribute> itr = attributes.iterator();
		Attribute att = itr.next();

		// Attribute: Severity
		assertEquals("Attribute: Severity", "Severity", att.getName());

		Map<String, String> attOptions = att.getOptionValues(); // HashMap of
		// options for the
		// current
		// attribute
		Object[] options = attOptions.keySet().toArray(); // Array of keys for
		// the options of the
		// current attribute
		assertEquals("# Severity options", 7, options.length);

		int i = 0;
		while (i < options.length) {
			assertEquals("severity options", "blocker", options[i++]);
			assertEquals("severity options", "critical", options[i++]);
			assertEquals("severity options", "major", options[i++]);
			assertEquals("severity options", "normal", options[i++]);
			assertEquals("severity options", "minor", options[i++]);
			assertEquals("severity options", "trivial", options[i++]);
			assertEquals("severity options", "enhancement", options[i++]);
		}

		// Attribute: product
		att = itr.next();
		assertEquals("Attribute: product", "product", att.getName());

		attOptions = att.getOptionValues();
		options = attOptions.keySet().toArray();
		assertEquals("No product options", 0, options.length);

		// Attribute: AssignedTo
		att = itr.next();
		assertEquals("Attribute: AssignedTo", "AssignedTo", att.getName());

		attOptions = att.getOptionValues();
		options = attOptions.keySet().toArray();
		assertEquals("No AssignedTo options", 0, options.length);

		// Attribute: OS
		att = itr.next();
		assertEquals("Attribute: OS", "OS", att.getName());

		attOptions = att.getOptionValues();
		options = attOptions.keySet().toArray();
		assertEquals("# of options", 20, options.length);

		i = 0;
		while (i < options.length) {
			assertEquals("OS options", "All", options[i++]);
			assertEquals("OS options", "AIX Motif", options[i++]);
			assertEquals("OS options", "Windows 95", options[i++]);
			assertEquals("OS options", "Windows 98", options[i++]);
			assertEquals("OS options", "Windows CE", options[i++]);
			assertEquals("OS options", "Windows ME", options[i++]);
			assertEquals("OS options", "Windows 2000", options[i++]);
			assertEquals("OS options", "Windows NT", options[i++]);
			assertEquals("OS options", "Windows XP", options[i++]);
			assertEquals("OS options", "Windows All", options[i++]);
			assertEquals("OS options", "MacOS X", options[i++]);
			assertEquals("OS options", "Linux", options[i++]);
			assertEquals("OS options", "Linux-GTK", options[i++]);
			assertEquals("OS options", "Linux-Motif", options[i++]);
			assertEquals("OS options", "HP-UX", options[i++]);
			assertEquals("OS options", "Neutrino", options[i++]);
			assertEquals("OS options", "QNX-Photon", options[i++]);
			assertEquals("OS options", "Solaris", options[i++]);
			assertEquals("OS options", "Unix All", options[i++]);
			assertEquals("OS options", "other", options[i++]);
		}

		// Attribute: Version
		att = itr.next();
		assertEquals("Attribute: Version", "Version", att.getName());

		// attOptions = (HashMap) att.getOptionValues();
		options = att.getOptionValues().keySet().toArray();
		assertEquals("# Version options", 5, options.length);

		i = 0;
		while (i < options.length) {
			assertEquals("Version options", "1.0", options[i++]);
			assertEquals("Version options", "1.0.1", options[i++]);
			assertEquals("Version options", "1.1", options[i++]);
			assertEquals("Version options", "1.2", options[i++]);
			assertEquals("Version options", "2.0", options[i++]);
		}

		// Attribute: Platform
		att = itr.next();
		assertEquals("Attribute: Platform", "Platform", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("# Platform options", 6, options.length);

		i = 0;
		while (i < options.length) {
			assertEquals("Platform options", "All", options[i++]);
			assertEquals("Platform options", "Macintosh", options[i++]);
			assertEquals("Platform options", "PC", options[i++]);
			assertEquals("Platform options", "Power PC", options[i++]);
			assertEquals("Platform options", "Sun", options[i++]);
			assertEquals("Platform options", "Other", options[i++]);
		}

		// Attribute: Component
		att = itr.next();
		assertEquals("Attribute: Component", "Component", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("# Component options", 9, options.length);

		i = 0;
		while (i < options.length) {
			assertEquals("Component options", "CDT-parser", options[i++]);
			assertEquals("Component options", "Core", options[i++]);
			assertEquals("Component options", "Cpp-Extensions", options[i++]);
			assertEquals("Component options", "Debug", options[i++]);
			assertEquals("Component options", "Debug-MI", options[i++]);
			assertEquals("Component options", "Doc", options[i++]);
			assertEquals("Component options", "Generic-Extensions", options[i++]);
			assertEquals("Component options", "Launcher", options[i++]);
			assertEquals("Component options", "UI", options[i++]);
		}

		// Attribute: bug_status
		att = itr.next();
		assertEquals("Attribute: bug_status", "bug_status", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("# bug_status options [none]", 0, options.length);

		// Attribute: form_name
		att = itr.next();
		assertEquals("Attribute: form_name", "form_name", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("No form_name options", 0, options.length);

		// Attribute: bug_file_loc
		att = itr.next();
		assertEquals("Attribute: bug_file_loc", "bug_file_loc", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("No bug_file_loc options", 0, options.length);

		// Attribute: priority
		att = itr.next();
		assertEquals("Attribute: priority", "priority", att.getName());

		options = att.getOptionValues().keySet().toArray();
		assertEquals("No priority options", 0, options.length);

	}
}
