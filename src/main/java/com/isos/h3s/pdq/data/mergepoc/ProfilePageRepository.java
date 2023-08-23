package com.isos.h3s.pdq.data.mergepoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.isos.h3s.pdq.data.mergepoc.Profile;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ProfilePageRepository extends PagingAndSortingRepository<Profile, Integer> {
	
	@Query("SELECT p.id, p.firstName, p.lastName, p.employeeId, p.companyId, p.Phone, p.Email, p.employeeId, p.ttProfileId from Profile p where p.companyId = :id ")
    Page<Profile> findByCompanyId(@Param("id") int id, Pageable pageable);

}
