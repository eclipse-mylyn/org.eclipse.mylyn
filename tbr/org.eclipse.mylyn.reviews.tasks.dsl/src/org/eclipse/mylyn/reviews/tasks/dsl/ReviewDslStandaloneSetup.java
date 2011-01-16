
package org.eclipse.mylyn.reviews.tasks.dsl;

import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ReviewDslStandaloneSetup extends ReviewDslStandaloneSetupGenerated{

	public static void doSetup() {
		new ReviewDslStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

