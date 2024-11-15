package es.zed.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public class RespModel<T> {

  private T data;

  private String message;

  public T getData() {
    return data;
  }

  public String getMessage() {
    return message;
  }
}
