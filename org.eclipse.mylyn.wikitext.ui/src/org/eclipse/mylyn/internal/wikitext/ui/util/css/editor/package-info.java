/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

/**
 * The basics for CSS partitioning and highlighting.  Use with a SourceViewer as follows:
 * 
 * <pre>
 Document document = new Document();
 CssPartitioner partitioner = new CssPartitioner();
 partitioner.connect(document);
 document.setDocumentPartitioner(partitioner);
 sourceViewer.setDocument(document);

 CssConfiguration configuration = new CssConfiguration(colorRegistry);
 sourceViewer.configure(configuration);
 * </pre>
 * 
 * @see CssConfiguration
 * @see CssPartitioner
 * 
 * @author David Green
 */
package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

