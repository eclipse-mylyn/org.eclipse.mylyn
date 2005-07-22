package org.eclipse.mylar.java.ui.editor;

import org.eclipse.core.internal.commands.util.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractMylarHyperlinkDetector implements IHyperlinkDetector {

	private ITextEditor editor;
	
	public AbstractMylarHyperlinkDetector() {
	}
	
	public ITextEditor getEditor() {
		return editor; 
	}
	
	public abstract IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks);

	public void setEditor(ITextEditor editor) {
		Assert.isNotNull(editor);
		this.editor = editor;
	}

}
