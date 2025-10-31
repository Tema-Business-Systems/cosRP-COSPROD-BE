package com.transport.tracking.repository;


import com.transport.tracking.model.DocDs;
import com.transport.tracking.model.SorderList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SorderListRepository extends CrudRepository<SorderList, Integer> {


    public List<SorderList> findAll();


    @Query(value="select top 1 * from COSPROD.X10CSOH c where c.XTSOHNUM_0 = ?1 ORDER BY c.XTLINENUM_0 DESC ",nativeQuery = true)
    public SorderList getLatestLineNobyOrderNo(String docnum);

    @Query(value="select  * from COSPROD.X10CSOH c where c.XTVRNUM_0 = ?1  AND XTPTHNUM_0 = '' ",nativeQuery = true)
    public List<SorderList> getOpenDocsSObyRouteCode(String vrnum);

    @Query(value="select  top 1 * from COSPROD.X10CSOH c where c.XTVRNUM_0 = ?1 AND c.XTSOHNUM_0 = ?2  AND XTPTHNUM_0 = '' ",nativeQuery = true)
    public SorderList getOpenDocsbyRouteCodeAndSO(String vrnum, String docnum);
}
