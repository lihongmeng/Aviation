package com.jxntv.search.template;

import androidx.databinding.ViewDataBinding;

import com.jxntv.media.template.view.MediaVideoTemplate;
import com.jxntv.search.model.ISearchModel;

/**
 * 搜索模板类接口
 */
public interface ISearchTemplate {

    /**
     * 更新数据
     *
     * @param searchModel   搜索数据模型
     */
    void update(ISearchModel searchModel);

    /**
     * 获取当前feed模型数据
     *
     * @return 当前feed模型数据
     */
    ISearchModel getSearchModel();

    /**
     * 获取当前databinding
     *
     * @return 当前databinding
     */
    ViewDataBinding getDataBinding();

}
