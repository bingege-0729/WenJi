package com.example.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一响应结果
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;//状态码
    private String msg;//提示信息
    private T data;// 数据

    public static <E> Result<E> success(E data){
    	return new Result<E>("200","操作成功",data);
    }

    public static Result success(){
        return new Result("200","操作成功",null);
    }

    public static Result error(String msg){
        return new Result("500",msg,null);
    }
}
