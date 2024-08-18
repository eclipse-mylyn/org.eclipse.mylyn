package org.eclipse.mylyn.bugzilla.rest.core.junit5.tests;

import java.util.List;

import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DefaultFixtureParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
            throws ParameterResolutionException {
        return (parameterContext.getParameter().getType().equals(BugzillaRestTestFixture.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
		TestConfiguration defFixture = TestConfiguration.getDefault();
		defFixture.setDefaultOnly(true);
		List<BugzillaRestTestFixture> parametersList = (List<BugzillaRestTestFixture>) defFixture.discover(BugzillaRestTestFixture.class,
				"bugzillaREST");
	    return parametersList.get(0);
    }
}