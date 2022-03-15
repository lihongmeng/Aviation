package com.hzlz.aviation.kernel.base.utils;

import com.hzlz.aviation.library.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数组分页工具
 */
public class ListPageUtil<T> {

    /**
     * 当前页数
     */
    private int pageNumber = 0;

    /**
     * 每一页的数据数量
     */
    private int pageSize = 10;

    /**
     * 分页后的数据集合
     */
    private HashMap<Integer, List<T>> map = new HashMap<>();

    public void updateDataSource(List<T> dataSource) {
        map.clear();
        if (dataSource == null || dataSource.isEmpty()) {
            pageNumber = 0;
            return;
        }
        int size = dataSource.size();
        int maxPage = size / pageSize;

        // 先处理只有一页的情况
        if (maxPage == 0) {
            pageNumber = 0;
            map.put(0, dataSource.subList(0, size));
            return;
        }

        // 判断是否是整数，因为会影响pageNumber的取值范围
        // 如果是整数，pageNumber -> [0,maxPage)
        // 如果不是整数，pageNumber -> [0,maxPage]
        boolean isSizeMulti = (size % pageSize == 0);

        if (isSizeMulti) {
            if (pageNumber > maxPage - 1) {
                pageNumber = maxPage - 1;
            }
            for (int index = 0; index < maxPage; index++) {
                map.put(index, dataSource.subList(index * pageSize, (index + 1) * pageSize));
            }
            return;
        }
        if (pageNumber > maxPage) {
            pageNumber = maxPage;
        }
        for (int index = 0; index < maxPage + 1; index++) {
            if (index == maxPage) {
                map.put(index, dataSource.subList(index * pageSize, size));
            } else {
                map.put(index, dataSource.subList(index * pageSize, (index + 1) * pageSize));
            }
        }
    }

    public void updatePageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<T> getCurrentPageData() {
        return map.get(pageNumber);
    }

    public List<T> getNextPageAndAdd() {
        int size = map.size();
        if (pageNumber == size - 1) {
            return null;
        }
        pageNumber++;
        return getCurrentPageData();
    }

    public List<T> getNextPage() {
        int size = map.size();
        if (pageNumber == size - 1) {
            return null;
        }
        return getCurrentPageData();
    }

    public boolean hasNextPage() {
        List<T> nextPageList = getNextPage();
        return nextPageList != null && !nextPageList.isEmpty();
    }

    public void updatePageSize(int pageSize) {
        this.pageSize = pageSize;

        if (map.isEmpty()) {
            return;
        }

        // 将map拆了重新组装
        List<T> result = new ArrayList<>();
        for (List<T> list : map.values()) {
            result.addAll(list);
        }
        updateDataSource(result);
    }

    public void printMapData() {
        for (List<T> list : map.values()) {
            LogUtils.d("=================================");
            for (T t : list) {
                LogUtils.d("" + t);
            }
            LogUtils.d("=================================");
        }
    }

}
