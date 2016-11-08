package cn.kkserver.view.lua;

import android.util.Log;
import java.util.Map;
import java.util.TreeMap;
import cn.kkserver.core.IGetter;
import cn.kkserver.lua.LuaFunction;
import cn.kkserver.lua.LuaState;
import cn.kkserver.view.Property;
import cn.kkserver.view.element.Element;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.event.EventFunction;
import cn.kkserver.view.style.Style;

/**
 * Created by zhanghailong on 2016/11/8.
 */

public class LuaElement implements IGetter {

    private static final LuaFunction _funcGet = new LuaFunction() {
        @Override
        public int invoke(LuaState L) {

            int top = L.gettop();

            if(top > 1 && L.type( - top ) == LuaState.LUA_TOBJECT && L.type( - top + 1) == LuaState.LUA_TSTRING) {

                Object object = L.toobject(- top);

                String name = L.tostring(- top + 1);

                Object v = null;

                if(object instanceof LuaElement) {

                    Element element = ((LuaElement) object).element;

                    Property prop = Style.get(name);

                    if(prop != null) {
                        v = element.get(prop);
                    }
                }

                L.pushValue(v);

                return 1;
            }

            return 0;
        }
    };

    private static final LuaFunction _funcSet = new LuaFunction() {
        @Override
        public int invoke(LuaState L) {

            int top = L.gettop();

            if(top > 1 && L.type( - top ) == LuaState.LUA_TOBJECT && L.type( - top + 1) == LuaState.LUA_TSTRING) {

                Object object = L.toobject(- top);

                String name = L.tostring(- top + 1);

                Object v = top > 2 ? L.toValue( - top +2) : null;

                if(object instanceof LuaElement) {

                    Element element = ((LuaElement) object).element;

                    Property prop = Style.get(name);

                    if(prop != null) {
                        if(v == null) {
                            element.removeProperty(prop);
                        }
                        else {
                            element.set(prop, v);
                        }
                    }
                }
            }

            return 0;
        }
    };



    private static class LuaListener implements EventFunction<Object> {

        private final LuaState _luaState;
        private final int _ref;

        public LuaListener(LuaState luaState,int ref) {
            _luaState = luaState;
            _ref = ref;
        }

        @Override
        protected void finalize() throws Throwable {
            _luaState.unref(_ref);
            super.finalize();
        }

        @Override
        public void onEvent(String name, Event event, Object weakObject) {

            int top = _luaState.gettop();

            _luaState.getref(_ref);

            if(_luaState.type( - 1) == LuaState.LUA_TFUNCTION) {

                _luaState.pushValue(name);
                _luaState.pushValue(event);
                _luaState.pushValue(weakObject);

                if(0 != _luaState.pcall(3,0)) {
                    Log.d(Loader.Tag,_luaState.tostring( -1));
                }

            }

            _luaState.pop(_luaState.gettop() - top);
        }
    }

    private static final LuaFunction _funcOn = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 2 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TSTRING
                    && luaState.type( - top + 2) == LuaState.LUA_TFUNCTION) {

                Object object = luaState.toobject(- top);

                String name = luaState.tostring( - top + 1);

                Object weakObject = top > 3 ? luaState.toValue(- top +3) : null;

                if(object instanceof LuaElement) {

                    Element element = ((LuaElement) object).element;

                    luaState.pushvalue( - top + 2);
                    int ref = luaState.ref();

                    element.on(name,new LuaListener(luaState,ref),weakObject);

                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcOff = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 0 && luaState.type( - top ) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);

                String name = top > 1 && luaState.type(- top +1) == LuaState.LUA_TSTRING ? luaState.tostring( - top + 1) : null;

                Object weakObject = top > 2 ? luaState.toValue(- top +2) : null;

                if(object instanceof LuaElement) {

                    Element element = ((LuaElement) object).element;

                    element.off(name,null,weakObject);

                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcAppend = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.append(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcAppendTo = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.appendTo(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcBefore = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.before(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcBeforeTo = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.beforeTo(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcAfter = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.after(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcAfterTo = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 1 && luaState.type( - top ) == LuaState.LUA_TOBJECT
                    && luaState.type( - top + 1) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);
                Object element = luaState.toobject(- top +1);

                if(object instanceof LuaElement && element instanceof LuaElement) {
                    ((LuaElement) object).element.afterTo(((LuaElement) element).element);
                }

            }

            return 0;
        }
    };

    private static final LuaFunction _funcRemove = new LuaFunction() {

        @Override
        public int invoke(LuaState luaState) {

            int top = luaState.gettop();

            if(top > 0 && luaState.type( - top ) == LuaState.LUA_TOBJECT) {

                Object object = luaState.toobject(- top);

                if(object instanceof LuaElement ) {
                    ((LuaElement) object).element.remove();
                }

            }

            return 0;
        }
    };

    private final static Map<String,LuaFunction> _funcs = new TreeMap<>();

    static {
        _funcs.put("get",_funcGet);
        _funcs.put("set",_funcSet);
        _funcs.put("on",_funcOn);
        _funcs.put("off",_funcOff);
        _funcs.put("append",_funcAppend);
        _funcs.put("appendTo",_funcAppendTo);
        _funcs.put("before",_funcBefore);
        _funcs.put("beforeTo",_funcBeforeTo);
        _funcs.put("after",_funcAfter);
        _funcs.put("afterTo",_funcAfterTo);
        _funcs.put("remove",_funcRemove);
    }

    public final Element element;

    public LuaElement(Element element) {
        this.element = element;
    }

    @Override
    public Object get(String name) {
        if(_funcs.containsKey(name)) {
            return _funcs.get(name);
        }
        if("parent".equals(name)) {
            return element.parent();
        }
        if("firstChild".equals(name)) {
            return element.firstChild();
        }
        if("lastChild".equals(name)) {
            return element.lastChild();
        }
        if("nextSibling".equals(name)) {
            return element.nextSibling();
        }
        if("prevSibling".equals(name)) {
            return element.prevSibling();
        }
        return null;
    }

}
