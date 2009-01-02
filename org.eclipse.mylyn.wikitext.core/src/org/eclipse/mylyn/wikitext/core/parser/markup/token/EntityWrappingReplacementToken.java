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
package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A replacement token for segments of text that are delimited by specified text. Useful for replacing delimiting
 * characters, for example text wrapped by double quotes could be replaced with the same text delimited by single
 * quotes.
 * 
 * @author David Green
 */
public class EntityWrappingReplacementToken extends PatternBasedElement {

	private final String delimiter;

	private final String leftEntity;

	private final String rightEntity;

	public EntityWrappingReplacementToken(String delimiter, String leftEntity, String rightEntity) {
		this.delimiter = delimiter;
		this.leftEntity = leftEntity;
		this.rightEntity = rightEntity;
		if (delimiter.length() != 1) {
			throw new IllegalArgumentException(delimiter);
		}
	}

	@Override
	public String getPattern(int groupOffset) {
		String quoted = Character.isLetterOrDigit(delimiter.charAt(0)) ? delimiter : "\\" + delimiter; //$NON-NLS-1$
		return "(?:(?:(?<=\\W)|^)" + quoted + "([^" + quoted + "]+)" + quoted + "(?=\\W))"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EntityWrappingReplacementTokenProcessor(leftEntity, rightEntity);
	}

	private static class EntityWrappingReplacementTokenProcessor extends PatternBasedElementProcessor {
		private final String leftEntity;

		private final String rightEntity;

		public EntityWrappingReplacementTokenProcessor(String leftEntity, String rightEntity) {
			this.leftEntity = leftEntity;
			this.rightEntity = rightEntity;
		}

		@Override
		public void emit() {
			String content = group(1);
			builder.entityReference(leftEntity);
			builder.characters(content);
			builder.entityReference(rightEntity);
		}
	}

}
