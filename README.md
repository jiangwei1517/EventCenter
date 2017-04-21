# <a name="fenced-code-block">EventCenter</a>

![MacDown logo](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492799239489&di=3683731794f7c92b0e10b75a4746df26&imgtype=0&src=http%3A%2F%2Fimg27.51tietu.net%2Fpic%2F2017-011500%2F20170115001256mo4qcbhixee164299.jpg)
## <a name="fenced-code-block">解决问题</a>
* 消息的全局分发
* 消息的定向分发
* 消息拦截分发
* 消息正序，倒序分发

## <a name="fenced-code-block">基本思想</a>
* 基于Handler实现
* 消息接收器的注册与解除注册
* 接口回调方法
* 反射

## <a name="fenced-code-block">使用方法</a>
### <a name="fenced-code-block">handler的接收方法：</a>
Handler的基本用法，所实现的Handler都要直接或间接的继承EventHandler，并且实现接收的接口，便于回调：

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
handler所实现的接口都要继承于Event这个标签：

	public interface EventOnLoad extends Event{

    public boolean onLoad(String a);
	}
handler的注册与解除注册，其中注册分先后顺序，先注册先接收。

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
     
### <a name="fenced-code-block">发送方法参数： </a>
  `event:接收器所实现的接口的Class对象`

  `targetHandlerId:目标接收器的id唯一标识，仅对指定分发此参数有效,通过handler.getHandlerId()获取。`

  `sendType:发送方式，TYPE_ALL正序全局发送、TYPE_ONE定向正序发送、`
  `TYPE_TAIL_ALL倒序全局发送、TYPE_TAIL_ONE倒序定向发送`
  `TYPE_BUT_ONE正向除了指定ID发送、TYPE_TAIL_BUT_ONE倒序除了指定ID发送。`
   
 	public void send(@NonNull Class<? extends Event> event, int targetHandlerId, int sendType, Object...objs) {
 	 ...
  	}
  	
## <a name="fenced-code-block">例子</a>

### <a name="fenced-code-block">定向全局发送消息</a>

	package com.jiangwei.eventcenter.activity;
	
	import com.eventcenter.luffy.eventcenter.R;
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
























