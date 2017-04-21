package com.jiangwei.eventcenter.activity;

import com.eventcenter.luffy.eventcenter.R;
import com.jiangwei.eventcenter.core.EventCenter;
import com.jiangwei.eventcenter.core.EventHandler;
import com.jiangwei.eventcenter.core.SendType;
import com.jiangwei.eventcenter.eventinterface.EventOnLoad;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SendOneActivity extends AppCompatActivity {
    private EventHandler1 handler1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_one_event_center);
        Button btnSend1 = (Button) findViewById(R.id.btn_send1);
        Button btnSend2 = (Button) findViewById(R.id.btn_send2);
        btnSend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnLoad.class, handler1.getHandlerId(), SendType.TYPE_ONE,
                        "我是先注册的handler1");
            }
        });
        btnSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnLoad.class, handler1.getHandlerId(), SendType.TYPE_ONE,
                        "我是后注册的handler2");
            }
        });
        handler1 = new EventHandler1(this);
        handler1.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler1.unRegister();
    }

    public static class EventHandler1 extends EventHandler implements EventOnLoad {

        public EventHandler1(Context context) {
            super(context);
        }

        @Override
        public boolean onLoad(String a) {
            Toast.makeText(getContext(), "定向接收成功!" + a, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
