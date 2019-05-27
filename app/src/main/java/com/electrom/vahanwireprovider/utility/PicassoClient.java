package com.electrom.vahanwireprovider.utility;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context c, String url, ImageView i)
    {

        if(url!=null && url.length() > 0)
        {
            Picasso
                    .with(c)
                    .load(url)
                    //.resize(300,300)
                    //.placeholder(c.getResources().getDrawable(R.drawable.logo))
                    //.error(c.getResources().getDrawable(R.drawable.logo))
                    .into(i);
        }
    }
}
