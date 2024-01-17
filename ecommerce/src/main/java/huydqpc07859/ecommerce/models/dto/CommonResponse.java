package huydqpc07859.ecommerce.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {
    private String status;
    private Object data;
    private Object message;

    public CommonResponse(Object data){
        this.status = "ok";
        this.message = "";
        this.data = data;
    }

    public CommonResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}
