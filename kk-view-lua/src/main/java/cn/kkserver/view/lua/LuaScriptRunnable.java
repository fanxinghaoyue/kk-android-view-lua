package cn.kkserver.view.lua;

import cn.kkserver.lua.LuaState;
import cn.kkserver.view.script.IScriptFunction;
import cn.kkserver.view.script.IScriptRunnable;
import cn.kkserver.view.script.ScriptContext;
import cn.kkserver.view.script.ScriptException;

/**
 * Created by zhanghailong on 2016/11/8.
 */

public class LuaScriptRunnable implements IScriptRunnable {

    private final LuaState _luaState;

    public LuaScriptRunnable(LuaState luaState) {
        _luaState = luaState;
    }

    @Override
    public IScriptFunction compile(ScriptContext context, String code) throws Throwable {

        StringBuilder sb = new StringBuilder();

        sb.append("return function(self,...) ").append(code).append(" end");

        if(0 != _luaState.loadstring(sb.toString())) {
            String v = _luaState.tostring(-1);
            _luaState.pop(1);
            throw new ScriptException(v);
        }

        if(0 != _luaState.pcall(0,1)) {
            String v = _luaState.tostring(-1);
            _luaState.pop(1);
            throw new ScriptException(v);
        }

        if(_luaState.type(-1) == LuaState.LUA_TFUNCTION) {

            int ref = _luaState.ref();
            return new LuaScriptFunction(_luaState,ref);
        }
        else {
            _luaState.pop(1);
        }

        return null;
    }

    private static class LuaScriptFunction implements IScriptFunction {

        private final LuaState _luaState;
        private final int _ref;

        public LuaScriptFunction(LuaState luaState,int ref) {
            _luaState = luaState;
            _ref = ref;
        }

        @Override
        protected void finalize() throws Throwable {
            _luaState.unref(_ref);
            super.finalize();
        }

        @Override
        public Object call(Object object) throws Throwable {

            _luaState.getref(_ref);

            _luaState.pushValue(object);

            if(0 != _luaState.pcall(1,1)) {
                String v = _luaState.tostring(-1);
                _luaState.pop(1);
                throw new ScriptException(v);
            }

            Object v = _luaState.toValue(-1);

            _luaState.pop(1);

            return v;
        }
    }
}
