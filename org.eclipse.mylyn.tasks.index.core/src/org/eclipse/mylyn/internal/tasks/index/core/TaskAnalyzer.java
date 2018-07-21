/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Red Hat, Inc. Bug 384685 - consume Apache Lucene 3.x
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.index.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * An analyzer that is aware of task fields
 *
 * @author David Green
 */
class TaskAnalyzer {

	public static PerFieldAnalyzerWrapper instance() {
		Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
		analyzerPerField.put(TaskListIndex.FIELD_IDENTIFIER.getIndexKey(), new KeywordAnalyzer());
		analyzerPerField.put(TaskListIndex.FIELD_TASK_KEY.getIndexKey(), new KeywordAnalyzer());
		analyzerPerField.put(TaskListIndex.FIELD_REPOSITORY_URL.getIndexKey(), new KeywordAnalyzer());
		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		return wrapper;
	}
}
