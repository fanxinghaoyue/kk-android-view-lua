package cn.kkserver.view.lua;

import cn.kkserver.lua.IObjectReflect;
import cn.kkserver.lua.LuaState;
import cn.kkserver.view.element.Element;
import cn.kkserver.view.element.ElementEvent;

/**
 * Created by zhanghailong on 2016/11/8.
 */

public class Loader {

    public final static String Tag = "kk";

    static {

        LuaState.addObjectReflect(new IObjectReflect() {

            @Override
            public boolean canReflectToJavaObject(Object object) {
                return object != null && (object instanceof LuaElement || object instanceof LuaElementEvent);
            }

            @Override
            public boolean canReflectToLuaObject(Object object) {
                return object != null && (object instanceof Element || object instanceof ElementEvent);
            }

            @Override
            public Object reflectToJavaObject(Object object) {
                if(object != null ) {
                    if(object instanceof LuaElement) {
                        return ((LuaElement) object).element;
                    }
                    if(object instanceof LuaElementEvent) {
                        return ((LuaElementEvent) object).event;
                    }
                }
                return object;
            }

            @Override
            public Object reflectToLuaObject(Object object) {
                if(object != null) {
                    if(object instanceof LuaElement) {
                        return object;
                    }
                    if(object instanceof LuaElementEvent) {
                        return object;
                    }
                    if(object instanceof Element) {
                        return new LuaElement((Element) object);
                    }
                    if(object instanceof ElementEvent) {
                        return new LuaElementEvent((ElementEvent) object);
                    }
                }
                return object;
            }
        });
    }

}
