package com.yhzsk.wiki.req;

public class EbookQueryReq extends PageReq{
    private Long id;

    private String name;

    //设置两个参数   也就是可能会通过id来进行模糊搜索  或者根据name来进行模糊搜索

    private Long categoryId2;

    public Long getCategoryId2() {
        return categoryId2;
    }

    public void setCategoryId2(Long categoryId2) {
        this.categoryId2 = categoryId2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EbookQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId2=" + categoryId2 +
                "} " + super.toString();
    }
}