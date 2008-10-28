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
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.token;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class TemplateReplacementToken extends PatternBasedElement {

	private static Map<String, Class<? extends PatternBasedElementProcessor>> processorByTemplate = new HashMap<String, Class<? extends PatternBasedElementProcessor>>();
	static {
		processorByTemplate.put("endash", EndashElementProcessor.class); //$NON-NLS-1$
		processorByTemplate.put("ndash", EndashElementProcessor.class); //$NON-NLS-1$
		processorByTemplate.put("mdash", EmdashElementProcessor.class); //$NON-NLS-1$
		processorByTemplate.put("emdash", EmdashElementProcessor.class); //$NON-NLS-1$
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "(\\{\\{([^\\s]+)\\}\\})"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new DispatchingProcessor();
	}

	private static class DispatchingProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			Class<? extends PatternBasedElementProcessor> processor = processorByTemplate.get(group(2));
			if (processor == null) {
				getBuilder().characters(group(1));
			} else {
				PatternBasedElementProcessor delegate;
				try {
					delegate = processor.newInstance();
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				delegate.setLineStartOffset(getLineStartOffset());
				delegate.setLineEndOffset(getLineEndOffset());
				delegate.setParser(getParser());
				delegate.setState(getState());
				delegate.setGroup(1, group(1), start(1), end(1));
				delegate.setGroup(2, group(2), start(2), end(2));
				delegate.emit();
			}
		}
	}

	public static class EndashElementProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			getBuilder().entityReference("nbsp"); //$NON-NLS-1$
			getBuilder().entityReference("ndash"); //$NON-NLS-1$
			getBuilder().characters(" "); //$NON-NLS-1$
		}
	}

	public static class EmdashElementProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			getBuilder().entityReference("nbsp"); //$NON-NLS-1$
			getBuilder().entityReference("mdash"); //$NON-NLS-1$
			getBuilder().characters(" "); //$NON-NLS-1$
		}
	}
}
