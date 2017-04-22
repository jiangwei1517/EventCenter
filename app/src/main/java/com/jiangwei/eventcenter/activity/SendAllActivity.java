package com.jiangwei.eventcenter.activity;

import com.jiangwei.eventcenter.R;
import com.jiangwei.eventcenter.core.EventCenter;
import com.jiangwei.eventcenter.core.EventHandler;
import com.jiangwei.eventcenter.core.SendType;
import com.jiangwei.eventcenter.eventinterface.EventOnHahaLoad;
import com.jiangwei.eventcenter.eventinterface.EventOnLoad;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SendAllActivity extends AppCompatActivity {
    private EventHandler1 handler1;
    private EventHandler2 handler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_all_event_center);
        Button btnSend1 = (Button) findViewById(R.id.btn_send1);
        Button btnSend2 = (Button) findViewById(R.id.btn_send2);
        btnSend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnHahaLoad.class, handler1.getHandlerId(), SendType.TYPE_ALL);
            }
        });
        btnSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnHahaLoad.class, handler1.getHandlerId(), SendType.TYPE_TAIL_ALL);
            }
        });
        handler1 = new EventHandler1(this);
        handler1.register();
        handler2 = new EventHandler2(this);
        handler2.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler1.unRegister();
        handler2.unRegister();
    }

    public static class EventHandler1 extends EventHandler implements EventOnLoad, EventOnHahaLoad {

        public EventHandler1(Context context) {
            super(context);
        }

        @Override
        public void onHaha() {
            Toast.makeText(getContext(), "全局接收成功1次!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLoad(String a) {
            return false;
        }
    }

    public static class EventHandler2 extends EventHandler implements EventOnLoad, EventOnHahaLoad {

        public EventHandler2(Context context) {
            super(context);
        }

        @Override
        public void onHaha() {
            Toast.makeText(getContext(), "全局接收成功2次!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLoad(String a) {
            return false;
        }
    }
}
