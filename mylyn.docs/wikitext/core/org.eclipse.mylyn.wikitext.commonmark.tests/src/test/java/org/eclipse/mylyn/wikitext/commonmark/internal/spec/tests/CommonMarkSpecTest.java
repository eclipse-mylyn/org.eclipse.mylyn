/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.spec.tests;

import static org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.commonmark.CommonMarkLanguage;
import org.eclipse.mylyn.wikitext.internal.util.WikiStringStyle;
import org.eclipse.mylyn.wikitext.util.LocationTrackingReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@SuppressWarnings({ "nls", "restriction" })
@RunWith(Parameterized.class)
public class CommonMarkSpecTest {

	private static final String SPEC_VERSION = "0.21";

	private static final URI COMMONMARK_SPEC_URI = URI
			.create(String.format("https://raw.githubusercontent.com/jgm/CommonMark/%s/spec.txt", SPEC_VERSION));

	private static final Set<String> HEADING_EXCLUSIONS = Set.of();

	private static final Set<Integer> LINE_EXCLUSIONS = Set.of(//
			281, // Tabs
			2478, // Link reference definitions
			2515, // Link reference definitions
			3380, // List items
			3404, // List items
			3766, // List items
			3789, // List items
			3801, // List items
			3817, // List items
			4401, // Lists
			4425, // Lists
			4664, // Lists
			4681 // Lists
			);

	public static class Expectation {

		final String input;

		final String expected;

		public Expectation(String input, String expected) {
			this.input = input;
			this.expected = expected;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, WikiStringStyle.WIKI_STRING_STYLE) //
					.append("input", input)
					.append("expected", expected)
					.toString();
		}
	}

	private final Expectation expectation;

	private final String heading;

	private final int lineNumber;

	@Before
	public void preconditions() {
		assumeTrue(!HEADING_EXCLUSIONS.contains(heading));
		assumeTrue(!LINE_EXCLUSIONS.contains(Integer.valueOf(lineNumber)));
	}

	@Test
	public void test() {
		try {
			CommonMarkLanguage language = createCommonMarkLanguage();
			assertContent(language, expectation.expected, expectation.input);
		} catch (AssertionError e) {
			System.out.println(lineNumber + ", // " + heading);
			System.out.flush();
			throw e;
		}
	}

	private CommonMarkLanguage createCommonMarkLanguage() {
		CommonMarkLanguage language = new CommonMarkLanguage();
		language.setStrictlyConforming(true);
		return language;
	}

	@Parameters //(name = "{0} test {index}")
	public static List<Object[]> parameters() {
		List<Object[]> parameters = new ArrayList<>();

		loadSpec(parameters);

		return List.copyOf(parameters);
	}

	public CommonMarkSpecTest(String title, String heading, int lineNumber, Expectation expectation) {
		this.heading = heading;
		this.lineNumber = lineNumber;
		this.expectation = expectation;
	}

	private static void loadSpec(List<Object[]> parameters) {
		Pattern headingPattern = Pattern.compile("#+\\s*(.+)");
		try {
			String spec = loadCommonMarkSpec();
			LocationTrackingReader reader = new LocationTrackingReader(new StringReader(spec));
			String heading = "unspecified";
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replace('→', '\t');
				if (line.trim().equals(".")) {
					int testLineNumber = reader.getLineNumber();
					Expectation expectation = readExpectation(reader);
					parameters.add(
							new Object[] { heading + ":line " + testLineNumber, heading, testLineNumber, expectation });
				}
				Matcher headingMatcher = headingPattern.matcher(line);
				if (headingMatcher.matches()) {
					heading = headingMatcher.group(1);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Expectation readExpectation(LocationTrackingReader reader) throws IOException {
		String input = readUntilDelimiter(reader);
		String expected = readUntilDelimiter(reader);
		return new Expectation(input, expected);
	}

	private static String readUntilDelimiter(LocationTrackingReader reader) throws IOException {
		List<String> lines = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.replace('→', '\t');
			if (line.trim().equals(".")) {
				break;
			}
			lines.add(line);
		}
		return lines.stream().collect(Collectors.joining("\n"));
	}

	private static String loadCommonMarkSpec() throws IOException {
		File tmpFolder = new File("./tmp");
		if (!tmpFolder.exists()) {
			tmpFolder.mkdir();
		}
		assertTrue(tmpFolder.getAbsolutePath(), tmpFolder.exists());
		File spec = new File(tmpFolder, String.format("spec%s.txt", SPEC_VERSION));
		if (!spec.exists()) {
			try (FileOutputStream out = new FileOutputStream(spec)) {
				IOUtils.copy(COMMONMARK_SPEC_URI.toURL(), out);
			}
		}
		try (InputStream in = new FileInputStream(spec)) {
			return IOUtils.toString(in, StandardCharsets.UTF_8);
		}
	}

}
