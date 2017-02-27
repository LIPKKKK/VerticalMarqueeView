VerticalMarqueeView

maven

```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
Add the dependency

```
    dependencies {
            compile 'com.github.LIPKKKK:VerticalMarqueeView:v1.0.3'
      }
```
  ##how to use

```
<com.lip.verticalmarqueeviewdemo.view.VerticalMarqueeView
    android:id="@+id/lip_VerticalView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="6"
    app:VerticalMarqueeTextView_textColor = "#000"
    app:VerticalMarqueeTextView_textSize = "20sp"
    app:VerticalMarqueeTextView_isRepeat = "true"
    app:VerticalMarqueeTextView_startPoint = "0"
    app:VerticalMarqueeTextView_direction = "1"
    app:VerticalMarqueeTextView_speed = "20"
    />
```
*VerticalMarqueeTextView_textColor : 文字颜色

*VerticalMarqueeTextView_textSize ： 文字大小

*VerticalMarqueeTextView_isRepeat ： 是否重复

*VerticalMarqueeTextView_startPoint ： 开始位置

*VerticalMarqueeTextView_direction ： 滚动方向

*VerticalMarqueeTextView_speed : 滚动速度

   handler.sendEmptyMessageDelayed(START_ROLL, 100);

```
   /**
    * 开始播放
    */
    private void startRoll() {
        Log.i("滚动事件", "startRoll");
        if (strings.length - 1 > count++) {
            lipVerticalView.setText(strings[count]);
            lipVerticalView.startScroll(true, 5);
        } else {
            count = 0;
            lipVerticalView.setText(strings[count]);
            lipVerticalView.startScroll(true, 5);
        }
    }

   /**
     * 停止播放
     */
    private void playNext() {
        lipVerticalView.setText("");
        lipVerticalView.stopScroll();
        startRoll();
    }

    /**
     * 暂停播放
     */
    private void pauseRoll() {
        lipVerticalView.pauseScroll();
    }

    /**
     * 恢复播放
     */
    private void restartRoll() {
        lipVerticalView.restartRoll();
    }
```

