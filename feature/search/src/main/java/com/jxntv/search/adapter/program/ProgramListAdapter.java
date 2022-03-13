package com.jxntv.search.adapter.program;

import android.content.Context;
import android.view.ViewGroup;

import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;
import com.jxntv.search.databinding.LayoutProgramListBinding;
import com.jxntv.search.databinding.SearchResultDataBinding;
import com.jxntv.search.model.SearchDetailModel;

/**
 * @author huangwei
 * date : 2021/10/25
 * desc : 节目列表
 **/
public class ProgramListAdapter extends BaseRecyclerAdapter<SearchDetailModel.ProgramModel, BaseRecyclerViewHolder> {

    private SearchDetailModel searchDetailModel;

    public void setSearchDetailModel(SearchDetailModel searchDetailModel){
        this.searchDetailModel = searchDetailModel;
    }

    public ProgramListAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder<>(LayoutProgramListBinding.inflate(mInflater,
                parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        if (holder.getBinding() instanceof  LayoutProgramListBinding){
            LayoutProgramListBinding binding = (LayoutProgramListBinding) holder.getBinding();
            SearchDetailModel.ProgramModel model = mList.get(position);
            binding.title.setText(String.format("%s %s", model.playDate, model.programName));

            SearchResultDataBinding dataBinding = new SearchResultDataBinding();

            binding.title.setOnClickListener(view -> {
                PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(
                        binding.title.getContext(), Long.parseLong(model.columnId),Long.parseLong(model.id), "搜索-"+searchDetailModel.getSearchTypeString());
                dataBinding.clickCommon(searchDetailModel);
            });
        }


    }
}
