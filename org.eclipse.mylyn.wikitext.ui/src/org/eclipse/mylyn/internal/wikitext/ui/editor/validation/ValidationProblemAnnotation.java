/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.validation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;

/**
 * 
 * 
 * @author David Green
 */
public class ValidationProblemAnnotation extends Annotation {

	private static final String TYPE_BASE = "org.eclipse.mylyn.wikitext.ui.editor.problem.validation";

	public static final String TYPE_WARNING = TYPE_BASE + ".warning";

	public static final String TYPE_ERROR = TYPE_BASE + ".error";

	public ValidationProblemAnnotation(ValidationProblem problem) {
		super(computeType(problem), false, problem.getMessage());
	}

	private static String computeType(ValidationProblem problem) {
		switch (problem.getSeverity()) {
		case ERROR:
			return TYPE_ERROR;
		case WARNING:
			return TYPE_WARNING;
		default:
			throw new IllegalStateException(problem.getSeverity().name());
		}
	}

	public static boolean isValidationAnnotation(Annotation annotation) {
		return annotation.getType().startsWith(TYPE_BASE);
	}
}
