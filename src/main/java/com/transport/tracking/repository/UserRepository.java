package com.transport.tracking.repository;


import com.transport.tracking.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    public User findByXusrnameAndXpswd(String userName, String password);

    public User findByXloginAndXpswdAndXact(String userName, String password, int x);
}
