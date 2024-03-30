package com.contactmgr.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contactmgr.app.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
