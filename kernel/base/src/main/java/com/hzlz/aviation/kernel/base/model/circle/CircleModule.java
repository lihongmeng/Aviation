package com.hzlz.aviation.kernel.base.model.circle;

public class CircleModule {

    public String guideText;

    private long id;

    private String name;
    //组件类型:1-话题合集、2-投诉爆料、3-问答、4-广播、5-整期
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public boolean isTvProgram(){
        return type == 5;
    }

    public void setType(int type) {
        this.type = type;
    }
}
