package me.goldze.mvvmhabit.http;

import com.google.gson.annotations.SerializedName;

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class BaseResponse<T> {
    private int status;
    public String result;
    private String message;
    @SerializedName(value = "total", alternate = {"counts", "totalPage", "mytotal"})
    private int total;
    @SerializedName(value = "data", alternate = {"voatext", "voaexam"})
    private T data;

    public int getTotal() {
        return total;
    }

    public String getResult() {
        return result;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return status == 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
