package huydqpc07859.ecommerce.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderRequest {
    private String id;
    private String email;
    private String fullName;
    private String imageURL;
    private String provider;
}
