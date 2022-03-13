package com.jxntv.live.bean;

import android.view.View;

public class AudienceOperation {

    public String operation;

    public View.OnClickListener clickListener;

    public AudienceOperation() {

    }

    public AudienceOperation(String operation, View.OnClickListener clickListener) {
        this.operation = operation;
        this.clickListener = clickListener;
    }

}
