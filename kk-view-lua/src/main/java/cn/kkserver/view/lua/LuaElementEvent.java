package cn.kkserver.view.lua;

import cn.kkserver.core.IGetter;
import cn.kkserver.core.ISetter;
import cn.kkserver.view.element.ElementEvent;

/**
 * Created by zhanghailong on 2016/11/8.
 */

public class LuaElementEvent implements IGetter,ISetter {

    public final ElementEvent event;

    public LuaElementEvent(ElementEvent event) {
        this.event = event;
    }

    @Override
    public Object get(String name) {
        if("cancelBubble".equals(name)) {
            return event.isCancelBubble();
        }
        else if("element".equals(name)) {
            return event.element;
        }
        return null;
    }

    @Override
    public void set(String name, Object value) {
        if("cancelBubble".equals(name)) {
            event.setCancelBubble(value != null && value instanceof Boolean ? (Boolean) value : false);
        }
    }
}
