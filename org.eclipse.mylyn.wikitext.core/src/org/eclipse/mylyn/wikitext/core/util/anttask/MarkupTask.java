/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.util.anttask;

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
import org.eclipse.mylyn.internal.wikitext.core.validation.StandaloneMarkupValidator;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
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
	 * @since 1.3
	 */
	public MarkupLanguageConfiguration getMarkupLanguageConfiguration() {
		return markupLanguageConfiguration;
	}

	/**
	 * @since 1.3
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
			MarkupLanguage language = ServiceLocator.getInstance(getClass().getClassLoader()).getMarkupLanguage(
					markupLanguage);
			if (internalLinkPattern != null) {
				language.setInternalLinkPattern(internalLinkPattern);
			}
			if (markupLanguageConfiguration != null) {
				language.configure(markupLanguageConfiguration);
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

	/**
	 * The source encoding.
	 * 
	 * @return the source encoding, or null if the default encoding is to be used.
	 * @see java.nio.charset.Charset
	 * @since 1.1
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
	 * @since 1.1
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
	 * 
	 * @since 1.1
	 */
	public void addConfiguredMarkupLanguageConfiguration(MarkupLanguageConfiguration markupLanguageConfiguration) {
		if (this.markupLanguageConfiguration != null) {
			throw new BuildException(Messages.getString("MarkupTask.tooManyConfigurations")); //$NON-NLS-1$
		}
		this.markupLanguageConfiguration = markupLanguageConfiguration;
	}

	/**
	 * Support a nested markup language configuration.
	 * 
	 * @since 1.7
	 */
	public void addConfigured(MarkupLanguageConfiguration markupLanguageConfiguration) {
		// bug 367633 - this method is required for Ant to enable subclassing of MarkupLanguageConfiguration
		addConfiguredMarkupLanguageConfiguration(markupLanguageConfiguration);
	}

	/**
	 * @since 1.1
	 */
	protected String readFully(File inputFile) {
		StringBuilder w = new StringBuilder((int) inputFile.length());
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(inputFile));
			try {
				Reader r = sourceEncoding == null ? new InputStreamReader(input) : new InputStreamReader(input,
						sourceEncoding);
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
			throw new BuildException(MessageFormat.format(
					Messages.getString("MarkupTask.cannotReadSource"), inputFile, e.getMessage()), e); //$NON-NLS-1$
		}
		return w.toString();
	}
}
