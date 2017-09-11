package silent.kuasapmaterial.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kuas.ap.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import silent.kuasapmaterial.base.SilentFragment;
import silent.kuasapmaterial.libs.MaterialProgressBar;
import silent.kuasapmaterial.libs.Utils;
import silent.kuasapmaterial.models.NewsModel;

public class NewsFragment extends SilentFragment implements View.OnClickListener {

    private View view;

    Activity activity;

    ImageView imageView;
    MaterialProgressBar mMaterialProgressBar;

    NewsModel newsModel;

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
        imageView = view.findViewById(R.id.imageView);
        mMaterialProgressBar = view.findViewById(R.id.materialProgressBar);
    }

    private void setUpViews() {
        mMaterialProgressBar.setVisibility(View.VISIBLE);

        imageView.setBackgroundColor(0);
        ImageLoader.getInstance().displayImage(newsModel.image, imageView,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                imageView.setImageBitmap(loadedImage);
                mMaterialProgressBar.setVisibility(View.GONE);
            }
        });
        imageView.setOnClickListener(this);
    }

    public void setData(NewsModel newsModel) {
        this.newsModel = newsModel;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageView) {
            String shareData = newsModel.title + "\n" + newsModel.url +
                    "\n\n" + getString(R.string.send_from);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            Bitmap icon =
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_white_24dp);
            builder.setActionButton(icon, getString(R.string.share),
                    Utils.createSharePendingIntent(activity, shareData));
            builder.setToolbarColor(ContextCompat.getColor(activity, R.color.main_theme));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(activity, Uri.parse(newsModel.url));
        }
    }
}
