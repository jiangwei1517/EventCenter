package activity;

import com.eventcenter.luffy.eventcenter.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import core.EventCenter;
import core.EventHandler;
import core.SendType;
import eventinterface.EventOnHahaLoad;
import eventinterface.EventOnLoad;

public class TestEventCenterActivity extends AppCompatActivity {
    private EventHandler1 handler1;
    private EventHandler2 handler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_event_center);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCenter.getInstance().send(EventOnHahaLoad.class, handler2.getHandlerId(), SendType.TYPE_ONE);
                // EventCenter.getInstance().send(EventOnHahaLoad.class, handler1.getHandlerId(), SendType.TYPE_ONE);
            }
        });
        handler1 = new EventHandler1(this);
        handler2 = new EventHandler2(this);
        handler1.register();
        handler2.register();
    }

    public static class EventHandler1 extends EventHandler implements EventOnLoad, EventOnHahaLoad {

        public EventHandler1(Context context) {
            super(context);
        }

        @Override
        public boolean onLoad(String a) {
            System.out.println("1");
            // 不拦截
            return false;
        }

        @Override
        public void onHaha() {
            System.out.println("ddddd");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler1.unRegister();
    }

    public static class EventHandler2 extends EventHandler1 implements EventOnLoad, EventOnHahaLoad {

        public EventHandler2(Context context) {
            super(context);
        }

        @Override
        public void onHaha() {
            System.out.println("ddddd");
        }
    }
}
