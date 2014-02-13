/*=============================================================================#
 # Copyright (c) 2008-2013 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.viewers;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.IDelayedLabelDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;


/**
 * A {@link DecoratingStyledCellLabelProvider} is a
 * {@link DelegatingStyledCellLabelProvider} that uses a nested
 * {@link DelegatingStyledCellLabelProvider.IStyledLabelProvider} to compute
 * styled text label and image and takes a {@link ILabelDecorator} to decorate
 * the label.
 * 
 * <p>
 * Use this label provider as a replacement for the
 * {@link DecoratingLabelProvider} when decorating styled text labels.
 * </p>
 * 
 * <p>
 * The {@link DecoratingStyledCellLabelProvider} will try to evaluate the text
 * decoration added by the {@link ILabelDecorator} and will apply the style
 * returned by {@link #getDecorationStyle(Object)}
 * </p>
 * <p>
 * The {@link ILabelDecorator} can optionally implement {@link IColorDecorator}
 * and {@link IFontDecorator} to provide foreground and background color and
 * font decoration.
 * </p>
 * 
 * Like implementation in JFace but adds:
 * <ul>
 *   <li>#getElementToDecorate(Object)</li>
 * </ul>
 * 
 * @see org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider
 */
public class DecoratingStyledCellLabelProvider extends DelegatingStyledCellLabelProvider {
	
	
	private ILabelDecorator decorator;
	private IDecorationContext decorationContext= DecorationContext.DEFAULT_CONTEXT;
	private final ILabelProviderListener labelProviderListener;
	
	
	/**
	 * Creates a {@link DecoratingStyledCellLabelProvider} that delegates the
	 * requests for styled labels and for images to a
	 * {@link DelegatingStyledCellLabelProvider.IStyledLabelProvider}.
	 * 
	 * @param labelProvider the styled label provider
	 * @param decorator a label decorator or <code>null</code> to not decorate the
	 *         label
	 * @param decorationContext a decoration context or <code>null</code> if the no
	 *          decorator is configured or the default decorator should be used
	 */
	public DecoratingStyledCellLabelProvider(
			final IStyledLabelProvider labelProvider, final ILabelDecorator decorator,
			final IDecorationContext decorationContext) {
		super(labelProvider);
		
		this.decorator = decorator;
		this.decorationContext = (decorationContext != null) ?
				decorationContext : DecorationContext.DEFAULT_CONTEXT;
		
		this.labelProviderListener = new ILabelProviderListener() {
			@Override
			public void labelProviderChanged(final LabelProviderChangedEvent event) {
				fireLabelProviderChanged(event);
			}
		};
		labelProvider.addListener(this.labelProviderListener);
		if (decorator != null) {
			decorator.addListener(this.labelProviderListener);
		}
	}
	
	/**
	 * Returns the decoration context associated with this label provider. It
	 * will be passed to the decorator if the decorator is an instance of
	 * {@link LabelDecorator}.
	 * 
	 * @return the decoration context associated with this label provider
	 */
	public IDecorationContext getDecorationContext() {
		return this.decorationContext;
	}
	
	
	protected Object getElementToDecorate(final Object element) {
		return element;
	}
	
	
	/**
	 * Set the decoration context that will be based to the decorator for this
	 * label provider if that decorator implements {@link LabelDecorator}.
	 * 
	 * @param decorationContext the decoration context.
	 */
	public void setDecorationContext(final IDecorationContext decorationContext) {
		Assert.isNotNull(decorationContext);
		this.decorationContext = decorationContext;
	}
	
	private boolean waitForPendingDecoration(final ViewerCell cell) {
		if (this.decorator == null) {
			return false;
		}
		
		final Object element = cell.getElement();
		final String oldText = cell.getText();
		
		boolean isDecorationPending = false;
		if (this.decorator instanceof LabelDecorator) {
			isDecorationPending = !((LabelDecorator) this.decorator).prepareDecoration(
					getElementToDecorate(element), oldText, getDecorationContext() );
		} else if (this.decorator instanceof IDelayedLabelDecorator) {
			isDecorationPending = !((IDelayedLabelDecorator) this.decorator).prepareDecoration(
					getElementToDecorate(element), oldText );
		}
		if (isDecorationPending && oldText.length() == 0) {
			// item is empty: is shown for the first time: don't wait
			return false;
		}
		return isDecorationPending;
	}
	
	@Override
	public void update(final ViewerCell cell) {
		if (waitForPendingDecoration(cell)) {
			return; // wait until the decoration is ready
		}
		super.update(cell);
	}
	
	@Override
	public Color getForeground(final Object element) {
		if (this.decorator instanceof IColorDecorator) {
			final Color foreground = ((IColorDecorator) this.decorator).decorateForeground(
					getElementToDecorate(element) );
			if (foreground != null) {
				return foreground;
			}
		}
		return super.getForeground(element);
	}
	
	@Override
	public Color getBackground(final Object element) {
		if (this.decorator instanceof IColorDecorator) {
			final Color color = ((IColorDecorator) this.decorator).decorateBackground(
					getElementToDecorate(element) );
			if (color != null) {
				return color;
			}
		}
		return super.getBackground(element);
	}
	
	@Override
	public Font getFont(final Object element) {
		if (this.decorator instanceof IFontDecorator) {
			final Font font = ((IFontDecorator) this.decorator).decorateFont(
					getElementToDecorate(element) );
			if (font != null) {
				return font;
			}
		}
		return super.getFont(element);
	}
	
	@Override
	public Image getImage(final Object element) {
		final Image image = super.getImage(element);
		if (this.decorator == null) {
			return image;
		}
		Image decorated = null;
		if (this.decorator instanceof LabelDecorator) {
			decorated = ((LabelDecorator) this.decorator).decorateImage(image,
					getElementToDecorate(element), getDecorationContext() );
		} else {
			decorated = this.decorator.decorateImage(image,
					getElementToDecorate(element) );
		}
		if (decorated != null) {
			return decorated;
		}
		
		return image;
	}
	
	/**
	 * Returns the styled text for the label of the given element.
	 * 
	 * @param element the element for which to provide the styled label text
	 * @return the styled text string used to label the element
	 */
	@Override
	protected StyledString getStyledText(final Object element) {
		final StyledString styledString = super.getStyledText(element);
		if (this.decorator == null) {
			return styledString;
		}
		
		final String label = styledString.getString();
		String decorated;
		if (this.decorator instanceof LabelDecorator) {
			decorated = ((LabelDecorator) this.decorator).decorateText(label,
					getElementToDecorate(element), getDecorationContext() );
		} else {
			decorated = this.decorator.decorateText(label,
					getElementToDecorate(element) );
		}
		if (decorated == null) {
			return styledString;
		}
		
		final Styler style = getDecorationStyle(element);
		return StyledCellLabelProvider.styleDecoratedString(decorated, style, styledString);
	}
	
	/**
	 * Sets the {@link StyledString.Styler} to be used for string
	 * decorations. By default the
	 * {@link StyledString#DECORATIONS_STYLER decoration style}. Clients
	 * can override.
	 * 
	 * Note that it is the client's responsibility to react on color changes of
	 * the decoration color by refreshing the view
	 * 
	 * @param element the element that has been decorated
	 * 
	 * @return return the decoration style
	 */
	protected Styler getDecorationStyle(final Object element) {
		return StyledString.DECORATIONS_STYLER;
	}
	
	/**
	 * Returns the decorator or <code>null</code> if no decorator is installed
	 * 
	 * @return the decorator or <code>null</code> if no decorator is installed
	 */
	public ILabelDecorator getLabelDecorator() {
		return this.decorator;
	}
	
	/**
	 * Sets the label decorator. Removes all known listeners from the old
	 * decorator, and adds all known listeners to the new decorator. The old
	 * decorator is not disposed. Fires a label provider changed event
	 * indicating that all labels should be updated. Has no effect if the given
	 * decorator is identical to the current one.
	 * 
	 * @param newDecorator the label decorator, or <code>null</code> if no decorations
	 *         are to be applied
	 */
	public void setLabelDecorator(final ILabelDecorator newDecorator) {
		final ILabelDecorator oldDecorator = this.decorator;
		if (oldDecorator != newDecorator) {
			if (oldDecorator != null) {
				oldDecorator.removeListener(this.labelProviderListener);
			}
			this.decorator = newDecorator;
			if (newDecorator != null) {
				newDecorator.addListener(this.labelProviderListener);
			}
		}
		fireLabelProviderChanged(new LabelProviderChangedEvent(this));
	}
	
	@Override
	public void addListener(final ILabelProviderListener listener) {
		super.addListener(listener);
		if (this.decorator != null) {
			this.decorator.addListener(this.labelProviderListener);
		}
	}
	
	@Override
	public void removeListener(final ILabelProviderListener listener) {
		super.removeListener(listener);
		if (this.decorator != null && !isListenerAttached()) {
			this.decorator.removeListener(this.labelProviderListener);
		}
	}
	
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		if (super.isLabelProperty(element, property)) {
			return true;
		}
		return (this.decorator != null
				&& this.decorator.isLabelProperty(getElementToDecorate(element), property) );
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (this.decorator != null) {
			this.decorator.removeListener(this.labelProviderListener);
			this.decorator.dispose();
			this.decorator = null;
		}
	}
	
}
