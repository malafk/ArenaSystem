package lol.maltest.arenasystem.redis;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RedisMessage
{

    private MessageAction action;
    private Map<String, String> params;

    public RedisMessage(MessageAction action)
    {
        this.action = action;
        params = new HashMap<>();
    }

    public RedisMessage setParam(String key, String value)
    {
        params.put(key, value);
        return this;
    }

    public String getParam(String key)
    {
        if (containsParam(key))
        {
            return params.get(key);
        }
        return null;
    }

    public boolean containsParam(String key)
    {
        return params.containsKey(key);
    }

    public void removeParam(String key)
    {
        if (containsParam(key))
        {
            params.remove(key);
        }
    }

    public String toJSON()
    {
        return new Gson().toJson(this);
    }

    public MessageAction getAction()
    {
        return action;
    }
}
