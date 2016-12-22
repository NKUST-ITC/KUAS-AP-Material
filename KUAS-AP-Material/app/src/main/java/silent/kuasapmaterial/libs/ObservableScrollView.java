package silent.kuasapmaterial.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

	private OnScrollListener mOnScrollListener;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mOnScrollListener == null) {
			return;
		}
		if (t < oldt) {
			mOnScrollListener.onScrollUp();
		} else {
			mOnScrollListener.onScrollDown();
		}
	}

	public void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	public interface OnScrollListener {

		void onScrollDown();

		void onScrollUp();
	}
}
