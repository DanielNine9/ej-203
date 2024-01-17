package huydqpc07859.ecommerce.repositories;

import huydqpc07859.ecommerce.models.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository  extends JpaRepository<UserInfo, Long> {
}
