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
package org.eclipse.mylyn.wikitext.core.util.anttask;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.internal.wikitext.core.validation.StandaloneMarkupValidator;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

/**
 * An abstract class for Ant tasks that use a configurable {@link MarkupLanguage}.
 * 
 * @author David Green
 */
public abstract class MarkupTask extends Task {

	private String markupLanguage;

	private String internalLinkPattern;

	private boolean validate = true;

	private boolean failOnValidationError = true;

	private boolean failOnValidationWarning = false;

	/**
	 * The markup language to use. Should correspond to a {@link MarkupLanguage#getName() markup language name}.
	 */
	public String getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * The markup language to use. Should correspond to a {@link MarkupLanguage#getName() markup language name}.
	 */
	public void setMarkupLanguage(String markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	/**
	 * Create a {@link MarkupLanguage markup language parser} for the {@link #getMarkupLanguage() specified markup
	 * language}.
	 * 
	 * @return the markup language
	 * 
	 * @throws BuildException
	 *             if the markup language is not specified or if it is unknown.
	 */
	protected MarkupLanguage createMarkupLanguage() throws BuildException {
		if (markupLanguage == null) {
			throw new BuildException("Must specify @markupLanguage");
		}
		try {
			MarkupLanguage language = ServiceLocator.getInstance(getClass().getClassLoader()).getMarkupLanguage(
					markupLanguage);
			if (internalLinkPattern != null) {
				language.setInternalLinkPattern(internalLinkPattern);
			}
			return language;
		} catch (IllegalArgumentException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}

	/**
	 * @see MarkupLanguage#setInternalLinkPattern(String)
	 */
	public void setInternalLinkPattern(String internalLinkPattern) {
		this.internalLinkPattern = internalLinkPattern;
	}

	/**
	 * @see MarkupLanguage#getInternalLinkPattern()
	 */
	public String getInternalLinkPattern() {
		return internalLinkPattern;
	}

	/**
	 * Indicate if WikiText markup should be validated. The default is true.
	 */
	public boolean isValidate() {
		return validate;
	}

	/**
	 * Indicate if WikiText markup should be validated. The default is true.
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * Indicate if WikiText validation errors should cause the Ant build to fail. The default is true.
	 */
	public boolean isFailOnValidationError() {
		return failOnValidationError;
	}

	/**
	 * Indicate if WikiText validation errors should cause the Ant build to fail. The default is true.
	 */
	public void setFailOnValidationError(boolean failOnValidationError) {
		this.failOnValidationError = failOnValidationError;
	}

	/**
	 * Indicate if WikiText validation warnings should cause the Ant build to fail. The default is false.
	 */
	public boolean isFailOnValidationWarning() {
		return failOnValidationWarning;
	}

	/**
	 * Indicate if WikiText validation warnings should cause the Ant build to fail. The default is false.
	 */
	public void setFailOnValidationWarning(boolean failOnValidationWarning) {
		this.failOnValidationWarning = failOnValidationWarning;
	}

	protected void performValidation(File source, String markupContent) {
		if (!validate) {
			return;
		}
		if (markupLanguage == null) {
			throw new IllegalStateException();
		}
		log(String.format("Validating %s", source), Project.MSG_VERBOSE);

		StandaloneMarkupValidator markupValidator = StandaloneMarkupValidator.getValidator(markupLanguage);

		List<ValidationProblem> problems = markupValidator.validate(markupContent);

		int errorCount = 0;
		int warningCount = 0;
		for (ValidationProblem problem : problems) {
			int messageLevel = Project.MSG_ERR;
			if (problem.getSeverity() == Severity.ERROR) {
				++errorCount;
			} else if (problem.getSeverity() == Severity.WARNING) {
				++warningCount;
				messageLevel = Project.MSG_WARN;
			}
			log(String.format("%s:%s %s", source.getName(), problem.getOffset(), problem.getMessage()), messageLevel);
		}

		if ((errorCount > 0 && failOnValidationError) || (warningCount > 0 && failOnValidationWarning)) {
			throw new BuildException(String.format("Validation: %s errors and %s warnings on file %s", errorCount,
					warningCount, source));
		}
	}

}
