/*******************************************************************************
 * Copyright (c) 2011, 2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.internal.EclipseTocImporter;
import org.eclipse.mylyn.docs.epub.opf.Role;

/**
 * Assemble a new EPUB.
 *
 * @author Torkild U. Resheim
 * @ant.task name="epub" category="epub"
 */
@SuppressWarnings("restriction")
public class EpubTask extends Task {

	/** Ant task only supports EPUB 2.0 */
	private OPSPublication oebps = null;

	private ArrayList<FileSetType> filesets = null;

	private TocType toc = null;

	private File workingFolder;

	private File epubFile;

	private AntLogger logger;

	public EpubTask() {
		super();
		try {
			logger = new AntLogger(this);
			oebps = new OPSPublication(logger);
			filesets = new ArrayList<FileSetType>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addConfiguredContributor(ContributorType item) {
		if (item.role == null) {
			oebps.addContributor(item.id, item.lang, item.name, null, item.fileAs);
		} else {
			oebps.addContributor(item.id, item.lang, item.name, Role.get(item.role), item.fileAs);
		}
	}

	public void addConfiguredCover(CoverType item) {
		oebps.setCover(new File(item.image), item.value);
	}

	public void addConfiguredCoverage(CoverageType coverage) {
		oebps.addCoverage(coverage.id, coverage.lang, coverage.text);
	}

	public void addConfiguredCreator(CreatorType item) {
		if (item.role == null) {
			oebps.addCreator(item.id, item.lang, item.name, null, item.fileAs);
		} else {
			oebps.addCreator(item.id, item.lang, item.name, Role.get(item.role), item.fileAs);
		}
	}

	/**
	 * @ant.not-required
	 */
	public void addConfiguredDate(DateType item) {
		oebps.addDate(item.id, item.date, item.event);
	}

	/**
	 * @ant.not-required Add fileset to publication.
	 */
	public void addConfiguredFileSet(FileSetType fs) {
		filesets.add(fs);
	}

	/**
	 * @ant.not-required
	 */
	public void addConfiguredFormat(FormatType format) {
		oebps.addFormat(format.id, format.text);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredIdentifier(IdentifierType identifier) {
		oebps.addIdentifier(identifier.id, identifier.scheme, identifier.value);
	}

	/**
	 * @since 2.1
	 */
	public void addConfiguredImport(ImportType includeType) {
		if (!"eclipse-help".equals(includeType.format)) { //$NON-NLS-1$
			throw new BuildException("Unsupported include format specified."); //$NON-NLS-1$
		}
		if (!includeType.file.exists()) {
			throw new BuildException("Include file does not exist."); //$NON-NLS-1$
		}
		try {
			EclipseTocImporter.importFile(oebps, includeType.file);
		} catch (Exception e) {
			throw new BuildException("Could not import Eclipse Table of Contents", e); //$NON-NLS-1$
		}
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredItem(ItemType item) {
		oebps.addItem(item.id, item.lang, item.file, item.dest, item.type, item.spine, item.linear, item.noToc);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredLanguage(LanguageType language) {
		oebps.addLanguage(language.id, language.code);
	}

	public void addConfiguredMeta(MetaType item) {
		oebps.addMeta(item.name, item.content);
	}

	public void addConfiguredPublisher(PublisherType publisher) {
		oebps.addPublisher(publisher.id, publisher.lang, publisher.text);
	}

	public void addConfiguredReference(ReferenceType reference) {
		oebps.addReference(reference.href, reference.title, reference.type);
	}

	public void addConfiguredRelation(RelationType relation) {
		oebps.addRelation(relation.id, relation.lang, relation.text);
	}

	public void addConfiguredRights(RightsType rights) {
		oebps.addRights(rights.id, rights.lang, rights.text);
	}

	public void addConfiguredSource(SourceType source) {
		oebps.addSource(source.id, source.lang, source.text);
	}

	public void addConfiguredSubject(SubjectType subject) {
		oebps.addSubject(subject.id, subject.lang, subject.text);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredTitle(TitleType title) {
		oebps.addTitle(title.id, title.lang, title.text);
	}

	public void addConfiguredToc(TocType toc) {
		if (this.toc != null) {
			throw new BuildException("Only one table of contents (toc) declaration is allowed."); //$NON-NLS-1$
		}
		this.toc = toc;
	}

	public void addConfiguredType(org.eclipse.mylyn.docs.epub.ant.core.TypeType type) {
		oebps.addType(type.id, type.text);
	}

	private void addFilesets() {
		for (FileSetType fs : filesets) {
			final File fsDir = fs.getDir(getProject());
			if (fsDir == null) {
				throw new BuildException("File or Resource without directory or file specified"); //$NON-NLS-1$
			} else if (!fsDir.isDirectory()) {
				throw new BuildException("Directory does not exist:" + fsDir); //$NON-NLS-1$
			}
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] includedFiles = ds.getIncludedFiles();
			for (String includedFile : includedFiles) {
				String filename = includedFile.replace('\\', '/');
				filename = filename.substring(filename.lastIndexOf('/') + 1);
				File base = ds.getBasedir();
				File found = new File(base, includedFile);
				oebps.addItem(null, fs.lang, found, fs.dest, null, false, true, false);
			}

		}

	}

	@Override
	public void execute() throws BuildException {
		// When running from within Eclipse, the project may not have been set
		if (getProject() == null) {
			Project project = new Project();
			setProject(project);
		}

		addFilesets();
		if (toc != null) {
			if (toc.generate) {
				oebps.setGenerateToc(true);
			} else if (toc.file != null) {
				oebps.setTableOfContents(toc.file);
			}
		}
		try {
			EPUB epub = new EPUB(logger);
			epub.add(oebps);
			if (workingFolder == null) {
				epub.pack(epubFile);
			} else {
				epub.pack(epubFile, workingFolder);
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	/**
	 * @param file
	 *            path to the generated EPUB file.
	 */
	public void setFile(File file) {
		this.epubFile = file;
	}

	public void setIdentifierId(String identifierId) {
		oebps.setIdentifierId(identifierId);
	}

	/**
	 * @ant.not-required Automatically add referenced resources.
	 */
	public void setIncludeReferenced(boolean automatic) {
		oebps.setIncludeReferencedResources(automatic);
	}

	public void setWorkingFolder(File workingFolder) {
		this.workingFolder = workingFolder;
	}
}
