package com.raison.timesheet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.raison.timesheet.Entity.UserCredential;
@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long>{
    UserCredential findByUsername(String username);

}
