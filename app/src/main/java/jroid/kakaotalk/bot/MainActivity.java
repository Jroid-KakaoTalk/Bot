package jroid.kakaotalk.bot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import jroid.kakaotalk.bot.service.ForcedTerminationService;
import jroid.kakaotalk.bot.service.NotificationService;

public class MainActivity extends AppCompatActivity {
    
    private static MainActivity instance;
    
    public static MainActivity getInstance() {
        return instance;
    }
    
    public static boolean isOn = false;
    private SwitchCompat switchBtn;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        
        this.setContentView(R.layout.activity_main);
        
        this.switchBtn = findViewById(R.id.switchBtn);
        this.switchBtn.setChecked(isOn);
    
        this.startService(new Intent(this, ForcedTerminationService.class));
        this.startService(new Intent(this, NotificationService.class));
    }
    
    public void onSwitchClick(View view) {
        isOn = this.switchBtn.isChecked();
        
        if(isOn) {
            this.toast("스위치가 켜졌습니다");
        } else {
            this.toast("스위치가 꺼졌습니다");
        }
    }
    
    public void onCheckClick(View view) {
        boolean isForcedTerminationServiceOn = ForcedTerminationService.getInstance().isAlive;
        boolean isNotificationServiceOn = NotificationService.getInstance().isAlive;
        
        if(isForcedTerminationServiceOn && isNotificationServiceOn) {
            toast("모든 서비스가 정상적으로 작동중입니다");
        } else {
            if(!isForcedTerminationServiceOn) {
                this.startService(new Intent(this, ForcedTerminationService.class));
                toast("ForcedTerminationService 를 활성화했습니다");
            }
    
            if(!isNotificationServiceOn) {
                this.startService(new Intent(this, NotificationService.class));
                toast("NotificationService 를 활성화했습니다");
            }
        }
    }
    
    public void toast(final String msg) {
        if(instance != null) {
            instance.runOnUiThread(() -> {
                Toast toast = Toast.makeText(instance, msg, Toast.LENGTH_SHORT);
                toast.show();
            });
        }
    }
    
    @Override
    public void onBackPressed() {
        this.endProgram();
        this.finish();
    }
    
    public void endProgram() {
        this.toast("프로그램을 종료합니다");
    
        ForcedTerminationService.getInstance().stopSelf();
        NotificationService.getInstance().stopSelf();
    }

}
