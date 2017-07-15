package com.dietbuddy.tralice.mapsapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

public class ParallaxToolbarScrollViewActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private ImageView mImageView;
    private TextView description;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax_toolbar_scroll_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mImageView = (ImageView)findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        description= (TextView) findViewById(R.id.body);

        Intent i=getIntent();
        Bundle bun=this.getIntent().getExtras();
        int image=bun.getInt(MyAdapter.EXTRA_MESSAGE_IMAGE);
        String des_title=bun.getString(MyAdapter.EXTRA_MESSAGE_DESCRIPTION_TITLE);
        int des=bun.getInt(MyAdapter.EXTRA_MESSAGE_DESCRIPTION);
        //Fill Image By Clickable
        mImageView.setImageResource(image);
        getSupportActionBar().setTitle(des_title);
        ;
        //description.setText(getResources().getString(des));
        description.setText(Html.fromHtml((getResources().getString(des))));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight=mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / 120);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
