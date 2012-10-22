/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 *
 * @author Kilian Matt
 *
 */
public class ChangeSetAnalyzer  {
		public static Analyzer get() {
			PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_CURRENT));
			analyzer.addAnalyzer(IndexedFields.REPOSITORY.getIndexKey(), new KeywordAnalyzer());
			analyzer.addAnalyzer(IndexedFields.COMMIT_MESSAGE.getIndexKey(), new Analyzer() {
				@Override
				public TokenStream tokenStream(String fieldName, Reader reader) {
					WhitespaceAnalyzer delegate =new WhitespaceAnalyzer();
					TokenStream tokenStream = delegate.tokenStream(fieldName, reader);
					BracketFilter filteredStream = new BracketFilter(tokenStream);
					return filteredStream;
				}
			});
			return analyzer;
		}
	}