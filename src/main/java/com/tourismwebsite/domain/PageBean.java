package com.tourismwebsite.domain;

import java.util.List;

/**
 * @Author j
 * @Date 2019/12/11 16:27
 * @Version 1.0
 */
public class PageBean<E> {
    private Long currentPage;//当前页
    private Integer pageSize;//每页数
    private Long totalPage;//总页数
    private Long totalSize;//总条数
    private List<E> list;//每页数据集合

    public PageBean() {
    }

    public PageBean(Long currentPage, Integer pageSize, Long totalPage, Long totalSize, List<E> list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalSize = totalSize;
        this.list = list;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
        //总数据条数确定了,和每页数据条数确定了,那么总数据确定了
        totalPage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalSize=" + totalSize +
                ", list=" + list +
                '}';
    }
}
