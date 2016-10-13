package com.qburst.lekha.arraylistexample;

import android.view.View;

/**
 * Created by user on 12/10/16.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
