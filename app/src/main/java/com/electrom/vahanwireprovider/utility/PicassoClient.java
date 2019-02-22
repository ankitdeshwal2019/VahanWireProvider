package com.electrom.vahanwireprovider.utility;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context c, String url, ImageView i)
    {
        Picasso
                .with(c)
                .load(url)
                //.placeholder(c.getResources().getDrawable(R.drawable.logo))
                //.error(c.getResources().getDrawable(R.drawable.logo))
                .into(i);
    }
}
