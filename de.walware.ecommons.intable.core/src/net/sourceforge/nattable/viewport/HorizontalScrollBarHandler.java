package net.sourceforge.nattable.viewport;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.layer.LayerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Listener for the Horizontal scroll bar events on the Viewport Layer. State is
 * exposed to this class from the viewport, since it works in close conjnuction
 * with it.
 */
public class HorizontalScrollBarHandler extends ScrollBarHandlerTemplate {

	public HorizontalScrollBarHandler(ViewportLayer viewportLayer, ScrollBar scrollBar) {
		super(viewportLayer, scrollBar);
		
	}

	/**
	 * In a normal scenario scroll by the width of the viewport. 
	 * If the col being scrolled is wider than above, use the col width
	 */
	@Override
	int pageScrollDistance() {
		int widthOfColBeingScrolled = scrollableLayer.getColumnWidthByPosition(getScrollablePosition());
		int viewportWidth = viewportLayer.getClientAreaWidth(); 
		int scrollWidth = (widthOfColBeingScrolled > viewportWidth) ? widthOfColBeingScrolled : viewportWidth;
		return scrollWidth;
	}
	
	@Override
	int getSpanByPosition(int scrollablePosition) {
		return scrollableLayer.getColumnWidthByPosition(scrollablePosition);
	}
	
	@Override
	int getScrollablePosition() {
		return LayerUtil.convertColumnPosition(viewportLayer, 0, scrollableLayer);
//		int scrollablePosition = 
//				LayerUtil.convertColumnPosition(viewportLayer, 0, scrollableLayer);
//		int adjustedScrollablePosition =
//				scrollablePosition - viewportLayer.getMinimumOriginColumnPosition();
//		return adjustedScrollablePosition;
	}
	
	@Override
	int getStartPixelOfPosition(int position){
		return scrollableLayer.getStartXOfColumnPosition(position);
	}
	
	@Override
	int getPositionByPixel(int pixelValue) {
		return scrollableLayer.getColumnPositionByX(pixelValue);
	}

	@Override
	void setViewportOrigin(int position) {
		viewportLayer.invalidateHorizontalStructure();
//		viewportLayer.setOriginColumnPosition(
//				viewportLayer.getMinimumOriginColumnPosition() + position );
		viewportLayer.setOriginColumnPosition(position);
		scrollBar.setIncrement(viewportLayer.getColumnWidthByPosition(0));
	}
	
	@Override
	Direction scrollDirectionForEventDetail(int eventDetail){
		return (eventDetail == SWT.PAGE_UP || eventDetail == SWT.ARROW_UP ) ?
				Direction.LEFT : Direction.RIGHT;
	}
	
	@Override
	boolean keepScrolling() {
		return !viewportLayer.isLastColumnCompletelyDisplayed();
	}
	
	@Override
	int getViewportWindowSpan() {
		return viewportLayer.getClientAreaWidth();
	}

	@Override
	int getScrollableLayerSpan() {
		return scrollableLayer.getWidth();
	}
	
}