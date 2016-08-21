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
package de.walware.ecommons.waltable.style.editor;

import static de.walware.ecommons.waltable.config.CellConfigAttributes.CELL_STYLE;
import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.style.DisplayMode.NORMAL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.ILValueIterator;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ColumnOverrideLabelAccumulator;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.persistence.StylePersistor;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.Style;


/**
 * 
 * 1. Captures a new style using the <code>StyleEditorDialog</code> 
 * 2. Registers style from step 1 in the <code>ConfigRegistry</code> with a new label 
 * 3. Applies the label from step 2 to all cells in the selected column
 * 
 */
public class DisplayColumnStyleEditorCommandHandler extends AbstractLayerCommandHandler<DisplayColumnStyleEditorCommand> implements IPersistable {
	
	protected static final String PERSISTENCE_PREFIX= "userDefinedColumnStyle"; //$NON-NLS-1$
	protected static final String USER_EDITED_STYLE_LABEL= "USER_EDITED_STYLE_FOR_INDEX_"; //$NON-NLS-1$

	protected final SelectionLayer selectionLayer;
	protected ColumnOverrideLabelAccumulator columnLabelAccumulator;
	private final IConfigRegistry configRegistry;
	protected ColumnStyleEditorDialog dialog;
	protected final Map<String, Style> stylesToPersist= new HashMap<>();

	public DisplayColumnStyleEditorCommandHandler(final SelectionLayer selectionLayer, final ColumnOverrideLabelAccumulator labelAccumulator, final IConfigRegistry configRegistry) {
		this.selectionLayer= selectionLayer;
		this.columnLabelAccumulator= labelAccumulator;
		this.configRegistry= configRegistry;
	}

	@Override
	public boolean doCommand(final DisplayColumnStyleEditorCommand command) {
		final long columnIndexOfClick= command.getNattableLayer().getDim(HORIZONTAL)
				.getPositionId(command.columnPosition, command.columnPosition);
		
		final LabelStack configLabels= new LabelStack();
		this.columnLabelAccumulator.accumulateConfigLabels(configLabels, columnIndexOfClick, 0);
		configLabels.addLabel(getConfigLabel(columnIndexOfClick));
		
		// Column style
		final Style clickedCellStyle= (Style) this.configRegistry.getConfigAttribute(CELL_STYLE, NORMAL, configLabels.getLabels());
		
		this.dialog= new ColumnStyleEditorDialog(Display.getCurrent().getActiveShell(), clickedCellStyle);
		this.dialog.open();

		if(this.dialog.isCancelPressed()) {
			return true;
		}
		
		applySelectedStyleToColumns(command, this.selectionLayer.getSelectedColumnPositions());
		return true;
	}

	@Override
	public Class<DisplayColumnStyleEditorCommand> getCommandClass() {
		return DisplayColumnStyleEditorCommand.class;
	}

	protected void applySelectedStyleToColumns(final DisplayColumnStyleEditorCommand command,
			final LRangeList columnPositions) {
		for (final ILValueIterator columnIter= columnPositions.values().iterator(); columnIter.hasNext(); ) {
			final long position= columnIter.nextValue();
			final long columnIndex= this.selectionLayer.getDim(HORIZONTAL).getPositionId(position, position);
			// Read the edited styles
			final Style newColumnCellStyle= this.dialog.getNewColumnCellStyle(); 
			
			final String configLabel= getConfigLabel(columnIndex);
			if (newColumnCellStyle == null) {
				this.stylesToPersist.remove(configLabel);
			} else {
				newColumnCellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.dialog.getNewColumnBorderStyle());
				this.stylesToPersist.put(configLabel, newColumnCellStyle);
			}
			this.configRegistry.registerConfigAttribute(CELL_STYLE, newColumnCellStyle, NORMAL, configLabel);
			this.columnLabelAccumulator.registerColumnOverridesOnTop(columnIndex, configLabel);
		}
	}

	protected String getConfigLabel(final long columnIndex) {
		return USER_EDITED_STYLE_LABEL + columnIndex;
	}

	@Override
	public void loadState(String prefix, final Properties properties) {
		prefix= prefix + DOT + PERSISTENCE_PREFIX;
		final Set<Object> keySet= properties.keySet();

		for (final Object key : keySet) {
			final String keyString= (String) key;

			// Relevant Key
			if (keyString.contains(PERSISTENCE_PREFIX)) {
				final long colIndex= parseColumnIndexFromKey(keyString);

				// Has the config label been processed
				if (!this.stylesToPersist.keySet().contains(getConfigLabel(colIndex))) {
					final Style savedStyle= StylePersistor.loadStyle(prefix + DOT + getConfigLabel(colIndex), properties);

					this.configRegistry.registerConfigAttribute(CELL_STYLE, savedStyle, NORMAL, getConfigLabel(colIndex));
					this.stylesToPersist.put(getConfigLabel(colIndex), savedStyle);
					this.columnLabelAccumulator.registerColumnOverrides(colIndex, getConfigLabel(colIndex));
				}
			}
		}
	}

	protected long parseColumnIndexFromKey(final String keyString) {
		final int colLabelStartIndex= keyString.indexOf(USER_EDITED_STYLE_LABEL);
		final String columnConfigLabel= keyString.substring(colLabelStartIndex, keyString.indexOf('.', colLabelStartIndex));
		final int lastUnderscoreInLabel= columnConfigLabel.lastIndexOf('_', colLabelStartIndex);

		return Long.parseLong(columnConfigLabel.substring(lastUnderscoreInLabel + 1));
	}

	@Override
	public void saveState(String prefix, final Properties properties) {
		prefix= prefix + DOT + PERSISTENCE_PREFIX;

		for (final Map.Entry<String, Style> labelToStyle : this.stylesToPersist.entrySet()) {
			final Style style= labelToStyle.getValue();
			final String label= labelToStyle.getKey();

			StylePersistor.saveStyle(prefix + DOT + label, properties, style);
		}
	}
}
