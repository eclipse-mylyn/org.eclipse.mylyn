/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

/**
 * @author Mik Kersten
 */
public class ScalingFactorsTest extends TestCase {

	public void testLandmarkDefaults() {
		IInteractionContextScaling scalingFactors = new InteractionContextScaling();
		assertEquals(7 * scalingFactors.getLandmark(), scalingFactors.getForcedLandmark());
	}

}
