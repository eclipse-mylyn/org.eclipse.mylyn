/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class ScalingFactorsTest extends TestCase {

	public void testLandmarkDefaults() {
		InteractionContextScaling scalingFactors = new InteractionContextScaling();
		assertEquals(10 * scalingFactors.getLandmark(), scalingFactors.getForcedLandmark());
	}
	
}
