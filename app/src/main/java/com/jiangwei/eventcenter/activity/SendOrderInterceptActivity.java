package com.jiangwei.eventcenter.activity;

import com.jiangwei.eventcenter.R;
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

public class SendOrderInterceptActivity extends AppCompatActivity {
    private EventHandler1 handler1;
    private EventHandler2 handler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_order_intercept_event_center);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnLoad.class, handler1.getHandlerId(), SendType.TYPE_ALL,
                        "我是handler1");
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

    public static class EventHandler1 extends EventHandler implements EventOnLoad {

        public EventHandler1(Context context) {
            super(context);
        }

        @Override
        public boolean onLoad(String a) {
            Toast.makeText(getContext(), "Handler2被Handler1拦截了" + a, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static class EventHandler2 extends EventHandler implements EventOnLoad {

        public EventHandler2(Context context) {
            super(context);
        }

        @Override
        public boolean onLoad(String a) {
            Toast.makeText(getContext(), "Handler2被Handler1拦截了" + a, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
