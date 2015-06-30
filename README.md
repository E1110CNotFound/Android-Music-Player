Android-Music-Player
========

v1.0
--------
1.	使用fragment进行布局，分别是MusicBrowserFragment（呈现音乐列表）和PlaybackControlFragment（呈现控制界面）组成，在fragment中处理相应的事件

2.	fragment与托管fragment的activity之间主要采用两种通信方式，回调接口和广播，fragment中的事件通过回调函数传递给activity来进行相应操作，activity中的事件则通过广播通知fragment

3.	音乐播放类MediaPlayer使用service进行封装，service和activity之间采用广播和binder机制，activity通过binder机制获取service的实例并调用相应的方法，service中的事件通过广播通知相应的activity