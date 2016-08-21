/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.edit.config;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.waltable.edit.editor.AbstractEditErrorHandler;
import de.walware.ecommons.waltable.edit.editor.ControlDecorationProvider;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.edit.editor.IEditErrorHandler;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.IStyle;
import de.walware.ecommons.waltable.style.Style;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * {@link IEditErrorHandler} implementation that will directly change the rendering
 * of the value inside the editor control.
 */
public class RenderErrorHandling extends AbstractEditErrorHandler {

	/**
	 * The default error styling used for rendering an error.
	 */
	protected IStyle defaultErrorStyle;
	{
        this.defaultErrorStyle= new Style();
        this.defaultErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
	}
	
	/**
	 * The original foreground color, needed to be able to restore the
	 * normal rendering if the error is removed.
	 */
	protected Color originalFgColor;
	/**
	 * The original background color, needed to be able to restore the
	 * normal rendering if the error is removed.
	 */
	protected Color originalBgColor;
	/**
	 * The original font, needed to be able to restore the
	 * normal rendering if the error is removed.
	 */
	protected Font originalFont;
	/**
	 * The style that should be used to render an error.
	 */
	protected IStyle errorStyle;
	/**
	 * The decoration provider that should be used for decorating the
	 * editor control on error.
	 */
	protected final ControlDecorationProvider decorationProvider;
	
	/**
	 * Flag to know whether currently a error styling is applied or not.
	 * This is necessary because first the error styling will be removed and
	 * afterwards it will be applied again if the value is still invalid.
	 * Without this flag the wrong original values would be stored.
	 */
	private boolean errorStylingActive= false;
	
	/**
	 * Create a {@link RenderErrorHandling} with no underlying error handler
	 * and no decoration provider. 
	 */
	public RenderErrorHandling() {
		this(null);
	}
	
	/**
	 * Create a {@link RenderErrorHandling} with no underlying error handler
	 * and the specified decoration provider.
	 * @param decorationProvider The decoration provider that should be used for decorating the
	 * 			editor control on error.
	 */
	public RenderErrorHandling(final ControlDecorationProvider decorationProvider) {
        this(null, decorationProvider);
	}
	
	/**
	 * Create a {@link RenderErrorHandling} with the underlying error handler
	 * and the specified decoration provider.
	 * By default the error style is set to render the value in the editor control
	 * with red foreground color. You can override that style by calling setErrorStyle(IStyle)
	 * @param underlyingErrorHandler The underlying error handler.
	 * @param decorationProvider The decoration provider that should be used for decorating the
	 * 			editor control on error.
	 */
    public RenderErrorHandling(final IEditErrorHandler underlyingErrorHandler, final ControlDecorationProvider decorationProvider) {
		super(underlyingErrorHandler);
        this.decorationProvider= decorationProvider;
        this.errorStyle= this.defaultErrorStyle;
	}
	
    /**
	 * {@inheritDoc}
	 * After the error remove is handled by its underlying {@link IEditErrorHandler},
	 * the original style will be applied to the editor control.
     */
	@Override
	public void removeError(final ICellEditor cellEditor) {
		super.removeError(cellEditor);
		
		if (this.errorStylingActive) {
			final Control editorControl= cellEditor.getEditorControl();
			
			//reset the rendering information to normal
			editorControl.setBackground(this.originalBgColor);
			editorControl.setForeground(this.originalFgColor);
			editorControl.setFont(this.originalFont);
			
			//ensure to reset the stored original values so possible
			//dynamic rendering aspects are also covered
			this.originalBgColor= null;
			this.originalFgColor= null;
			this.originalFont= null;
			
			if (this.decorationProvider != null) {
				this.decorationProvider.hideDecoration();
			}
			
			this.errorStylingActive= false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * After the error is handled by its underlying {@link IEditErrorHandler},
	 * the configured error style will be applied to the editor control.
	 */
	@Override
	public void displayError(final ICellEditor cellEditor, final Exception e) {
		super.displayError(cellEditor, e);
		
		if (!this.errorStylingActive) {
			final Control editorControl= cellEditor.getEditorControl();
			
			//store the current rendering information to be able to reset again
			this.originalBgColor= editorControl.getBackground();
			this.originalFgColor= editorControl.getForeground();
			this.originalFont= editorControl.getFont();
			
			//set the rendering information out of the error style
			editorControl.setBackground(this.errorStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
			editorControl.setForeground(this.errorStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
			editorControl.setFont(this.errorStyle.getAttributeValue(CellStyleAttributes.FONT));
			
			if (this.decorationProvider != null) {
				this.decorationProvider.showDecoration();
			}
			
			this.errorStylingActive= true;
		}
	}

	/**
	 * @param errorStyle The style that should be used to render an error.
	 * 			Supported style attributes are foreground color, background color and font.
	 */
	public void setErrorStyle(final IStyle errorStyle) {
		this.errorStyle= errorStyle != null ? errorStyle : this.defaultErrorStyle;
	}

}
