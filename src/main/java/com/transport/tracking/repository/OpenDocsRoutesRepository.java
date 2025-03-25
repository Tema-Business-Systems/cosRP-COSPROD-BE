package com.transport.tracking.repository;

import com.transport.tracking.model.OpenDocsRoutes;
import com.transport.tracking.model.Trip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;


public interface OpenDocsRoutesRepository extends CrudRepository<OpenDocsRoutes, String> {

    public List<OpenDocsRoutes> findBySite(String site);

    public List<OpenDocsRoutes> findBySiteAndDocdate(String site, Date date);



    @Query(value="select * from COSPILOT.XTMSG2ADDDOCS c where c.SITE IN (?1) and (c.DOCDATE between ?2 AND ?3)",nativeQuery = true)
    public List<OpenDocsRoutes> getOpenDocsRouteBySiteAndDocdateRange(List<String> site, Date sdate, Date edate);



}
