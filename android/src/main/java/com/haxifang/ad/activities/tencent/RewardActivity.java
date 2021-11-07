package com.haxifang.ad.activities.tencent;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static com.haxifang.ad.RewardVideo.sendEvent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import com.haxifang.ad.AdBoss;
import com.haxifang.ad.RewardVideo;
import com.haxifang.ad.utils.TToast;
import com.haxifang.R;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.util.Map;

/**
* 腾讯激励视频广告
*/

public class RewardActivity extends Activity {

   static String TAG = "RewardVideo";

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

      //  WindowManager.LayoutParams windowLP = getWindow().getAttributes();
      //  windowLP.alpha = 0.0f;
      //  getWindow().setAttributes(windowLP);

       setContentView(R.layout.video_view);

       // 给rn回调用
       AdBoss.rewardActivity = this;
       AdBoss.resetRewardResult();
       loadAd();
   }

   public void loadAd() {
       final Activity _this = this;
    //    TToast.show(_this, "codeid " + AdBoss.codeid_reward_video_tencent);
       AdBoss.txRewardAd = new RewardVideoAD(this, AdBoss.tx_appid, AdBoss.codeid_reward_video_tencent,
               new RewardVideoADListener() {
                   @Override
                   public void onADLoad() {
                       String msg = "腾讯激励视频加载 onADLoad eCPM = " + AdBoss.txRewardAd.getECPM() + " , eCPMLevel = "
                               + AdBoss.txRewardAd.getECPMLevel();
                       Log.v(TAG, msg);
                       fireEvent("onAdLoaded", 200, "视频广告的素材加载完毕");
                       showAd();
                   }

                   @Override
                   public void onVideoCached() {
                       // 视频素材缓存成功，可在此回调后进行广告展示
                       String msg = "onVideoCached: 视频素材缓存成功";
                       Log.d(TAG, msg);
                       fireEvent("onAdVideoCached", 201, "视频素材缓存成功");
                   }

                   @Override
                   public void onADShow() {
                       // 激励视频广告页面开始展示
                       String msg = "onADShow: 激励视频广告页面开始展示";
                       Log.d(TAG, msg);
                       AdBoss.is_show = true;
                       fireEvent("onAdLoaded", 202, msg);
                   }

                   @Override
                   public void onADExpose() {
                       // 激励视频广告曝光
                       String msg = "onADExpose: 激励视频广告曝光 ";
                       Log.d(TAG, msg);
                       AdBoss.is_show = true;
                   }

                   @Override
                   public void onReward(Map<String, Object> map) {
                       // 激励视频触发激励（观看视频大于一定时长或者视频播放完毕）
                       AdBoss.is_reward = true;
                       String msg = "onReward: 激励视频触发激励";
                       Log.d(TAG, msg);
                   }

                   @Override
                   public void onADClick() {
                       // 激励视频广告被点击
                       AdBoss.is_click = true;
                       Log.d(TAG, "onADClick: ");
                       fireEvent("onAdClick", 203, "onADClick");
                   }

                   @Override
                   public void onVideoComplete() {
                       // 激励视频播放完毕
                       AdBoss.is_show = true;
                       String msg = "腾讯激励视频播放完毕";
                       Log.d(TAG, msg);
                       fireEvent("onVideoComplete", 205, msg);
                   }

                   @Override
                   public void onADClose() {
                       // 激励视频广告被关闭
                       AdBoss.is_close = true;
                       Log.d(TAG, "onADClose: ");
                       fireEvent("onAdClose", 204, "关闭激励视频");
                       AdBoss.getRewardResult();
                       if (AdBoss.rewardActivity != null) {
                           AdBoss.rewardActivity.finish();
                       }
                   }

                   @Override
                   public void onError(AdError adError) {
                       String message = adError.getErrorCode() + " 加载腾讯激励视频失败:" + adError.getErrorMsg();
                       Log.e(TAG, message);
                       fireEvent("onAdError", 1004, message);
                       AdBoss.rewardPromise.reject("{\"ad_error_msg\": \"" + adError.getErrorMsg() + "\"}");
                       if (AdBoss.rewardActivity != null) {
                           AdBoss.rewardActivity.finish();
                       }
                    //    _this.runOnUiThread(() -> {
                    //        TToast.show(_this, message);
                    //        String msg = "切头条激励视频...";
                    //        Log.d(TAG, "onError: " + msg);
                    //        RewardVideo.startTT(AdBoss.codeid_reward_video);
                    //    });
                   }
               });

       AdBoss.txRewardAd.loadAD();
   }

   public void showAd() {
       // 展示广告
       if (AdBoss.txRewardAd != null) {
           // 广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
           if (!AdBoss.txRewardAd.hasShown()) {
               // 广告展示检查2：当前广告数据还没有展示过
               AdBoss.txRewardAd.showAD();
           } else {
               String msg = "onVideoCached: 此条广告已经展示过，请再次请求广告后进行广告展示！";
               Log.d(TAG, msg);
               TToast.show(this, msg);
               finish();
           }
       } else {
           String msg = "onVideoCached: 成功加载广告后再进行广告展示！";
           Log.d(TAG, msg);
           TToast.show(this, msg);
           finish();
       }
   }
// 二次封装发送到RN的事件函数
  public static void fireEvent(
    String eventName,
    int startCode,
    String message
  ) {
    WritableMap params = Arguments.createMap();
    params.putInt("code", startCode);
    params.putString("message", message);
    sendEvent(eventName, params);
  }
}
