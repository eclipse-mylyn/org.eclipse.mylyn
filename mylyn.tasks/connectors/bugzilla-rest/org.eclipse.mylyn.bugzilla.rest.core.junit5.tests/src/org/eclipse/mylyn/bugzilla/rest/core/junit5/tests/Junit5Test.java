package org.eclipse.mylyn.bugzilla.rest.core.junit5.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("restriction")
@EnabledIfSystemProperty(named = "CI_SERVER_TESTS", matches = "true")
class Junit5Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Junit5Test @BeforeAll : setUpBeforeClass()");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("Junit5Test @AfterAll  : tearDownAfterClass()");
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("Junit5Test @BeforeEach: setUp()");
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("Junit5Test @AfterEach : tearDown()");
	}

	@Test
	@ExtendWith(DefaultFixtureParameterResolver.class)
	@DisplayName("TEST 1")
	@Tag("my-tag")
	void testMyFunc(BugzillaRestTestFixture actFixture, TestInfo testInfo) {
		assertEquals("TEST 1", testInfo.getDisplayName());
//        assertEquals("testMyFunc(TestInfo)", testInfo.getDisplayName());
		assertTrue(testInfo.getTags().contains("my-tag"));
		assertNotNull(actFixture);
		System.out.println("testMyFunc: " + actFixture.getRepositoryUrl());
	}

	@ParameterizedTest
	@MethodSource("fixtureProvider")
	@DisplayName("Connection Test")
	@Tag("CI_SERVER_TEST")
	void myTes(BugzillaRestTestFixture actFixture, TestInfo testInfo) {
		assertTrue(testInfo.getTags().contains("CI_SERVER_TEST"));
		assertTrue(testInfo.getDisplayName().endsWith(actFixture.getRepositoryUrl()));
		System.out.println("myTes: " + actFixture.getRepositoryUrl() + "  "+ actFixture.getSimpleInfo() + "  " + actFixture.getDescription());
	}

	static Stream<BugzillaRestTestFixture> fixtureProvider() {
		TestConfiguration defFixture = TestConfiguration.getDefault();
		List<BugzillaRestTestFixture> parametersList = (List<BugzillaRestTestFixture>) defFixture
				.discover(BugzillaRestTestFixture.class, "bugzillaREST");
		return parametersList.stream();
	}
}
