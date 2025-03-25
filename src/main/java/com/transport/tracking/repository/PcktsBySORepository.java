package com.transport.tracking.repository;


import com.transport.tracking.model.PcktsBySO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PcktsBySORepository extends CrudRepository<PcktsBySO, Integer> {


    public List<PcktsBySO> findAll();

    public List<PcktsBySO> findBySorder(String sorder);
}
