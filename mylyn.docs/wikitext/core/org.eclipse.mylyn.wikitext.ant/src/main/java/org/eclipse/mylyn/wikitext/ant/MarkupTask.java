/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.wikitext.ant.internal.Messages;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.validation.StandaloneMarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;

/**
 * An abstract class for Ant tasks that use a configurable {@link MarkupLanguage}.
 *
 * @author David Green
 * @since 3.0
 */
public abstract class MarkupTask extends Task {

	private String markupLanguage;

	private String internalLinkPattern;

	private boolean validate = true;

	private boolean failOnValidationError = true;

	private boolean failOnValidationWarning = false;

	private MarkupLanguageConfiguration markupLanguageConfiguration;

	private String sourceEncoding;

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
	 *
	 */
	public MarkupLanguageConfiguration getMarkupLanguageConfiguration() {
		return markupLanguageConfiguration;
	}

	/**
	 *
	 */
	public void setMarkupLanguageConfiguration(MarkupLanguageConfiguration markupLanguageConfiguration) {
		this.markupLanguageConfiguration = markupLanguageConfiguration;
	}

	/**
	 * Create a {@link MarkupLanguage markup language parser} for the {@link #getMarkupLanguage() specified markup
	 * language}.
	 *
	 * @return the markup language
	 * @throws BuildException
	 *             if the markup language is not specified or if it is unknown.
	 */
	protected MarkupLanguage createMarkupLanguage() throws BuildException {
		if (markupLanguage == null) {
			throw new BuildException(Messages.getString("MarkupTask.0")); //$NON-NLS-1$
		}
		try {
			MarkupLanguage language = ServiceLocator.getInstance(getClass().getClassLoader())
					.getMarkupLanguage(markupLanguage);
			if (internalLinkPattern != null) {
				checkAbstractMarkupLanguage(language, "internalLinkPattern"); //$NON-NLS-1$
				((AbstractMarkupLanguage) language).setInternalLinkPattern(internalLinkPattern);
			}
			if (markupLanguageConfiguration != null) {
				language.configure(markupLanguageConfiguration);
			}
			return language;
		} catch (IllegalArgumentException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}

	private void checkAbstractMarkupLanguage(MarkupLanguage language, String field) {
		if (!(language instanceof AbstractMarkupLanguage)) {
			throw new BuildException(MessageFormat.format(Messages.getString("MarkupTask.2"), field, //$NON-NLS-1$
					language.getName()));
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

	/**
	 * The source encoding.
	 *
	 * @return the source encoding, or null if the default encoding is to be used.
	 * @see java.nio.charset.Charset
	 */
	public String getSourceEncoding() {
		return sourceEncoding;
	}

	/**
	 * The source encoding. The is the character encoding to be used when reading source files.
	 *
	 * @param sourceEncoding
	 *            the source encoding, or null if the default encoding is to be used.
	 * @see java.nio.charset.Charset
	 */
	public void setSourceEncoding(String sourceEncoding) {
		this.sourceEncoding = sourceEncoding;
	}

	protected void performValidation(File source, String markupContent) {
		if (!validate) {
			return;
		}
		if (markupLanguage == null) {
			throw new IllegalStateException();
		}
		log(MessageFormat.format(Messages.getString("MarkupTask.1"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

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
			log(String.format("%s:%s %s", source.getName(), problem.getOffset(), problem.getMessage()), messageLevel); //$NON-NLS-1$
		}

		if ((errorCount > 0 && failOnValidationError) || (warningCount > 0 && failOnValidationWarning)) {
			throw new BuildException(MessageFormat.format(Messages.getString("MarkupTask.3"), errorCount, //$NON-NLS-1$
					warningCount, source));
		}
	}

	/**
	 * Support a nested markup language configuration.
	 */
	public void addConfiguredMarkupLanguageConfiguration(MarkupLanguageConfiguration markupLanguageConfiguration) {
		if (this.markupLanguageConfiguration != null) {
			throw new BuildException(Messages.getString("MarkupTask.tooManyConfigurations")); //$NON-NLS-1$
		}
		this.markupLanguageConfiguration = markupLanguageConfiguration;
	}

	/**
	 * Support a nested markup language configuration.
	 */
	public void addConfigured(MarkupLanguageConfiguration markupLanguageConfiguration) {
		// bug 367633 - this method is required for Ant to enable subclassing of MarkupLanguageConfiguration
		addConfiguredMarkupLanguageConfiguration(markupLanguageConfiguration);
	}

	/**
	 *
	 */
	protected String readFully(File inputFile) {
		StringBuilder w = new StringBuilder((int) inputFile.length());
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(inputFile));
			try {
				Reader r = sourceEncoding == null
						? new InputStreamReader(input)
						: new InputStreamReader(input, sourceEncoding);
				try {
					int i;
					while ((i = r.read()) != -1) {
						w.append((char) i);
					}
				} finally {
					r.close();
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new BuildException(
					MessageFormat.format(Messages.getString("MarkupTask.cannotReadSource"), inputFile, e.getMessage()), //$NON-NLS-1$
					e);
		}
		return w.toString();
	}
}
