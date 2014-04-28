package com.mulberry.toolkit.collect;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 1/16/14
 *         Time: 9:58 PM
 * 本应继承List，对于Collection集合的子类，Hessian会使用CollectionSerializer处理，在序列化的过程中会调用默认的构造函数，
 * 创建一个新的List，然后往List中一个一个添加元素，这不是问题，关键是居然会忽略List之外的数据，比如分页相关的数据，这肯定是
 * 不对的，当然可以扩展SerializerFactory，但是这样需要反射注入_staticSerializerMap，相当不便。而且在我们的应用中，
 * records一旦设定，就不再希望调用方更新集合本身，所以决定继承Iterable接口，并去除所有可以更改集合的所有方法。
 *
 */
public class PaginatedList<T extends Serializable> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = -7663937119013112333L;

    private final List<T>     records;
    private int pageSize;
    private int totalCount;
    private int pageIndex;

    public PaginatedList(){
        this.records = new ArrayList<T>();//Collections.emptyList();
    }

    public PaginatedList(List<T> records) {
        this.records = records;
    }

    public PaginatedList(List<T> records, int pageSize) {
        this(records);
        this.pageSize = pageSize;
    }

    public PaginatedList(List<T> records, int pageSize, int totalCount) {
        this(records, pageSize);
        this.totalCount = totalCount;
    }

    public PaginatedList(List<T> records, int pageSize, int totalCount, int pageIndex) {
        this(records, pageSize, totalCount);
        this.pageIndex = pageIndex;
    }

    public List<T> getRecords() {
        return records;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int size() {
        return records.size();
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public boolean contains(Object o) {
        return records.contains(o);
    }

    public Iterator<T> iterator() {
        return records.iterator();
    }

    public Object[] toArray() {
        return records.toArray();
    }

    public <E> E[] toArray(E[] ts) {
        return records.toArray(ts);
    }

    public boolean containsAll(Collection<?> objects) {
        return records.containsAll(objects);
    }

    public T get(int i) {
        return records.get(i);
    }

    public int indexOf(Object o) {
        return records.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return records.lastIndexOf(o);
    }

    public List<T> subList(int i, int i2) {
        return records.subList(i, i2);
    }

}
