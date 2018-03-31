/*******************************************************************************
 * Copyright (c) 2010 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
 

 function EclipseIntegration() {
	 this.eclipseRunning = false;
	 this.pollingInterval = 250;
	 this.format = new EclipseStyles();
	 this.pendingCommandIdentifier = "";
	 
 }
 
 
 
 EclipseIntegration.prototype.init = function(editor) {
 	this.editor = editor;
 	this.data = editor.getData();
 	this.format.init(editor);
 	var thisInstance = this;
 	try {
 		this.executeCommand("maximize");
 	} catch (err) {
 		// do nothing
 	}
 	
 	editor.on('selectionChange', function(ev) {thisInstance.selectionChanged(ev)});
 	editor.on('focus', function(ev) {thisInstance.focusGained(ev)});
 	editor.on('blur', function(ev) {thisInstance.focusLost(ev)});
 	this.timerModifications();
 	
 	if (this.eclipseRunning) {
 		_delegate_init();
 	}
 }
 EclipseIntegration.prototype.checkModifications = function() {
	 if (this.data != this.editor.getData()) {
		 if (this.eclipseRunning) {
			 _delegate_modified(this.pendingCommandIdentifier);
		 }
		 this.data = this.editor.getData();
	 }
	 
 }
 EclipseIntegration.prototype.timerModifications = function() {
	 var thisInstance = this;
	 this.checkModifications();
	 window.setTimeout(function() {thisInstance.timerModifications();},this.pollingInterval);
 } 
 
 EclipseIntegration.prototype.modified = function(event) {
	 this.currentModifiedEvent = event;
	 if (this.eclipseRunning) {
		 _delegate_modified(this.pendingCommandIdentifier);
	 }
 }
 EclipseIntegration.prototype.focusGained = function(event) {
	 this.currentFocusEvent = event;
	 editor.focus();
	 
	 editor.getSelection().selectElement( this.currentSelection);
	// editor.getSelection.unlock(true);
	 if (this.eclipseRunning) {
		 _delegate_focus();
	 }
	 this.checkModifications();
 }
 
EclipseIntegration.prototype.focusLost = function(event) {
	//editor.getSelection.lock();
	this.currentSelection = this.editor.getSelection().getStartElement();
 }
 EclipseIntegration.prototype.selectionChanged = function(event) {
	 this.currentSelectionEvent = event;
	 if (this.eclipseRunning) {
		 _delegate_selectionChanged();
	 }
	 this.checkModifications();
 }
 EclipseIntegration.prototype.executeCommand = function(commandName) {
	if (editor.mode == 'wysiwyg') {
		editor.execCommand(commandName);
	}
	else {
		alert( 'You must be on WYSIWYG mode!' );
	}
	
}
 EclipseIntegration.prototype.insertHtml = function(html) {
	if (editor.mode == 'wysiwyg' ) {
		editor.insertHtml( value );
	}
	else {
		alert( 'You must be on WYSIWYG mode!' );
	}
 }
 
