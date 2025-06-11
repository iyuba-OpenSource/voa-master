package com.iyuba.voa.data.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@Root(name = "response")
public class XmlResponse {
    @Element(name = "msg", required = false)
    private String msg;
    @Element(name = "result", required = false)
    private String result;
    @Element(name = "type", required = false)
    private String type;
    @Element(name = "topic", required = false)
    private String topic;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
