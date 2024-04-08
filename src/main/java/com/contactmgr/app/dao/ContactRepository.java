package com.contactmgr.app.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactmgr.app.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// Pagination
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> allContactByUser(@Param("userId") int userId, Pageable pageable);
}
