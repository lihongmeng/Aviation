package com.hzlz.aviation.feature.search.databinding;

import android.text.TextUtils;

import androidx.databinding.ObservableField;

/**
 * 搜索bar数据绑定模型
 */
public class SearchBarDataBinding {

    /** 搜索词 */
    public ObservableField<String> searchWord = new ObservableField<>();
    /** hint搜索词 */
    public ObservableField<String> hintSearchWord = new ObservableField<>();
    /** 搜索按钮显示的状态 */
    public ObservableField<Boolean> searchBtnState = new ObservableField<>();

    /**
     * 构造函数
     */
    public SearchBarDataBinding() {
    }

    /**
     * 更新本地搜索词
     *
     * @param searchWord 搜索词
     */
    public void updateSearchWord(String searchWord) {
        this.searchWord.set(searchWord);
    }

    /**
     * 更新本地hint搜索词
     *
     * @param hintSearchWord 搜索词
     */
    public void updateHintSearchWord(String hintSearchWord) {
        this.hintSearchWord.set(hintSearchWord);
    }

    /**
     * 更新搜索按钮显示的状态
     */
    public void updateSearchBtnState(boolean state) {
        String hintSearchWord = this.hintSearchWord.get();
        if (!TextUtils.isEmpty(hintSearchWord)) {
            this.searchBtnState.set(true);
        } else {
            this.searchBtnState.set(state);
        }
    }
}
