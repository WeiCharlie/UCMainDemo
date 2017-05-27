## 仿UC首页下拉和上拉效果
 - 看了一些网上关于Behavior的使用，借鉴了一下。
 - 然后参考AppBarLayout的源码，Copy了design包中的几个类源码，采用自定义View+注解声明Behavior的方式。
 - 下拉刷新拷贝了SwipeRefreshLayout的源码进行了修改。
 - 支持上滑重合进入新闻列表，再下拉一点刷新，下拉到底回到首页，首页下拉到底进入新闻列表
 
### 以下是效果图
![效果图](ucmaindemo.gif)
