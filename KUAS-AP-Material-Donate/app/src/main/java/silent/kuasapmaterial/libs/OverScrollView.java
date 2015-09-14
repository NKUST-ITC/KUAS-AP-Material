package silent.kuasapmaterial.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class OverScrollView extends ScrollView {

	private OnOverScrolledListener mOnOverScrolledListener;

	private int mOverScrollByDeltaX;
	private int mOverScrollByDeltaY;

	public OverScrollView(Context context) {
		super(context);
	}

	public OverScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OverScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
	                               int scrollRangeX, int scrollRangeY, int maxOverScrollX,
	                               int maxOverScrollY, boolean isTouchEvent) {
		this.mOverScrollByDeltaX = deltaX;
		this.mOverScrollByDeltaY = deltaY;
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
				maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	;

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		if (mOnOverScrolledListener != null && (clampedX || clampedY)) {
			mOnOverScrolledListener
					.onOverScrolled(this, mOverScrollByDeltaX, mOverScrollByDeltaY, clampedX,
							clampedY);
		}
	}

	/**
	 * @return the OnOverScrolledListener
	 */
	public OnOverScrolledListener getOnOverScrolledListener() {
		return mOnOverScrolledListener;
	}

	/**
	 * @param listener the OnOverScrolledListener to set
	 */
	public void setOnOverScrolledListener(OnOverScrolledListener listener) {
		this.mOnOverScrolledListener = listener;
	}

	public interface OnOverScrolledListener {
		void onOverScrolled(android.widget.ScrollView scrollView, int deltaX, int deltaY,
		                    boolean clampedX, boolean clampedY);
	}
}