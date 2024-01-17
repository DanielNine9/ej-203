package huydqpc07859.ecommerce.advices;

import huydqpc07859.ecommerce.models.dto.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> ReponseRuntimeException(MethodArgumentNotValidException ex) {
        StringBuilder stringBuilder = new StringBuilder();
        for(ObjectError error : ex.getBindingResult().getAllErrors()){
            stringBuilder.append(error.getDefaultMessage());
            stringBuilder.append(", ");
        }
    return ResponseEntity.badRequest().body(new CommonResponse("failed", stringBuilder.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> ResponseException(Exception ex){
        return ResponseEntity.badRequest().body(new CommonResponse("failed", ex.getMessage()));
    }


}
