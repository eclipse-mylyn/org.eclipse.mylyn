/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.junit.jupiter.api.Test;

/**
 * @author Mik Kersten
 */
public class ScalingFactorsTest {

	@Test
	public void testLandmarkDefaults() {
		IInteractionContextScaling scalingFactors = new InteractionContextScaling();
		assertEquals(7 * scalingFactors.getLandmark(), scalingFactors.getForcedLandmark());
	}

}
