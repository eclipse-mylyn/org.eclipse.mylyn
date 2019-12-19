/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.mylyn.internal.wikitext.MockMarkupLanguage;
import org.eclipse.mylyn.internal.wikitext.MockMarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link ServiceLocator}
 *
 * @author David Green
 */
public class ServiceLocatorTest {

	static class TestMarkupLanguage extends MarkupLanguage {
		public TestMarkupLanguage(String name) {
			setName(name);
		}

		@Override
		public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
			throw new IllegalStateException();
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ServiceLocator locator;

	@Before
	public void setUp() {
		ServiceLocator.setImplementation(null);
		locator = ServiceLocator.getInstance(ServiceLocatorTest.class.getClassLoader());
	}

	@Test
	public void testKnownLanguageMetaInf() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageServices() {
		setupServiceLocatorWithMockMarkupLanguage(false);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageProviderMetaInf() {
		setupServiceLocatorWithMockMarkupLanguageProvider(true);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageProviderServices() {
		setupServiceLocatorWithMockMarkupLanguageProvider(false);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testLoadClassNotFound() throws MalformedURLException {
		final URL url = new URL("file://example");
		final AtomicBoolean wasThrown = new AtomicBoolean();
		locator = new ServiceLocator(ServiceLocatorTest.class.getClassLoader()) {
			@Override
			protected Class<?> loadClass(ResourceDescriptor resource, String className) throws ClassNotFoundException {
				wasThrown.set(true);
				throw new ClassNotFoundException(className);
			}

			@Override
			protected List<ResourceDescriptor> discoverServiceResources() {
				return Collections.singletonList(new ResourceDescriptor(url));
			}

			@Override
			protected List<String> readServiceClassNames(URL url) {
				return Collections.singletonList("test.TestLanguage");
			}

			@Override
			void logFailure(String className, Exception e) {
			}
		};
		Set<MarkupLanguage> languages = locator.getAllMarkupLanguages();
		assertTrue(wasThrown.get());
		assertNotNull(languages);
		assertTrue(languages.isEmpty());
	}

	protected void assertKnownMarkupLanguage() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(MockMarkupLanguage.class.getSimpleName());
		assertNotNull(markupLanguage);
		assertEquals(new MockMarkupLanguage().getName(), markupLanguage.getName());
	}

	protected void setupServiceLocatorWithMockMarkupLanguage(boolean metaInf) {
		try {
			ClassLoader classLoader = new URLClassLoader(new URL[0]) {
				@Override
				public Enumeration<URL> getResources(String name) throws IOException {
					if ((metaInf && name.equals("META-INF/services/" + MarkupLanguage.class.getName()))
							|| (!metaInf && name.equals("services/" + MarkupLanguage.class.getName()))) {
						Collection<URL> resources = Collections
								.singletonList(new URL("file:" + MockMarkupLanguage.class.getName()));
						return Collections.enumeration(resources);
					}
					return Collections.enumeration(Collections.emptyList());
				}

				@Override
				public Class<?> loadClass(String name) throws ClassNotFoundException {
					if (name.equals(MockMarkupLanguage.class.getName())) {
						return MockMarkupLanguage.class;
					}
					return super.loadClass(name);
				}
			};
			locator = new ServiceLocator(classLoader) {
				@Override
				protected List<String> readServiceClassNames(URL url) {
					return super.readServiceClassNames(new ByteArrayInputStream(
							MockMarkupLanguage.class.getName().getBytes(StandardCharsets.UTF_8)));
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void setupServiceLocatorWithMockMarkupLanguageProvider(boolean metaInf) {
		try {
			ClassLoader classLoader = new URLClassLoader(new URL[0]) {
				@Override
				public Enumeration<URL> getResources(String name) throws IOException {
					if ((metaInf && name.equals("META-INF/services/" + MarkupLanguageProvider.class.getName()))
							|| (!metaInf && name.equals("services/" + MarkupLanguageProvider.class.getName()))) {
						Collection<URL> resources = Collections
								.singletonList(new URL("file:" + MockMarkupLanguageProvider.class.getName()));
						return Collections.enumeration(resources);
					}
					return Collections.enumeration(Collections.emptyList());
				}

				@Override
				public Class<?> loadClass(String name) throws ClassNotFoundException {
					if (name.equals(MockMarkupLanguageProvider.class.getName())) {
						return MockMarkupLanguageProvider.class;
					}
					return super.loadClass(name);
				}
			};

			locator = new ServiceLocator(classLoader) {
				@Override
				protected List<String> readServiceClassNames(URL url) {
					return super.readServiceClassNames(new ByteArrayInputStream(
							MockMarkupLanguageProvider.class.getName().getBytes(StandardCharsets.UTF_8)));
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testUnknownLanguage() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		thrown.expect(IllegalArgumentException.class);
		locator.getMarkupLanguage("No Such Language asdlkfjal;sjdf");
	}

	@Test
	public void testFQN() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(MockMarkupLanguage.class.getName());
		assertNotNull(markupLanguage);
		assertEquals(MockMarkupLanguage.class, markupLanguage.getClass());
	}

	@Test
	public void getMarkupLanguageNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Must provide a languageName");
		locator.getMarkupLanguage(null);
	}

	@Test
	public void getMarkupLanguageUnknown() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(
				"Cannot load markup language 'UnknownLanguage'. Known markup languages are 'MockMarkupLanguage'");
		locator.getMarkupLanguage("UnknownLanguage");
	}

	@Test
	public void readServicesFileNone() {
		List<String> names = locator.readServiceClassNames(createInput(""));
		assertNotNull(names);
		assertTrue(names.isEmpty());
	}

	@Test
	public void readServicesFileCommentsAndEmptyLinesOnly() {
		List<String> names = locator.readServiceClassNames(createInput("#" + Object.class.getName() + "\n\n\n\n#last"));
		assertNotNull(names);
		assertTrue(names.isEmpty());
	}

	@Test
	public void readServicesFile() {
		List<String> names = locator.readServiceClassNames(
				createInput(Object.class.getName() + "\n" + String.class.getName() + " # trailing comment"));
		assertNotNull(names);
		assertEquals(2, names.size());
		assertEquals(Object.class.getName(), names.get(0));
		assertEquals(String.class.getName(), names.get(1));
	}

	@Test
	public void getClasspathServiceResourceNames() {
		List<String> names = locator.getClasspathServiceResourceNames();
		assertNotNull(names);
		assertTrue(names.contains("META-INF/services/" + MarkupLanguage.class.getName()));
		assertTrue(names.contains("services/" + MarkupLanguage.class.getName()));
	}

	@Test
	public void getAllMarkupLanguagesFiltersDuplicates() {
		final MarkupLanguage language1 = new TestMarkupLanguage("Language 1");
		final MarkupLanguage language1b = new MarkupLanguage() {
			{
				setName("Language 1");
			}

			@Override
			public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
				throw new IllegalStateException();
			}
		};
		final MarkupLanguage language2 = new TestMarkupLanguage("Language 3");
		final MarkupLanguage language2b = new TestMarkupLanguage("Language 3");

		ServiceLocator locator = new ServiceLocator(ServiceLocatorTest.class.getClassLoader()) {
			@Override
			void loadMarkupLanguages(MarkupLanguageVisitor visitor) {
				visitor.accept(language1);
				visitor.accept(language1b);
				visitor.accept(language2);
				visitor.accept(language2b);
			}
		};
		Set<MarkupLanguage> markupLanguages = locator.getAllMarkupLanguages();
		assertEquals(3, markupLanguages.size());
		assertTrue(markupLanguages.containsAll(ImmutableSet.of(language1, language1b)));
		assertTrue(markupLanguages.contains(language2) || markupLanguages.contains(language2b));
	}

	private InputStream createInput(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}
