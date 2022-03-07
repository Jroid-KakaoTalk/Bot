package jroid.kakaotalk.bot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import jroid.kakaotalk.bot.MainActivity;

public class ForcedTerminationService extends Service {
    
    private static ForcedTerminationService instance;
    public static ForcedTerminationService getInstance() {
        return instance;
    }
    
    public boolean isAlive = false;
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        this.isAlive = true;
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        this.isAlive = false;
        super.onDestroy();
    }
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        MainActivity.getInstance().endProgram();
    }
    
}
