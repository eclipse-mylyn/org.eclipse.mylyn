/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.ui.editor.validation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;

/**
 * @author David Green
 */
public class ValidationProblemAnnotation extends Annotation {

	private static final String TYPE_BASE = "org.eclipse.mylyn.wikitext.ui.editor.problem.validation"; //$NON-NLS-1$

	public static final String TYPE_WARNING = TYPE_BASE + ".warning"; //$NON-NLS-1$

	public static final String TYPE_ERROR = TYPE_BASE + ".error"; //$NON-NLS-1$

	public ValidationProblemAnnotation(ValidationProblem problem) {
		super(computeType(problem), false, problem.getMessage());
	}

	private static String computeType(ValidationProblem problem) {
		return switch (problem.getSeverity()) {
			case ERROR -> TYPE_ERROR;
			case WARNING -> TYPE_WARNING;
			default -> throw new IllegalStateException(problem.getSeverity().name());
		};
	}

	public static boolean isValidationAnnotation(Annotation annotation) {
		return annotation.getType().startsWith(TYPE_BASE);
	}
}
