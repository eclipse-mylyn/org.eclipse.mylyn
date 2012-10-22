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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetCollector;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexSearcher;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexer;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetSource;

/**
 *
 * @author Kilian Matt
 */
public class ChangeSetIndexer implements IChangeSetIndexSearcher {

	private IChangeSetSource source;

	private File indexDirectory;
	private IndexWriter indexWriter;
	private IndexReader indexReader;

	public ChangeSetIndexer(File directory, IChangeSetSource source) {
		this.indexDirectory = directory;
		this.source = source;
	}


	public void reindex(IProgressMonitor monitor) {
		try {
			indexWriter = new IndexWriter(new NIOFSDirectory(indexDirectory),ChangeSetAnalyzer.get(), true, MaxFieldLength.UNLIMITED);

			IChangeSetIndexer indexer = new IChangeSetIndexer() {

				public void index(ChangeSet changeset) {
					try {
						Document document = new Document();
						for (IndexedFields field : IndexedFields.values()) {
							document.add(new Field(field.getIndexKey(), field.getAccessor().getValue(changeset),
									Store.YES, Field.Index.ANALYZED));
						}
						indexWriter.addDocument(document);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			};
			source.fetchAllChangesets(monitor, indexer);
			indexWriter.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public int search(ITask task, String scmRepositoryUrl, int resultsLimit,IChangeSetCollector collector) throws CoreException {
		int count = 0;
		IndexReader indexReader = getIndexReader();
		if (indexReader != null) {
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			try {
				Query query = createQuery(task, scmRepositoryUrl);
				TopDocs results = indexSearcher.search(query, resultsLimit);
				for (ScoreDoc scoreDoc : results.scoreDocs) {
					Document document = indexReader.document(scoreDoc.doc);
					count++;
					if (count > resultsLimit)
						break;

					String revision = document.getField(IndexedFields.REVISION.getIndexKey()).stringValue();
					String repositoryUrl = document.getField(IndexedFields.REPOSITORY.getIndexKey()).stringValue();

					collector.collect(revision, repositoryUrl);
				}
			} catch (IOException e) {
				// StatusHandler.log(new Status(IStatus.ERROR,
				// org.eclipse.mylyn.versions.tasks.ui.internal.TaPLUGIN_ID,
				//"Unexpected failure within task list index", e)); //$NON-NLS-1$
			} finally {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return count;

	}

	private Query createQuery(ITask task, String repositoryUrl) {
		BooleanQuery query = new BooleanQuery();
		query.setMinimumNumberShouldMatch(1);
		query.add(new TermQuery(new Term(IndexedFields.REPOSITORY.getIndexKey(), repositoryUrl)), Occur.MUST);
		query.add(new PrefixQuery(new Term(IndexedFields.COMMIT_MESSAGE.getIndexKey(), task.getUrl())), Occur.SHOULD);
		query.add(new PrefixQuery(new Term(IndexedFields.COMMIT_MESSAGE.getIndexKey(), task.getTaskId())), Occur.SHOULD);
		query.add(new PrefixQuery(new Term(IndexedFields.COMMIT_MESSAGE.getIndexKey(), task.getTaskKey())), Occur.SHOULD);

		return query;
	}

	private IndexReader getIndexReader() {
		try {
			synchronized (this) {
				if (indexReader == null) {
					indexReader = IndexReader.open(new NIOFSDirectory(
							indexDirectory), true);
				}
				return indexReader;
			}
		} catch (CorruptIndexException e) {
			// rebuild index
		} catch (FileNotFoundException e) {
			// rebuild index
		} catch (IOException e) {
			// ignore
		}
		return null;
	}

}
