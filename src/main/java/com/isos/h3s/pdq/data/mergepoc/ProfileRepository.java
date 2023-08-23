package com.isos.h3s.pdq.data.mergepoc;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.isos.h3s.pdq.data.mergepoc.Profile;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ProfileRepository extends CrudRepository<Profile, Integer> {
//public interface ProfileRepository extends PagingAndSortingRepository<Profile, Integer> {

}
