package com.haru.SwipeStyle.Repository;

import com.haru.SwipeStyle.Entities.Clothing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SwipeStyleRepo extends JpaRepository<Clothing,Long>, JpaSpecificationExecutor<Clothing> {
}
