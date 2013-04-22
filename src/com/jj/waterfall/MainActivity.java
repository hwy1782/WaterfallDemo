package com.jj.waterfall;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements
        LazyScrollView.OnScrollListener {
    /** Called when the activity is first created. */
    private LinearLayout layout01, layout02, layout03;// 3列

    private List<String> image_filenames; // 图片集合

    private ImageDownLoadAsyncTask asyncTask;

    private AssetManager assetManager;

    private int Image_width;// 图片显示的宽度

    private int x, y;// 行，列

    private int current_page = 0;// 页码
    private int count = 15;// 每页显示的个数

    private LazyScrollView lazyScrollView;// 自定义scrollview

    private LinearLayout progressbar;// 进度条

    private TextView loadtext;// 底部加载view

    private String tag = "jj";

    /***
     * init view
     */
    public void InitView() {
        setContentView(R.layout.main);
        lazyScrollView = (LazyScrollView) findViewById(R.id.lazyscrollview);
        progressbar = (LinearLayout) findViewById(R.id.progressbar);
        loadtext = (TextView) findViewById(R.id.loadtext);
        lazyScrollView.getView();
        lazyScrollView.setOnScrollListener(this);
        layout01 = (LinearLayout) findViewById(R.id.layout01);
        layout02 = (LinearLayout) findViewById(R.id.layout02);
        layout03 = (LinearLayout) findViewById(R.id.layout03);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        InitView();
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);

        assetManager = this.getAssets();
        // 获取显示图片宽度
        Image_width = (getWindowManager().getDefaultDisplay().getWidth() - 4) / 3;
        try {
            image_filenames = Arrays.asList(assetManager.list("images"));// 获取图片名称
        } catch (IOException e) {
            e.printStackTrace();
        }

        addImage(current_page, count);

    }

    /***
     * 添加imageview 到layout
     *
     * @param imagePath
     *            图片name
     * @param j
     *            列
     * @param i
     *            行
     */
    public void addBitMapToImage(String imagePath, int j, int i) {
        ImageView imageView = getImageview();
        asyncTask = new ImageDownLoadAsyncTask(this, imagePath, imageView,
                Image_width);
        asyncTask.setProgressbar(progressbar);
        asyncTask.setLoadtext(loadtext);
        asyncTask.execute();

        imageView.setTag(i);
        if (j == 0) {
            layout01.addView(imageView);
        } else if (j == 1) {
            layout02.addView(imageView);
        } else if (j == 2) {
            layout03.addView(imageView);
        }

        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "您点击了" + v.getTag() + "个Item", Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }

    /***
     *
     * 创建显示imageview setScaleType 详解： CENTER /center
     * 按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截 取图片的居中部分显示 CENTER_CROP / centerCrop
     * 按比例扩大图片的size居中显示，使得图片长 (宽)等于或大于View的长(宽) CENTER_INSIDE / centerInside
     * 将图片的内容完整居中显示，通过按比例缩小 或原来的size使得图片长/宽等于或小于View的长/宽 FIT_CENTER / fitCenter
     * 把图片按比例扩大/缩小到View的宽度，居中显示 FIT_END / fitEnd 把
     * 图片按比例扩大/缩小到View的宽度，显示在View的下部分位置 FIT_START / fitStart 把
     * 图片按比例扩大/缩小到View的宽度，显示在View的上部分位置 FIT_XY / fitXY 把图片 不按比例 扩大/缩小到View的大小显示
     * MATRIX / matrix 用矩阵来绘制
     *
     * @return
     */
    public ImageView getImageview() {
        // 创建显示图片的对象
        ImageView imageView = new ImageView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(2, 0, 2, 2);// 设置间距
        return imageView;
    }

    @Override
    public void onBottom() {
        addImage(++current_page, count);
    }

    /***
     * 加载更多
     *
     * @param current_page
     *            当前页数
     * @param count
     *            每页显示个数
     */
    private void addImage(int current_page, int count) {
        for (int x = current_page * count; x < (current_page + 1) * count
                && x < image_filenames.size(); x++) {
            addBitMapToImage(image_filenames.get(x), y, x);
            y++;
            if (y >= 3)
                y = 0;
        }

    }

    @Override
    public void onTop() {
        Log.d(tag, "top");
    }

    @Override
    public void onScroll() {
        Log.d(tag, "scroll");
    }
}