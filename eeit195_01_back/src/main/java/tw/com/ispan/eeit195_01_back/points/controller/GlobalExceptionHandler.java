package tw.com.ispan.eeit195_01_back.points.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import tw.com.ispan.eeit195_01_back.points.bean.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 當捕獲到 IllegalArgumentException 時，返回錯誤訊息
        ErrorResponse errorResponse = new ErrorResponse( HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
