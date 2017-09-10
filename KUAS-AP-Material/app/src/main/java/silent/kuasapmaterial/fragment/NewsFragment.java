package silent.kuasapmaterial.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kuas.ap.R;

import silent.kuasapmaterial.base.SilentFragment;
import silent.kuasapmaterial.libs.MaterialProgressBar;
import silent.kuasapmaterial.libs.Utils;

public class NewsFragment extends SilentFragment implements View.OnTouchListener {

    private View view;

    Activity activity;

    WebView mWebView;
    MaterialProgressBar mMaterialProgressBar;

    String mTitle, mContent, mURL;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        restoreArgs(savedInstanceState);
        findView();
        setUpViews();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void restoreArgs(Bundle savedInstanceState) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void findView() {
        mWebView = view.findViewById(R.id.webView);
        mMaterialProgressBar = view.findViewById(R.id.materialProgressBar);
    }

    private void setUpViews() {
        mWebView.setVisibility(View.GONE);
        mMaterialProgressBar.setVisibility(View.VISIBLE);

        mWebView.setBackgroundColor(0);
        mWebView.loadDataWithBaseURL("", mContent, "text/html", "UTF-8", "");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (getActivity().isFinishing()) {
                    return;
                }
                mWebView.setVisibility(View.VISIBLE);
                mMaterialProgressBar.setVisibility(View.GONE);
            }
        });
        //mWebView.setOnTouchListener(this);
    }

    public void setData(String mTitle, String mContent, String mURL) {
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mURL = mURL;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.webView && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            String shareData = mTitle + "\n" + mURL +
                    "\n\n" + getString(R.string.send_from);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            Bitmap icon =
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_white_24dp);
            builder.setActionButton(icon, getString(R.string.share),
                    Utils.createSharePendingIntent(activity, shareData));
            builder.setToolbarColor(ContextCompat.getColor(activity, R.color.main_theme));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(activity, Uri.parse(mURL));
        }
        return false;
    }
}
