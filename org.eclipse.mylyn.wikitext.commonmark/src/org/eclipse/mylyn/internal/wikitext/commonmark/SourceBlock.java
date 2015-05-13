/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.commonmark;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;

public abstract class SourceBlock {

	public abstract void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence);

	public abstract boolean canStart(LineSequence lineSequence);

	public ProcessingContext createContext(LineSequence lineSequence) {
		ProcessingContext context = ProcessingContext.empty();
		process(context, new NoOpDocumentBuilder(), lineSequence);
		return context;
	}
}
