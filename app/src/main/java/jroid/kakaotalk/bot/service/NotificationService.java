package jroid.kakaotalk.bot.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import jroid.kakaotalk.bot.MainActivity;
import jroid.kakaotalk.bot.SystemConfig;
import jroid.kakaotalk.bot.dto.NotificationDto;

public class NotificationService extends NotificationListenerService {
    
    private static NotificationService instance;
    public static NotificationService getInstance() {
        return instance;
    }
    
    public boolean isAlive = false;
    private Context context;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        this.context = getApplicationContext();
        this.isAlive = true;
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        this.isAlive = false;
        super.onDestroy();
    }
    
    @Override
    public void onListenerConnected() {
        MainActivity.getInstance().toast("알림 감지를 시작합니다");
        super.onListenerConnected();
    }
    
    @Override
    public void onListenerDisconnected() {
        MainActivity.getInstance().toast("알림 감지가 중지되었습니다");
        super.onListenerDisconnected();
        
        this.stopSelf();
        startService(new Intent(MainActivity.getInstance(), NotificationService.class));
    }
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        MainActivity.getInstance().endProgram();
    }
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    
        if(!MainActivity.isOn) {
            return;
        }
    
        if (sbn.getPackageName().equals("com.kakao.talk")) {
            try {
                Notification.WearableExtender extender = new Notification.WearableExtender(sbn.getNotification());
            
                for (Notification.Action action : extender.getActions()) {
                    if (
                        action.getRemoteInputs() != null && action.getRemoteInputs().length > 0 &&
                        action.title.toString().toLowerCase().contains("reply") ||
                        action.title.toString().toLowerCase().contains("답장")
                    ) {
                        Bundle data = sbn.getNotification().extras;
                    
                        String room, sender, message, image;
                        Bitmap bitmap;
                        boolean isGroupChat = false;
                    
                        room = data.getString("android.summaryText");
                        sender = Objects.requireNonNull(data.getString("android.title"));
                        message = String.valueOf(data.get("android.text"));
                    
                        if(sbn.getNotification().getLargeIcon() == null) {
                            return;
                        }
                    
                        bitmap = ((BitmapDrawable) sbn.getNotification().getLargeIcon().loadDrawable(context)).getBitmap();
                    
                        if(room == null) {
                            room = sender;
                        } else {
                            isGroupChat = true;
                        }
                    
                        if (message.equals("null")) {
                            message = "";
                        }
                    
                        int length = message.length();
                        if(length > 100) {
                            return;
                        }
                    
                        if(bitmap == null) {
                            image = "이미지를 가져올 수 없습니다";
                        } else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        
                            image = Base64.encodeToString(baos.toByteArray(), 0);
                            int imageLength = image.length();
                            image = image.substring(200, 255);
                            image = SystemConfig.getRegex(image, "-");
                            image = image + "+" + image.hashCode() + "+" + imageLength;
                        }
    
                        NotificationDto notificationDto = NotificationDto.builder()
                            .room(room)
                            .sender(sender)
                            .message(message)
                            .image(image)
                            .isGroupChat(isGroupChat)
                            .build();
                        Log.i("onNotificationPosted", notificationDto.toString());
                    }
                }
            } catch(Exception e) {
                Log.e("KakaoTalkListener", "An Error occurred while parsing message", e);
            }
        }
    }
    
}
