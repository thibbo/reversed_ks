package fr.vigicorp.tlelievre.application_test;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JsonUTF8Request extends JsonRequest<JSONObject>
{
    protected static final String TYPE_UTF8_CHARSET = "charset=UTF-8";

    public JsonUTF8Request(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse paramNetworkResponse)
    {
        try
        {
            Response localResponse = Response.success(new JSONObject(new String(paramNetworkResponse.data, "GBK")), HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
            return localResponse;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
            return Response.error(new ParseError(localUnsupportedEncodingException));
        }
        catch (JSONException localJSONException)
        {
            return Response.error(new ParseError(localJSONException));
        }
    }
}
