package com.mandala.mq.produce.util;

import java.util.Iterator;
import java.util.List;

/**
 * @Author songzhenliang
 * @Date 2023-06-08 14:18
 */
public class ListSplitter<T> implements Iterator<List<T>> {

    /**
     * 集合内容
     */
    private final List<T> ts;

    /**
     * 数据分割长度
     */
    private int size;

    /**
     * 分割索尼
     */
    private int currIndex;

    public ListSplitter(List<T> ts, int size) {
        this.ts = ts;
        this.size = size;
    }


    @Override
    public boolean hasNext() {
        return currIndex < this.size;
    }

    @Override
    public List<T> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < ts.size(); nextIndex ++){
            T t = ts.get(nextIndex);
            totalSize = totalSize + t.toString().length();
            if(totalSize > size){
                break;
            }
        }
        List<T> subList = ts.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }
}
