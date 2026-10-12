package in.divyansh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.divyansh.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
	
     public Optional<User> findByEmail(String email);
     public boolean existsByEmail(String email);
     public List<User> findByIsActiveTrue();
     
     @Query("SELECT u FROM User u WHERE (LOWER(u.fullName) like LOWER(CONCAT('%',:keyword,'%'))"+
             "OR LOWER(u.email) like LOWER(CONCAT('%',:keyword,'%')))")
     public List<User> searchByNameOrEmail(@Param("keyword")String keyword);
}
