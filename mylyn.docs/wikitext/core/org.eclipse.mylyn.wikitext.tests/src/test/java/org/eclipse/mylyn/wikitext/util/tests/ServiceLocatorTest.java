/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.util.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
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

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.parser.markup.tests.MockMarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.parser.tests.MockMarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

	private ServiceLocator locator;

	@Before
	public void setUp() {
		ServiceLocator.setImplementation(null);
		locator = ServiceLocator.getInstance(ServiceLocatorTest.class.getClassLoader());
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void testKnownLanguageMetaInf() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		assertKnownMarkupLanguage();
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void testKnownLanguageServices() {
		setupServiceLocatorWithMockMarkupLanguage(false);
		assertKnownMarkupLanguage();
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void testKnownLanguageProviderMetaInf() {
		setupServiceLocatorWithMockMarkupLanguageProvider(true);
		assertKnownMarkupLanguage();
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
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

//			@Override
//			void logFailure(String className, Exception e) {
//			}
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
// FIXME: re-implement tests to avoid access to package-local members

//				@Override
//				protected List<String> readServiceClassNames(URL url) {
//					return super.readServiceClassNames(new ByteArrayInputStream(
//							MockMarkupLanguage.class.getName().getBytes(StandardCharsets.UTF_8)));
//				}
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
// FIXME: re-implement tests to avoid access to package-local members
//				@Override
//				protected List<String> readServiceClassNames(URL url) {
//					return super.readServiceClassNames(new ByteArrayInputStream(
//							MockMarkupLanguageProvider.class.getName().getBytes(StandardCharsets.UTF_8)));
//				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testUnknownLanguage() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		assertThrows(IllegalArgumentException.class,
				() -> locator.getMarkupLanguage("No Such Language asdlkfjal;sjdf"));
	}

	@Test
	public void testFQN() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(MockMarkupLanguage.class.getName());
		assertNotNull(markupLanguage);
		assertEquals(MockMarkupLanguage.class, markupLanguage.getClass());
	}

	@Test
	public void getMarkupLanguageNull() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> locator.getMarkupLanguage(null));
		assertTrue(e.getMessage().contains("Must provide a languageName"));
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void getMarkupLanguageUnknown() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> locator.getMarkupLanguage("UnknownLanguage"));
		assertTrue(e.getMessage().contains(
				"Cannot load markup language 'UnknownLanguage'. Known markup languages are 'MockMarkupLanguage'"));
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void readServicesFileNone() {
//		List<String> names = locator.readServiceClassNames(createInput(""));
//		assertNotNull(names);
//		assertTrue(names.isEmpty());
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void readServicesFileCommentsAndEmptyLinesOnly() {
//		List<String> names = locator.readServiceClassNames(createInput("#" + Object.class.getName() + "\n\n\n\n#last"));
//		assertNotNull(names);
//		assertTrue(names.isEmpty());
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void readServicesFile() {
//		List<String> names = locator.readServiceClassNames(
//				createInput(Object.class.getName() + "\n" + String.class.getName() + " # trailing comment"));
//		assertNotNull(names);
//		assertEquals(2, names.size());
//		assertEquals(Object.class.getName(), names.get(0));
//		assertEquals(String.class.getName(), names.get(1));
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void getClasspathServiceResourceNames() {
//		List<String> names = locator.getClasspathServiceResourceNames();
//		assertNotNull(names);
//		assertTrue(names.contains("META-INF/services/" + MarkupLanguage.class.getName()));
//		assertTrue(names.contains("services/" + MarkupLanguage.class.getName()));
	}

	// FIXME: re-implement tests to avoid access to package-local members
	@Ignore
	@Test
	public void getAllMarkupLanguagesFiltersDuplicates() {
//		final MarkupLanguage language1 = new TestMarkupLanguage("Language 1");
//		final MarkupLanguage language1b = new MarkupLanguage() {
//			{
//				setName("Language 1");
//			}
//
//			@Override
//			public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
//				throw new IllegalStateException();
//			}
//		};
//		final MarkupLanguage language2 = new TestMarkupLanguage("Language 3");
//		final MarkupLanguage language2b = new TestMarkupLanguage("Language 3");
//
//		ServiceLocator locator = new ServiceLocator(ServiceLocatorTest.class.getClassLoader()) {
//			@Override
//			void loadMarkupLanguages(MarkupLanguageVisitor visitor) {
//				visitor.accept(language1);
//				visitor.accept(language1b);
//				visitor.accept(language2);
//				visitor.accept(language2b);
//			}
//		};
//		Set<MarkupLanguage> markupLanguages = locator.getAllMarkupLanguages();
//		assertEquals(3, markupLanguages.size());
//		assertTrue(markupLanguages.containsAll(Set.of(language1, language1b)));
//		assertTrue(markupLanguages.contains(language2) || markupLanguages.contains(language2b));
	}

	private InputStream createInput(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}
