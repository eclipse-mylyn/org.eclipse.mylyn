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
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class ScalingFactorsTest extends TestCase {

	public void testLandmarkDefaults() {
		IInteractionContextScaling scalingFactors = new InteractionContextScaling();
		assertEquals(7 * scalingFactors.getLandmark(), scalingFactors.getForcedLandmark());
	}

}
