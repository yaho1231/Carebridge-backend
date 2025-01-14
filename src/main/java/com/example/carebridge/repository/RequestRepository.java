package com.example.carebridge.repository;

import com.example.carebridge.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    Request findByRequestId(Integer requestId);

    List<Request> findByMedicalStaffId(Integer medicalStaffId);
}
