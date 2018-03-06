package silent.kuasapmaterial.libs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Ray on 2017/9/10.
 */

public class NewsPagerTransformer implements ViewPager.PageTransformer {

	private int maxTranslateOffsetX;
	private ViewPager viewPager;

	public NewsPagerTransformer(Context context) {
		this.maxTranslateOffsetX = dp2px(context, 180);
	}

	public void transformPage(View view, float position) {
		if (viewPager == null) {
			viewPager = (ViewPager) view.getParent();
		}

		int leftInScreen = view.getLeft() - viewPager.getScrollX();
		int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
		int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
		float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
		float scaleFactor = 1 - Math.abs(offsetRate);
		if (scaleFactor > 0) {
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
			view.setTranslationX(-maxTranslateOffsetX * offsetRate);
		}
	}

	/**
	 * dp和像素转换
	 */
	private int dp2px(Context context, float dipValue) {
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * m + 0.5f);
	}

}
